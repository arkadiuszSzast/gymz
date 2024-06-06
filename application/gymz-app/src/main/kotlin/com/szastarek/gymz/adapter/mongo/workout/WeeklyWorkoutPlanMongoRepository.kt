package com.szastarek.gymz.adapter.mongo.workout

import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlan
import com.szastarek.gymz.domain.service.workout.WeeklyWorkoutPlanRepository
import com.szastarek.gymz.shared.SaveResult
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.page.PageTotalElements
import kotlinx.coroutines.flow.toList

class WeeklyWorkoutPlanMongoRepository(
    private val collection: MongoCollection<MongoWeeklyWorkoutPlan>,
) : WeeklyWorkoutPlanRepository {

    companion object {
        const val COLLECTION_NAME = "weekly-workout-plans"
    }

    override suspend fun findAll(pageQueryParameters: PageQueryParameters): Page<WeeklyWorkoutPlan> {
        val (pageSize, pageNumber) = pageQueryParameters
        val total = collection.countDocuments()
        val exercises = collection.find()
            .skip(pageQueryParameters.offset).limit(pageSize.value).toList().map { it.toDomain() }
        return Page(
            data = exercises,
            totalElements = PageTotalElements(total),
            pageSize = pageSize,
            pageNumber = pageNumber,
            isLastPage = total <= pageQueryParameters.offset + pageSize.value,
        )
    }

    override suspend fun save(workoutPlan: WeeklyWorkoutPlan): SaveResult = runCatching {
        collection.insertOne(MongoWeeklyWorkoutPlan.fromDomain(workoutPlan))
        SaveResult.Ok
    }.getOrElse { SaveResult.UnknownError(it) }
}
