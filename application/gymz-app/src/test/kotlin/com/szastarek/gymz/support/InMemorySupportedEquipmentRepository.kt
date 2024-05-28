package com.szastarek.gymz.support

import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.domain.model.equipment.SupportedEquipments
import com.szastarek.gymz.domain.service.equipment.SupportedEquipmentRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemorySupportedEquipmentRepository : SupportedEquipmentRepository {
    private val db = mutableMapOf<EquipmentId, Equipment>()
    private val mutex = Mutex()

    override suspend fun get(): SupportedEquipments = mutex.withLock {
        SupportedEquipments(db.values.toList())
    }

    suspend fun add(equipment: Equipment) = mutex.withLock {
        db[equipment.id] = equipment
    }

    suspend fun addAll(equipments: List<Equipment>) = mutex.withLock {
        equipments.forEach { db[it.id] = it }
    }

    suspend fun clear() = mutex.withLock {
        db.clear()
    }
}
