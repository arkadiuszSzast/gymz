package com.szastarek.gymz.domain.service.user.equipment.command.handler

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipments
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipmentsEvent
import com.szastarek.gymz.domain.service.equipment.SupportedEquipmentRepository
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.check
import com.szastarek.gymz.domain.service.user.ensure
import com.szastarek.gymz.domain.service.user.equipment.UserOwnedEquipmentsRepository
import com.szastarek.gymz.domain.service.user.equipment.command.ChangeUserOwnedEquipmentCommand
import com.szastarek.gymz.domain.service.user.equipment.command.ChangeUserOwnedEquipmentCommandError
import com.szastarek.gymz.domain.service.user.equipment.command.ChangeUserOwnedEquipmentCommandResult
import com.szastarek.gymz.domain.service.user.equipment.command.ChangeUserOwnedEquipmentCommandSuccessResult
import com.szastarek.gymz.event.store.model.ExpectedRevision
import com.szastarek.gymz.event.store.service.EventStoreWriteClient
import com.szastarek.gymz.event.store.service.appendToStream
import com.trendyol.kediatr.CommandWithResultHandler
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class ChangeUserOwnedEquipmentCommandHandler(
    private val accessManager: AccessManager,
    private val userOwnedEquipmentsRepository: UserOwnedEquipmentsRepository,
    private val supportedEquipmentRepository: SupportedEquipmentRepository,
    private val eventStoreWriteClient: EventStoreWriteClient,
) : CommandWithResultHandler<ChangeUserOwnedEquipmentCommand, ChangeUserOwnedEquipmentCommandResult> {
    override suspend fun handle(command: ChangeUserOwnedEquipmentCommand): ChangeUserOwnedEquipmentCommandResult = either {
        val (userContext, equipmentsIds) = command
        val supportedEquipments = supportedEquipmentRepository.get()
        val equipments = supportedEquipments.tryFind(equipmentsIds)
        val foundEquipmentsIds = equipments.map { it.id }
        val notFoundEquipments = equipmentsIds.filter { it !in foundEquipmentsIds }

        ensure(notFoundEquipments.isEmpty()) {
            logger.warn { "Equipments[${notFoundEquipments.joinToString(",")}] not found." }
            ChangeUserOwnedEquipmentCommandError.EquipmentNotFound.nel()
        }

        val userOwnedEquipments = userOwnedEquipmentsRepository.find(userContext.userId).getOrNull()
        val event = when (userOwnedEquipments) {
            null -> {
                accessManager.check(userContext, UserOwnedEquipments.resource, Action.create).ensure()
                UserOwnedEquipments.initialize(userContext.userId, equipments)
            }
            else -> {
                accessManager.check(userContext, userOwnedEquipments, Action.update).ensure()
                userOwnedEquipments.change(equipments)
            }
        }

        eventStoreWriteClient.appendToStream<UserOwnedEquipmentsEvent>(event, ExpectedRevision.Any)

        ChangeUserOwnedEquipmentCommandSuccessResult
    }
}
