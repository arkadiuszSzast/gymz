package com.szastarek.gymz.domain.model.equipment

import dev.cerbos.sdk.builders.Resource
import kotlinx.serialization.Serializable

@Serializable
data class SupportedEquipments(
    val equipments: List<Equipment>,
) {

    fun tryFind(equipmentsIds: List<EquipmentId>) = equipmentsIds.mapNotNull { find(it) }

    fun find(equipmentId: EquipmentId): Equipment? =
        equipments.find { it.id == equipmentId }

    companion object {
        val resource: Resource = Resource.newInstance("supported-equipments:object")
    }
}
