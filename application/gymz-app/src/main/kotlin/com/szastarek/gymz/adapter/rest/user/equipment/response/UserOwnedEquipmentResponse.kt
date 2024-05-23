package com.szastarek.gymz.adapter.rest.user.equipment.response

import com.szastarek.gymz.domain.model.equipment.Equipment
import kotlinx.serialization.Serializable

@Serializable
data class UserOwnedEquipmentResponse(
    val equipments: List<Equipment>,
)
