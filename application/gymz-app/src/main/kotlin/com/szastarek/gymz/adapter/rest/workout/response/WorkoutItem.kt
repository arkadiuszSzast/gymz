package com.szastarek.gymz.adapter.rest.workout.response

import com.szastarek.gymz.adapter.rest.exercise.response.GymExerciseResponse
import com.szastarek.gymz.domain.model.weight.Weight
import com.szastarek.gymz.domain.model.workout.WorkoutBreak
import com.szastarek.gymz.domain.model.workout.WorkoutItem
import com.szastarek.gymz.domain.model.workout.WorkoutSelfWeightExercise
import com.szastarek.gymz.domain.model.workout.WorkoutWeightBasedExercise
import com.szastarek.gymz.file.storage.FileUrlResolver
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
sealed interface WorkoutItemResponseModel {
    companion object {
        fun fromDomain(workoutItem: WorkoutItem, urlResolver: FileUrlResolver): WorkoutItemResponseModel = when (workoutItem) {
            is WorkoutBreak -> WorkoutBreakResponseModel.fromDomain(workoutItem)
            is WorkoutSelfWeightExercise -> WorkoutSelfWeightExerciseResponseModel.fromDomain(workoutItem, urlResolver)
            is WorkoutWeightBasedExercise -> WorkoutWeightBasedExerciseResponseModel.fromDomain(workoutItem, urlResolver)
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
    val exercise: GymExerciseResponse,
    val targetRepeats: UInt,
) : WorkoutItemResponseModel {
    companion object {
        fun fromDomain(workoutSelfWeightExercise: WorkoutSelfWeightExercise, urlResolver: FileUrlResolver): WorkoutSelfWeightExerciseResponseModel =
            WorkoutSelfWeightExerciseResponseModel(
                exercise = GymExerciseResponse.from(workoutSelfWeightExercise.exercise, urlResolver),
                targetRepeats = workoutSelfWeightExercise.targetRepeats,
            )
    }
}

@Serializable
@SerialName("WorkoutWeightBasedExercise")
data class WorkoutWeightBasedExerciseResponseModel(
    val exercise: GymExerciseResponse,
    val targetRepeats: UInt,
    val weight: Weight,
) : WorkoutItemResponseModel {
    companion object {
        fun fromDomain(workoutWeightBasedExercise: WorkoutWeightBasedExercise, urlResolver: FileUrlResolver): WorkoutWeightBasedExerciseResponseModel =
            WorkoutWeightBasedExerciseResponseModel(
                exercise = GymExerciseResponse.from(workoutWeightBasedExercise.exercise, urlResolver),
                targetRepeats = workoutWeightBasedExercise.targetRepeats,
                weight = workoutWeightBasedExercise.weight,
            )
    }
}
