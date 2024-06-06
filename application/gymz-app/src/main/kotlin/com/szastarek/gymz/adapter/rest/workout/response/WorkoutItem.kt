package com.szastarek.gymz.adapter.rest.workout.response

import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.weight.Weight
import com.szastarek.gymz.domain.model.workout.WorkoutBreak
import com.szastarek.gymz.domain.model.workout.WorkoutItem
import com.szastarek.gymz.domain.model.workout.WorkoutSelfWeightExercise
import com.szastarek.gymz.domain.model.workout.WorkoutWeightBasedExercise
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
sealed interface WorkoutItemResponseModel {
    companion object {
        fun fromDomain(workoutItem: WorkoutItem): WorkoutItemResponseModel = when (workoutItem) {
            is WorkoutBreak -> WorkoutBreakResponseModel.fromDomain(workoutItem)
            is WorkoutSelfWeightExercise -> WorkoutSelfWeightExerciseResponseModel.fromDomain(workoutItem)
            is WorkoutWeightBasedExercise -> WorkoutWeightBasedExerciseResponseModel.fromDomain(workoutItem)
        }
    }
}

@Serializable
@SerialName("WorkoutBreak")
data class WorkoutBreakResponseModel(val duration: Duration) : WorkoutItemResponseModel {
    companion object {
        fun fromDomain(workoutBreak: WorkoutBreak): WorkoutBreakResponseModel =
            WorkoutBreakResponseModel(workoutBreak.duration)
    }
}

@Serializable
@SerialName("WorkoutSelfWeightExercise")
data class WorkoutSelfWeightExerciseResponseModel(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
) : WorkoutItemResponseModel {
    companion object {
        fun fromDomain(workoutSelfWeightExercise: WorkoutSelfWeightExercise): WorkoutSelfWeightExerciseResponseModel =
            WorkoutSelfWeightExerciseResponseModel(
                exerciseId = workoutSelfWeightExercise.exerciseId,
                targetRepeats = workoutSelfWeightExercise.targetRepeats,
            )
    }
}

@Serializable
@SerialName("WorkoutWeightBasedExercise")
data class WorkoutWeightBasedExerciseResponseModel(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
    val weight: Weight,
) : WorkoutItemResponseModel {
    companion object {
        fun fromDomain(workoutWeightBasedExercise: WorkoutWeightBasedExercise): WorkoutWeightBasedExerciseResponseModel =
            WorkoutWeightBasedExerciseResponseModel(
                exerciseId = workoutWeightBasedExercise.exerciseId,
                targetRepeats = workoutWeightBasedExercise.targetRepeats,
                weight = workoutWeightBasedExercise.weight,
            )
    }
}
