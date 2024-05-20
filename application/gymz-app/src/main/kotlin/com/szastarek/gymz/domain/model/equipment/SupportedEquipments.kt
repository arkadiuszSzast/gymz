package com.szastarek.gymz.domain.model.equipment

import dev.cerbos.sdk.builders.Resource
import kotlinx.serialization.Serializable

@Serializable
data class SupportedEquipments(
    val equipments: List<Equipment>,
) {
    companion object {
        val resource: Resource = Resource.newInstance("supported-equipments:object")
    }
}
