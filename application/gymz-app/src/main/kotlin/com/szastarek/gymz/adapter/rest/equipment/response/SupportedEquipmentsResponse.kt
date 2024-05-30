package com.szastarek.gymz.adapter.rest.equipment.response

import kotlinx.serialization.Serializable

@Serializable
data class SupportedEquipmentsResponse(val equipments: List<EquipmentListItem>)
