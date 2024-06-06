package com.szastarek.gymz.adapter.rest.workout.request

import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.weight.Weight
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutEntry
import com.szastarek.gymz.domain.model.workout.WorkoutBreak
import com.szastarek.gymz.domain.model.workout.WorkoutItem
import com.szastarek.gymz.domain.model.workout.WorkoutSelfWeightExercise
import com.szastarek.gymz.domain.model.workout.WorkoutWeightBasedExercise
import com.szastarek.gymz.domain.service.workout.command.AddWeeklyWorkoutPlanCommand
import com.szastarek.gymz.shared.i18n.TranslationKey
import com.szastarek.gymz.shared.security.UserContext
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class AddWeeklyWorkoutRequest(
    val name: TranslationKey,
    val description: TranslationKey,
    val entries: List<WeeklyWorkoutEntryRequestModel>,
) {
    fun toCommand(userContext: UserContext) = AddWeeklyWorkoutPlanCommand(
        userContext = userContext,
        name = name,
        description = description,
        entries = entries.map { it.toDomain() },
    )
}

@Serializable
data class WeeklyWorkoutEntryRequestModel(
    val day: DayOfWeek,
    val items: List<WorkoutItemRequestModel>,
    val name: TranslationKey,
) {
    fun toDomain() = WeeklyWorkoutEntry(
        day = day,
        items = items.map { it.toDomain() },
        name = name,
    )
}

@Serializable
sealed interface WorkoutItemRequestModel {
    fun toDomain(): WorkoutItem
}

@Serializable
@SerialName("WorkoutBreak")
data class WorkoutBreakRequestModel(val duration: Duration) : WorkoutItemRequestModel {
    override fun toDomain() = WorkoutBreak(duration)
}

@Serializable
@SerialName("WorkoutSelfWeightExercise")
data class WorkoutSelfWeightExerciseRequestModel(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
) : WorkoutItemRequestModel {
    override fun toDomain() = WorkoutSelfWeightExercise(exerciseId, targetRepeats)
}

@Serializable
@SerialName("WorkoutWeightBasedExercise")
data class WorkoutWeightBasedExerciseRequestModel(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
    val weight: Weight,
) : WorkoutItemRequestModel {
    override fun toDomain() = WorkoutWeightBasedExercise(exerciseId, targetRepeats, weight)
}
