package com.szastarek.gymz.adapter.mongo.equipment

import com.mongodb.kotlin.client.MongoCollection
import com.szastarek.gymz.domain.model.equipment.SupportedEquipments
import com.szastarek.gymz.domain.service.equipment.SupportedEquipmentRepository

class SupportedEquipmentMongoRepository(
    private val collection: MongoCollection<MongoEquipment>
) : SupportedEquipmentRepository {
    override suspend fun get(): SupportedEquipments {
        val equipments = collection.find().map { it.toDomain() }.toList()
        return SupportedEquipments(equipments)
    }
}