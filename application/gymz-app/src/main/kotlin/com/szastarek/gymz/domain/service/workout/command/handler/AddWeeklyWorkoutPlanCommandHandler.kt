package com.szastarek.gymz.domain.service.workout.command.handler

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlan
import com.szastarek.gymz.domain.model.workout.WorkoutBreak
import com.szastarek.gymz.domain.model.workout.WorkoutSelfWeightExercise
import com.szastarek.gymz.domain.model.workout.WorkoutWeightBasedExercise
import com.szastarek.gymz.domain.service.exercise.GymExerciseRepository
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.ensure
import com.szastarek.gymz.domain.service.workout.WeeklyWorkoutPlanRepository
import com.szastarek.gymz.domain.service.workout.command.AddWeeklyWorkoutPlanCommand
import com.szastarek.gymz.domain.service.workout.command.AddWeeklyWorkoutPlanCommandResult
import com.szastarek.gymz.shared.SaveResult
import com.trendyol.kediatr.CommandWithResultHandler
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class AddWeeklyWorkoutPlanCommandHandler(
    private val accessManager: AccessManager,
    private val gymExerciseRepository: GymExerciseRepository,
    private val weeklyWorkoutPlanRepository: WeeklyWorkoutPlanRepository,
) : CommandWithResultHandler<AddWeeklyWorkoutPlanCommand, AddWeeklyWorkoutPlanCommandResult> {
    override suspend fun handle(command: AddWeeklyWorkoutPlanCommand): AddWeeklyWorkoutPlanCommandResult {
        accessManager.check(command.userContext, WeeklyWorkoutPlan.resource, Action.create).ensure()
        val gymExercisesIds = command.entries.flatMap { it.items }.mapNotNull {
            when (it) {
                is WorkoutBreak -> null
                is WorkoutSelfWeightExercise -> it.exerciseId
                is WorkoutWeightBasedExercise -> it.exerciseId
            }
        }
        val foundExercisesIds = gymExerciseRepository.findByIds(gymExercisesIds).map { it.id }

        val notFoundExercises = gymExercisesIds.filter { it !in foundExercisesIds }
        if (notFoundExercises.isNotEmpty()) {
            return AddWeeklyWorkoutPlanCommandResult.GymExerciseNotFound(notFoundExercises)
                .also { logger.error { "Failed to add new weekly workout plan because exercises with ids: $notFoundExercises were not found" } }
        }

        val workoutPlan = WeeklyWorkoutPlan.create(
            name = command.name,
            description = command.description,
            entries = command.entries,
        )

        return when (val saveResult = weeklyWorkoutPlanRepository.save(workoutPlan)) {
            is SaveResult.Ok -> AddWeeklyWorkoutPlanCommandResult.Ok(workoutPlan.id)
            is SaveResult.UnknownError -> AddWeeklyWorkoutPlanCommandResult.UnknownError(saveResult.error)
                .also { logger.error(it.throwable) { "Failed to add new weekly workout plan" } }
        }
    }
}
