package com.szastarek.gymz.adapter.mongo.workout

import com.szastarek.gymz.domain.model.workout.Challenge
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlan
import com.szastarek.gymz.domain.model.workout.WorkoutPlan
import com.szastarek.gymz.domain.model.workout.WorkoutPlanId
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface MongoWorkoutPlan {
    val id: WorkoutPlanId
    val name: TranslationKey
    val description: TranslationKey

    fun toDomain(): WorkoutPlan

    companion object {
        fun fromDomain(workoutPlan: WorkoutPlan): MongoWorkoutPlan = when (workoutPlan) {
            is Challenge -> MongoChallenge.fromDomain(workoutPlan)
            is WeeklyWorkoutPlan -> MongoWeeklyWorkoutPlan.fromDomain(workoutPlan)
        }
    }
}
