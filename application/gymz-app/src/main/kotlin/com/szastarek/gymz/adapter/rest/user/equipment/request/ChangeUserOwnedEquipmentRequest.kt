package com.szastarek.gymz.adapter.rest.user.equipment.request

import com.szastarek.gymz.domain.model.equipment.EquipmentId
import kotlinx.serialization.Serializable

@Serializable
data class ChangeUserOwnedEquipmentRequest(
    val equipmentsIds: List<EquipmentId>,
)
