package com.szastarek.gymz.domain.model.equipment

import dev.cerbos.sdk.builders.Resource

data class SupportedEquipments(
    val equipments: List<Equipment>
) {
    companion object {
        val resource =  Resource.newInstance("supported-equipments:object")
    }
}