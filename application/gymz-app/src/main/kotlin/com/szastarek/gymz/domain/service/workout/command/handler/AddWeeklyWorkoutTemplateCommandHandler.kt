package com.szastarek.gymz.domain.service.workout.command.handler

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutEntry
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplate
import com.szastarek.gymz.domain.model.workout.WorkoutBreak
import com.szastarek.gymz.domain.model.workout.WorkoutSelfWeightExercise
import com.szastarek.gymz.domain.model.workout.WorkoutWeightBasedExercise
import com.szastarek.gymz.domain.service.exercise.GymExerciseRepository
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.ensure
import com.szastarek.gymz.domain.service.workout.WeeklyWorkoutTemplateRepository
import com.szastarek.gymz.domain.service.workout.command.AddWeeklyWorkoutTemplateCommand
import com.szastarek.gymz.domain.service.workout.command.AddWeeklyWorkoutTemplateCommandResult
import com.szastarek.gymz.domain.service.workout.command.WorkoutBreakCommandModel
import com.szastarek.gymz.domain.service.workout.command.WorkoutSelfWeightExerciseCommandModel
import com.szastarek.gymz.domain.service.workout.command.WorkoutWeightBasedExerciseCommandModel
import com.szastarek.gymz.shared.SaveResult
import com.trendyol.kediatr.CommandWithResultHandler
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class AddWeeklyWorkoutTemplateCommandHandler(
    private val accessManager: AccessManager,
    private val gymExerciseRepository: GymExerciseRepository,
    private val weeklyWorkoutTemplateRepository: WeeklyWorkoutTemplateRepository,
) : CommandWithResultHandler<AddWeeklyWorkoutTemplateCommand, AddWeeklyWorkoutTemplateCommandResult> {
    override suspend fun handle(command: AddWeeklyWorkoutTemplateCommand): AddWeeklyWorkoutTemplateCommandResult {
        accessManager.check(command.userContext, WeeklyWorkoutTemplate.resource, Action.create).ensure()
        val gymExercisesIds = command.entries.flatMap { it.items }.mapNotNull {
            when (it) {
                is WorkoutBreakCommandModel -> null
                is WorkoutSelfWeightExerciseCommandModel -> it.exerciseId
                is WorkoutWeightBasedExerciseCommandModel -> it.exerciseId
            }
        }
        val foundExercises = gymExerciseRepository.findByIds(gymExercisesIds)

        val workoutTemplate = WeeklyWorkoutTemplate.create(
            name = command.name,
            description = command.description,
            entries = command.entries.map { entry ->
                WeeklyWorkoutEntry(
                    day = entry.day,
                    name = entry.name,
                    items = entry.items.map { item ->
                        when (item) {
                            is WorkoutBreakCommandModel -> WorkoutBreak(item.duration)
                            is WorkoutSelfWeightExerciseCommandModel -> WorkoutSelfWeightExercise(
                                exercise = foundExercises.firstOrNull { it.id == item.exerciseId }
                                    ?: return AddWeeklyWorkoutTemplateCommandResult.GymExerciseNotFound(listOf(item.exerciseId))
                                        .also { logger.error { "Failed to add new weekly workout template because exercise with id: ${item.exerciseId} was not found" } },
                                targetRepeats = item.targetRepeats,
                            )

                            is WorkoutWeightBasedExerciseCommandModel -> WorkoutWeightBasedExercise(
                                exercise = foundExercises.firstOrNull { it.id == item.exerciseId }
                                    ?: return AddWeeklyWorkoutTemplateCommandResult.GymExerciseNotFound(listOf(item.exerciseId))
                                        .also { logger.error { "Failed to add new weekly workout template because exercise with id: ${item.exerciseId} was not found" } },
                                targetRepeats = item.targetRepeats,
                                weight = item.weight,
                            )
                        }
                    },
                )
            },
        )

        return when (val saveResult = weeklyWorkoutTemplateRepository.save(workoutTemplate)) {
            is SaveResult.Ok -> AddWeeklyWorkoutTemplateCommandResult.Ok(workoutTemplate.id)
            is SaveResult.UnknownError -> AddWeeklyWorkoutTemplateCommandResult.UnknownError(saveResult.error)
                .also { logger.error(it.throwable) { "Failed to add new weekly workout template" } }
        }
    }
}
