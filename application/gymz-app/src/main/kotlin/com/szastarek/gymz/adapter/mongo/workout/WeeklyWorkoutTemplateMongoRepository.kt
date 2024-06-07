package com.szastarek.gymz.adapter.mongo.workout

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplate
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplateId
import com.szastarek.gymz.domain.service.workout.WeeklyWorkoutTemplateRepository
import com.szastarek.gymz.shared.SaveResult
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.page.PageTotalElements
import kotlinx.coroutines.flow.toList

class WeeklyWorkoutTemplateMongoRepository(
    private val collection: MongoCollection<MongoWeeklyWorkoutTemplate>,
) : WeeklyWorkoutTemplateRepository {

    companion object {
        const val COLLECTION_NAME = "weekly-workout-templates"
    }

    override suspend fun findAll(pageQueryParameters: PageQueryParameters): Page<WeeklyWorkoutTemplate> {
        val (pageSize, pageNumber) = pageQueryParameters
        val total = collection.countDocuments()
        val exercises = collection.find()
            .sort(Sorts.ascending("_id"))
            .skip(pageQueryParameters.offset).limit(pageSize.value).toList().map { it.toDomain() }
        return Page(
            data = exercises,
            totalElements = PageTotalElements(total),
            pageSize = pageSize,
            pageNumber = pageNumber,
            isLastPage = total <= pageQueryParameters.offset + pageSize.value,
        )
    }

    override suspend fun findById(id: WeeklyWorkoutTemplateId): WeeklyWorkoutTemplate? =
        collection.find(Filters.eq(id.value)).limit(1).toList().firstOrNull()?.toDomain()

    override suspend fun save(workoutTemplate: WeeklyWorkoutTemplate): SaveResult = runCatching {
        collection.insertOne(MongoWeeklyWorkoutTemplate.fromDomain(workoutTemplate))
        SaveResult.Ok
    }.getOrElse { SaveResult.UnknownError(it) }
}
