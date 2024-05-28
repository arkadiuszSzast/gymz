package com.szastarek.gymz.support

import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.service.exercise.GymExerciseRepository
import com.szastarek.gymz.shared.SaveResult
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.page.PageTotalElements
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryGymExerciseRepository : GymExerciseRepository {
    private val db = mutableMapOf<GymExerciseId, GymExercise>()
    private val mutex = Mutex()

    override suspend fun save(exercise: GymExercise): SaveResult = mutex.withLock {
        db[exercise.id] = exercise
        return SaveResult.Ok
    }

    override suspend fun findAll(pageQueryParameters: PageQueryParameters): Page<GymExercise> = mutex.withLock {
        val (pageSize, pageNumber) = pageQueryParameters

        val exercises = db.values.toList()
        val total = db.values.size

        Page(
            data = exercises.drop(pageQueryParameters.offset),
            totalElements = PageTotalElements(exercises.size.toLong()),
            pageSize = pageSize,
            pageNumber = pageNumber,
            isLastPage = total <= pageQueryParameters.offset + pageSize.value,
        )
    }

    suspend fun clear() = mutex.withLock {
        db.clear()
    }
}
