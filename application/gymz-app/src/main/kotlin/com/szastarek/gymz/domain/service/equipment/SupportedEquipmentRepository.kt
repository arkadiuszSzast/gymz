package com.szastarek.gymz.domain.service.equipment

import com.szastarek.gymz.domain.model.equipment.SupportedEquipments

interface SupportedEquipmentRepository {
    suspend fun get(): SupportedEquipments
}
