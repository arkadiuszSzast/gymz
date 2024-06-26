package com.szastarek.gymz.adapter.rest.workout.request

import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.weight.Weight
import com.szastarek.gymz.domain.service.workout.command.AddWeeklyWorkoutTemplateCommand
import com.szastarek.gymz.domain.service.workout.command.WeeklyWorkoutEntryCommandModel
import com.szastarek.gymz.domain.service.workout.command.WorkoutBreakCommandModel
import com.szastarek.gymz.domain.service.workout.command.WorkoutItemCommandModel
import com.szastarek.gymz.domain.service.workout.command.WorkoutSelfWeightExerciseCommandModel
import com.szastarek.gymz.domain.service.workout.command.WorkoutWeightBasedExerciseCommandModel
import com.szastarek.gymz.shared.i18n.TranslationKey
import com.szastarek.gymz.shared.security.UserContext
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class AddWeeklyWorkoutTemplateRequest(
    val name: TranslationKey,
    val description: TranslationKey,
    val entries: List<WeeklyWorkoutEntryRequestModel>,
) {
    fun toCommand(userContext: UserContext) = AddWeeklyWorkoutTemplateCommand(
        userContext = userContext,
        name = name,
        description = description,
        entries = entries.map { it.toCommand() },
    )
}

@Serializable
data class WeeklyWorkoutEntryRequestModel(
    val day: DayOfWeek,
    val items: List<WorkoutItemRequestModel>,
    val name: TranslationKey,
) {
    fun toCommand() = WeeklyWorkoutEntryCommandModel(
        day = day,
        items = items.map { it.toCommand() },
        name = name,
    )
}

@Serializable
sealed interface WorkoutItemRequestModel {
    fun toCommand(): WorkoutItemCommandModel
}

@Serializable
@SerialName("WorkoutBreak")
data class WorkoutBreakRequestModel(val duration: Duration) : WorkoutItemRequestModel {
    override fun toCommand() = WorkoutBreakCommandModel(duration)
}

@Serializable
@SerialName("WorkoutSelfWeightExercise")
data class WorkoutSelfWeightExerciseRequestModel(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
) : WorkoutItemRequestModel {
    override fun toCommand() = WorkoutSelfWeightExerciseCommandModel(exerciseId, targetRepeats)
}

@Serializable
@SerialName("WorkoutWeightBasedExercise")
data class WorkoutWeightBasedExerciseRequestModel(
    val exerciseId: GymExerciseId,
    val targetRepeats: UInt,
    val weight: Weight,
) : WorkoutItemRequestModel {
    override fun toCommand() = WorkoutWeightBasedExerciseCommandModel(exerciseId, targetRepeats, weight)
}
