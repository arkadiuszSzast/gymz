package com.szastarek.gymz.domain.service.workout.command

import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplateId
import com.szastarek.gymz.shared.i18n.TranslationKey
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.CommandWithResult

sealed interface AddWeeklyWorkoutTemplateCommandResult {
    data class Ok(val id: WeeklyWorkoutTemplateId) : AddWeeklyWorkoutTemplateCommandResult
    data class GymExerciseNotFound(val exercisesIds: List<GymExerciseId>) : AddWeeklyWorkoutTemplateCommandResult
    data class UnknownError(val throwable: Throwable) : AddWeeklyWorkoutTemplateCommandResult
}

data class AddWeeklyWorkoutTemplateCommand(
    val userContext: UserContext,
    val name: TranslationKey,
    val description: TranslationKey,
    val entries: List<WeeklyWorkoutEntryCommandModel>,
) : CommandWithResult<AddWeeklyWorkoutTemplateCommandResult>
