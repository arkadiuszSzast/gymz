package com.szastarek.gymz.adapter.rest.user.equipment.response

import com.szastarek.gymz.adapter.rest.equipment.response.EquipmentListItem
import kotlinx.serialization.Serializable

@Serializable
data class UserOwnedEquipmentResponse(
    val equipments: List<EquipmentListItem>,
)
