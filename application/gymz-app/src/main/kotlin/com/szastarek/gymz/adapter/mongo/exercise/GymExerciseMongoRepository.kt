package com.szastarek.gymz.adapter.mongo.exercise

import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.service.exercise.GymExerciseRepository
import com.szastarek.gymz.shared.SaveResult

class GymExerciseMongoRepository(
    private val collection: MongoCollection<MongoGymExercise>,
) : GymExerciseRepository {

    companion object {
        const val COLLECTION_NAME = "gym-exercises"
    }

    override suspend fun save(exercise: GymExercise): SaveResult = runCatching {
        collection.insertOne(MongoGymExercise.fromDomain(exercise))
        SaveResult.Ok
    }.getOrElse { SaveResult.UnknownError(it) }
}
