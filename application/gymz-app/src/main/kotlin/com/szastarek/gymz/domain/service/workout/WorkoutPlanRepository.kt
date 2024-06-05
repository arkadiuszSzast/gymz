package com.szastarek.gymz.domain.service.workout

import com.szastarek.gymz.domain.model.workout.WorkoutPlan
import com.szastarek.gymz.shared.SaveResult

interface WorkoutPlanRepository {
    suspend fun save(workoutPlan: WorkoutPlan): SaveResult
}
