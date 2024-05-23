package com.szastarek.gymz.domain.service.user.equipment.query.handler

import arrow.core.getOrElse
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipments
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.check
import com.szastarek.gymz.domain.service.user.ensure
import com.szastarek.gymz.domain.service.user.equipment.UserOwnedEquipmentsRepository
import com.szastarek.gymz.domain.service.user.equipment.query.UserOwnedEquipmentQuery
import com.trendyol.kediatr.QueryHandler

class UserOwnedEquipmentQueryHandler(
    private val accessManager: AccessManager,
    private val userOwnedEquipmentsRepository: UserOwnedEquipmentsRepository,
) : QueryHandler<UserOwnedEquipmentQuery, UserOwnedEquipments> {
    override suspend fun handle(query: UserOwnedEquipmentQuery): UserOwnedEquipments {
        val userContext = query.userContext
        val result = userOwnedEquipmentsRepository.find(userContext.userId)
            .getOrElse { UserOwnedEquipments.empty(userContext.userId) }
        accessManager.check(userContext, result, Action.read).ensure()

        return result
    }
}
