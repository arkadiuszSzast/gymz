package com.szastarek.gymz.adapter.mongo.migration

import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.MongoDatabase
import com.szastarek.gymz.adapter.mongo.equipment.MongoEquipment
import com.szastarek.gymz.adapter.mongo.equipment.SupportedEquipmentMongoRepository
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.file.storage.FileStorage
import com.szastarek.gymz.file.storage.model.FileBasePath
import com.szastarek.gymz.file.storage.model.FileCandidate
import com.szastarek.gymz.file.storage.model.FileKey
import com.szastarek.gymz.shared.i18n.TranslationKey
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import io.mongock.driver.mongodb.reactive.util.SubscriberSync
import kotlinx.coroutines.runBlocking

@ChangeUnit(id = "supported_equipments", order = "001", author = "szastarek", systemVersion = "1.0.0")
class SupportedEquipmentsMigration {
    @Execution
    fun migrationMethod(mongoDatabase: MongoDatabase, fileStorage: FileStorage, clientSession: ClientSession) {
        runBlocking {
            val subscriber: SubscriberSync<InsertManyResult> = MongoSubscriberSync()
            val id = EquipmentId("af292dea-b37e-4a0a-a4bb-b41174cbaeeb")
            val fileCandidate = FileCandidate.ByteFileCandidate(
                FileBasePath("equipments"),
                FileKey(id.value),
                ClassLoader.getSystemResource("./equipment-images/dumbbell.png").readBytes(),
            )
            val uploadedFile = fileStorage.uploadPublic(fileCandidate)

            mongoDatabase.getCollection(SupportedEquipmentMongoRepository.COLLECTION_NAME, MongoEquipment::class.java)
                .insertMany(
                    clientSession,
                    listOf(MongoEquipment(id, TranslationKey("equipment.dumbbells"), uploadedFile)),
                ).subscribe(subscriber)

            subscriber.await()
        }
    }

    @RollbackExecution
    fun rollback(mongoDatabase: MongoDatabase) {
        runBlocking {
            val subscriber: SubscriberSync<DeleteResult> = MongoSubscriberSync()

            mongoDatabase.getCollection(SupportedEquipmentMongoRepository.COLLECTION_NAME, MongoEquipment::class.java)
                .deleteMany(Filters.eq("_id", EquipmentId("af292dea-b37e-4a0a-a4bb-b41174cbaeeb")))
                .subscribe(subscriber)

            subscriber.await()
        }
    }
}
