package com.szastarek.gymz.domain.service.workout.command.handler

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlan
import com.szastarek.gymz.domain.model.workout.WorkoutPlan
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.ensure
import com.szastarek.gymz.domain.service.workout.WorkoutPlanRepository
import com.szastarek.gymz.domain.service.workout.command.AddWeeklyWorkoutPlanCommand
import com.szastarek.gymz.domain.service.workout.command.AddWeeklyWorkoutPlanCommandResult
import com.szastarek.gymz.shared.SaveResult
import com.trendyol.kediatr.CommandWithResultHandler
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class AddWeeklyWorkoutPlanCommandHandler(
    private val accessManager: AccessManager,
    private val workoutPlanRepository: WorkoutPlanRepository,
) : CommandWithResultHandler<AddWeeklyWorkoutPlanCommand, AddWeeklyWorkoutPlanCommandResult> {
    override suspend fun handle(command: AddWeeklyWorkoutPlanCommand): AddWeeklyWorkoutPlanCommandResult {
        accessManager.check(command.userContext, WorkoutPlan.resource, Action.create).ensure()
        val workoutPlan = WeeklyWorkoutPlan.create(
            name = command.name,
            description = command.description,
            entries = command.entries,
        )

        return when (val saveResult = workoutPlanRepository.save(workoutPlan)) {
            is SaveResult.Ok -> return AddWeeklyWorkoutPlanCommandResult.Ok(workoutPlan.id)
            is SaveResult.UnknownError -> return AddWeeklyWorkoutPlanCommandResult.UnknownError(saveResult.error)
                .also { logger.error(it.throwable) { "Failed to add new weekly workout plan" } }
        }
    }
}
