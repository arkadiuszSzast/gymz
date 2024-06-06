package com.szastarek.gymz.adapter.mongo.workout

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
@SerialName("WorkoutItem")
sealed interface MongoWorkoutItem {
    fun toDomain(): WorkoutItem

    companion object {
        fun fromDomain(workoutItem: WorkoutItem) = when (workoutItem) {
            is WorkoutBreak -> MongoWorkoutBreak.fromDomain(workoutItem)
            is WorkoutSelfWeightExercise -> MongoWorkoutSelfWeightExercise.fromDomain(workoutItem)
            is WorkoutWeightBasedExercise -> MongoWorkoutWeightBasedExercise.fromDomain(workoutItem)
        }
    }
}

@Serializable
@SerialName("WorkoutBreak")
data class MongoWorkoutBreak(val duration: Duration) : MongoWorkoutItem {
    companion object {
        fun fromDomain(workoutBreak: WorkoutBreak) = MongoWorkoutBreak(duration = workoutBreak.duration)
    }

    override fun toDomain(): WorkoutItem = WorkoutBreak(duration)
}

@Serializable
@SerialName("WorkoutSelfWeightExercise")
data class MongoWorkoutSelfWeightExercise(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
) : MongoWorkoutItem {

    companion object {
        fun fromDomain(workoutSelfWeightExercise: WorkoutSelfWeightExercise) = MongoWorkoutSelfWeightExercise(
            exerciseId = workoutSelfWeightExercise.exerciseId,
            targetRepeats = workoutSelfWeightExercise.targetRepeats,
        )
    }

    override fun toDomain(): WorkoutItem = WorkoutSelfWeightExercise(
        exerciseId = exerciseId,
        targetRepeats = targetRepeats,
    )
}

@Serializable
@SerialName("WorkoutWeightBasedExercise")
data class MongoWorkoutWeightBasedExercise(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
    val weight: Weight,
) : MongoWorkoutItem {

    companion object {
        fun fromDomain(workoutWeightBasedExercise: WorkoutWeightBasedExercise) = MongoWorkoutWeightBasedExercise(
            exerciseId = workoutWeightBasedExercise.exerciseId,
            targetRepeats = workoutWeightBasedExercise.targetRepeats,
            weight = workoutWeightBasedExercise.weight,
        )
    }

    override fun toDomain(): WorkoutItem = WorkoutWeightBasedExercise(
        exerciseId = exerciseId,
        targetRepeats = targetRepeats,
        weight = weight,
    )
}
