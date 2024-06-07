package com.szastarek.gymz.adapter.mongo.workout

import com.szastarek.gymz.adapter.mongo.exercise.MongoGymExercise
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
    val exercise: MongoGymExercise,
    val targetRepeats: UInt,
) : MongoWorkoutItem {

    companion object {
        fun fromDomain(workoutSelfWeightExercise: WorkoutSelfWeightExercise) = MongoWorkoutSelfWeightExercise(
            exercise = MongoGymExercise.fromDomain(workoutSelfWeightExercise.exercise),
            targetRepeats = workoutSelfWeightExercise.targetRepeats,
        )
    }

    override fun toDomain(): WorkoutItem = WorkoutSelfWeightExercise(
        exercise = exercise.toDomain(),
        targetRepeats = targetRepeats,
    )
}

@Serializable
@SerialName("WorkoutWeightBasedExercise")
data class MongoWorkoutWeightBasedExercise(
    val exercise: MongoGymExercise,
    val targetRepeats: UInt,
    val weight: Weight,
) : MongoWorkoutItem {

    companion object {
        fun fromDomain(workoutWeightBasedExercise: WorkoutWeightBasedExercise) = MongoWorkoutWeightBasedExercise(
            exercise = MongoGymExercise.fromDomain(workoutWeightBasedExercise.exercise),
            targetRepeats = workoutWeightBasedExercise.targetRepeats,
            weight = workoutWeightBasedExercise.weight,
        )
    }

    override fun toDomain(): WorkoutItem = WorkoutWeightBasedExercise(
        exercise = exercise.toDomain(),
        targetRepeats = targetRepeats,
        weight = weight,
    )
}
