package com.szastarek.gymz.domain.service.equipment.query.handler

import com.szastarek.gymz.domain.model.equipment.SupportedEquipments
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.Query

data class SupportedEquipmentsQuery(val userContext: UserContext) : Query<SupportedEquipmentsQueryResult>

data class SupportedEquipmentsQueryResult(val supportedEquipments: SupportedEquipments)