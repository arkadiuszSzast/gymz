package com.szastarek.gymz.domain.service.workout.command.handler

import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.weight.Weight
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.datetime.DayOfWeek
import kotlin.time.Duration

data class WeeklyWorkoutEntryCommandModel(
    val day: DayOfWeek,
    val items: List<WorkoutItemCommandModel>,
    val name: TranslationKey,
)

sealed interface WorkoutItemCommandModel

data class WorkoutBreakCommandModel(val duration: Duration) : WorkoutItemCommandModel

data class WorkoutSelfWeightExerciseCommandModel(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
) : WorkoutItemCommandModel

data class WorkoutWeightBasedExerciseCommandModel(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
    val weight: Weight,
) : WorkoutItemCommandModel
