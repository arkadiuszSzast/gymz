package com.szastarek.gymz.domain.model.workout

import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.weight.Weight
import kotlin.time.Duration

sealed interface WorkoutItem

data class WorkoutBreak(val duration: Duration) : WorkoutItem

data class WorkoutSelfWeightExercise(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
) : WorkoutItem

data class WorkoutWeightBasedExercise(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
    val weight: Weight,
) : WorkoutItem
