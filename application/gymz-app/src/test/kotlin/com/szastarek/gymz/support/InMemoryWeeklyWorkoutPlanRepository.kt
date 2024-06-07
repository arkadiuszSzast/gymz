package com.szastarek.gymz.support

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlan
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlanId
import com.szastarek.gymz.domain.service.workout.WeeklyWorkoutPlanRepository
import com.szastarek.gymz.shared.SaveResult
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.page.PageTotalElements
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryWeeklyWorkoutPlanRepository : WeeklyWorkoutPlanRepository {
    private val db = mutableMapOf<WeeklyWorkoutPlanId, WeeklyWorkoutPlan>()
    private val mutex = Mutex()

    override suspend fun save(workoutPlan: WeeklyWorkoutPlan): SaveResult = mutex.withLock {
        db[workoutPlan.id] = workoutPlan
        return SaveResult.Ok
    }

    override suspend fun findById(id: WeeklyWorkoutPlanId): WeeklyWorkoutPlan? = mutex.withLock {
        db[id]
    }

    override suspend fun findAll(pageQueryParameters: PageQueryParameters): Page<WeeklyWorkoutPlan> = mutex.withLock {
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
