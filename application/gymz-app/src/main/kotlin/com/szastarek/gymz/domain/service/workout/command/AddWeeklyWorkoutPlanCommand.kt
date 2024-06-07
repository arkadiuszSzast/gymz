package com.szastarek.gymz.domain.service.workout.command

import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlanId
import com.szastarek.gymz.domain.service.workout.command.handler.WeeklyWorkoutEntryCommandModel
import com.szastarek.gymz.shared.i18n.TranslationKey
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.CommandWithResult

sealed interface AddWeeklyWorkoutPlanCommandResult {
    data class Ok(val id: WeeklyWorkoutPlanId) : AddWeeklyWorkoutPlanCommandResult
    data class GymExerciseNotFound(val exercisesIds: List<GymExerciseId>) : AddWeeklyWorkoutPlanCommandResult
    data class UnknownError(val throwable: Throwable) : AddWeeklyWorkoutPlanCommandResult
}

data class AddWeeklyWorkoutPlanCommand(
    val userContext: UserContext,
    val name: TranslationKey,
    val description: TranslationKey,
    val entries: List<WeeklyWorkoutEntryCommandModel>,
) : CommandWithResult<AddWeeklyWorkoutPlanCommandResult>
