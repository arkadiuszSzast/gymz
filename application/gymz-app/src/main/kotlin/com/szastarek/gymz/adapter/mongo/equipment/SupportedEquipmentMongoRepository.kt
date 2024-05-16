package com.szastarek.gymz.adapter.mongo.equipment

import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.szastarek.gymz.domain.model.equipment.SupportedEquipments
import com.szastarek.gymz.domain.service.equipment.SupportedEquipmentRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

class SupportedEquipmentMongoRepository(
    private val collection: MongoCollection<MongoEquipment>,
) : SupportedEquipmentRepository {

    companion object {
        const val COLLECTION_NAME = "supported-equipments"
    }

    override suspend fun get(): SupportedEquipments {
        val equipments = collection.find().map { it.toDomain() }.toList()
        return SupportedEquipments(equipments)
    }
}
