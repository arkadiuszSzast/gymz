package com.szastarek.gymz.support

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplate
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplateId
import com.szastarek.gymz.domain.service.workout.WeeklyWorkoutTemplateRepository
import com.szastarek.gymz.shared.SaveResult
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.page.PageTotalElements
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryWeeklyWorkoutTemplateRepository : WeeklyWorkoutTemplateRepository {
    private val db = mutableMapOf<WeeklyWorkoutTemplateId, WeeklyWorkoutTemplate>()
    private val mutex = Mutex()

    override suspend fun save(workoutTemplate: WeeklyWorkoutTemplate): SaveResult = mutex.withLock {
        db[workoutTemplate.id] = workoutTemplate
        return SaveResult.Ok
    }

    override suspend fun findById(id: WeeklyWorkoutTemplateId): WeeklyWorkoutTemplate? = mutex.withLock {
        db[id]
    }

    override suspend fun findAll(pageQueryParameters: PageQueryParameters): Page<WeeklyWorkoutTemplate> = mutex.withLock {
        val (pageSize, pageNumber) = pageQueryParameters

        val exercises = db.values.toList().sortedBy { it.id.value }
        val total = db.values.size

        Page(
            data = exercises.drop(pageQueryParameters.offset).take(pageSize.value),
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
