package com.szastarek.gymz.domain.service.workout

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlan
import com.szastarek.gymz.shared.SaveResult
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters

interface WeeklyWorkoutPlanRepository {
    suspend fun findAll(pageQueryParameters: PageQueryParameters): Page<WeeklyWorkoutPlan>
    suspend fun save(workoutPlan: WeeklyWorkoutPlan): SaveResult
}
