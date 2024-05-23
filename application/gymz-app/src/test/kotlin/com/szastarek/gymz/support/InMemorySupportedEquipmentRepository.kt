package com.szastarek.gymz.support

import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.equipment.SupportedEquipments
import com.szastarek.gymz.domain.service.equipment.SupportedEquipmentRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemorySupportedEquipmentRepository : SupportedEquipmentRepository {
    private val db = mutableListOf<Equipment>()
    private val mutex = Mutex()

    override suspend fun get(): SupportedEquipments = mutex.withLock {
        SupportedEquipments(db.toList())
    }

    suspend fun add(equipment: Equipment) = mutex.withLock {
        db.add(equipment)
    }

    suspend fun addAll(equipments: List<Equipment>) = mutex.withLock {
        db.addAll(equipments)
    }

    suspend fun clear() = mutex.withLock {
        db.clear()
    }
}
