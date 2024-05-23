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
            val dumbbellId = EquipmentId("af292dea-b37e-4a0a-a4bb-b41174cbaeeb")
            val kettlebellsId = EquipmentId("eb03341b-2402-471f-b4d7-b252113e6d86")
            val dumbbellsFileCandidate = FileCandidate.ByteFileCandidate(
                FileBasePath("equipments"),
                FileKey(dumbbellId.value),
                ClassLoader.getSystemResource("./equipment-images/dumbbell.png").readBytes(),
            )
            val kettlebellsFileCandidate = FileCandidate.ByteFileCandidate(
                FileBasePath("equipments"),
                FileKey(kettlebellsId.value),
                ClassLoader.getSystemResource("./equipment-images/kettlebells.png").readBytes(),
            )
            val dumbbellsUploadedFile = fileStorage.uploadPublic(dumbbellsFileCandidate)
            val kettlebellsUploadedFile = fileStorage.uploadPublic(kettlebellsFileCandidate)

            mongoDatabase.getCollection(SupportedEquipmentMongoRepository.COLLECTION_NAME, MongoEquipment::class.java)
                .insertMany(
                    clientSession,
                    listOf(
                        MongoEquipment(dumbbellId, TranslationKey("equipment.dumbbells"), dumbbellsUploadedFile),
                        MongoEquipment(kettlebellsId, TranslationKey("equipment.kettlebells"), kettlebellsUploadedFile),
                    ),
                ).subscribe(subscriber)

            subscriber.await()
        }
    }

    @RollbackExecution
    fun rollback(mongoDatabase: MongoDatabase) {
        runBlocking {
            val subscriber: SubscriberSync<DeleteResult> = MongoSubscriberSync()

            mongoDatabase.getCollection(SupportedEquipmentMongoRepository.COLLECTION_NAME, MongoEquipment::class.java)
                .deleteMany(
                    Filters.`in`(
                        "_id",
                        listOf(
                            EquipmentId("af292dea-b37e-4a0a-a4bb-b41174cbaeeb"),
                            EquipmentId("eb03341b-2402-471f-b4d7-b252113e6d86"),
                        ),
                    ),
                )
                .subscribe(subscriber)

            subscriber.await()
        }
    }
}
