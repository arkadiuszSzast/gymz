package com.szastarek.gymz.adapter.mongo.workout

import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.szastarek.gymz.domain.model.workout.WorkoutPlan
import com.szastarek.gymz.domain.service.workout.WorkoutPlanRepository
import com.szastarek.gymz.shared.SaveResult

class WorkoutPlanMongoRepository(
    private val collection: MongoCollection<MongoWorkoutPlan>,
) : WorkoutPlanRepository {

    companion object {
        const val COLLECTION_NAME = "workout-plans"
    }

    override suspend fun save(workoutPlan: WorkoutPlan): SaveResult = runCatching {
        collection.insertOne(MongoWorkoutPlan.fromDomain(workoutPlan))
        SaveResult.Ok
    }.getOrElse { SaveResult.UnknownError(it) }
}
