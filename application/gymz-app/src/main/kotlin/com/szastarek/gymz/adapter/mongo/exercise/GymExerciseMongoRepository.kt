package com.szastarek.gymz.adapter.mongo.exercise

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.service.exercise.GymExerciseRepository
import com.szastarek.gymz.shared.SaveResult
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.page.PageTotalElements
import kotlinx.coroutines.flow.toList

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

    override suspend fun findById(id: GymExerciseId): GymExercise? =
        collection.find(Filters.eq(id.value)).limit(1).toList().firstOrNull()?.toDomain()

    override suspend fun findAll(pageQueryParameters: PageQueryParameters): Page<GymExercise> {
        val (pageSize, pageNumber) = pageQueryParameters
        val total = collection.countDocuments()
        val exercises = collection.find().skip(pageQueryParameters.offset).limit(pageSize.value).toList().map { it.toDomain() }
        return Page(
            data = exercises,
            totalElements = PageTotalElements(total),
            pageSize = pageSize,
            pageNumber = pageNumber,
            isLastPage = total <= pageQueryParameters.offset + pageSize.value,
        )
    }
}
