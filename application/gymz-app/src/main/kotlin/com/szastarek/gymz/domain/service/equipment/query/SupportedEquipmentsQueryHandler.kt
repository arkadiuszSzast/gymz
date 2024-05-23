package com.szastarek.gymz.domain.service.equipment.query

import com.szastarek.gymz.domain.model.equipment.SupportedEquipments
import com.szastarek.gymz.domain.service.equipment.SupportedEquipmentRepository
import com.szastarek.gymz.domain.service.equipment.query.handler.SupportedEquipmentsQuery
import com.szastarek.gymz.domain.service.equipment.query.handler.SupportedEquipmentsQueryResult
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.ensure
import com.trendyol.kediatr.QueryHandler

class SupportedEquipmentsQueryHandler(
    private val repository: SupportedEquipmentRepository,
    private val accessManager: AccessManager,
) : QueryHandler<SupportedEquipmentsQuery, SupportedEquipmentsQueryResult> {

    override suspend fun handle(query: SupportedEquipmentsQuery): SupportedEquipmentsQueryResult {
        accessManager.check(query.userContext, SupportedEquipments.resource, Action.read).ensure()
        return SupportedEquipmentsQueryResult(repository.get())
    }
}
