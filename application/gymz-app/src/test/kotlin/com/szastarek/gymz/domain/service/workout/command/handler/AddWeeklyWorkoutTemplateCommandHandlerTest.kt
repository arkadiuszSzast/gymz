package com.szastarek.gymz.domain.service.workout.command.handler

import com.szastarek.gymz.acl.AllowAllAccessManager
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.service.workout.command.AddWeeklyWorkoutTemplateCommandResult
import com.szastarek.gymz.fixtures.ExerciseTestFixtures
import com.szastarek.gymz.fixtures.WorkoutTestFixtures
import com.szastarek.gymz.shared.security.TestFixtures
import com.szastarek.gymz.support.InMemoryGymExerciseRepository
import com.szastarek.gymz.support.InMemoryWeeklyWorkoutTemplateRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf

class AddWeeklyWorkoutTemplateCommandHandlerTest : StringSpec({

    val gymExerciseRepository = InMemoryGymExerciseRepository()
    val weeklyWorkoutTemplateRepository = InMemoryWeeklyWorkoutTemplateRepository()
    val handler = AddWeeklyWorkoutTemplateCommandHandler(
        accessManager = AllowAllAccessManager,
        gymExerciseRepository = gymExerciseRepository,
        weeklyWorkoutTemplateRepository = weeklyWorkoutTemplateRepository,
    )

    beforeTest {
        gymExerciseRepository.clear()
        weeklyWorkoutTemplateRepository.clear()
    }

    "should add new weekly workout template" {
        // arrange
        val userContext = TestFixtures.userContext()
        val gymExercise = ExerciseTestFixtures.gymExercise().also { gymExerciseRepository.save(it) }
        val command = WorkoutTestFixtures.addWeeklyWorkoutRequest(
            entries = listOf(
                WorkoutTestFixtures.weeklyWorkoutEntryRequestModel(
                    items = listOf(
                        WorkoutTestFixtures.workoutSelfWeightExerciseRequestModel(
                            exerciseId = gymExercise.id,
                        ),
                    ),
                ),

            ),
        ).toCommand(userContext)

        // act
        val result = handler.handle(command)

        // assert
        result.shouldBeInstanceOf<AddWeeklyWorkoutTemplateCommandResult.Ok>()
    }

    "should return GymExerciseNotFound when exercise not found" {
        // arrange
        val userContext = TestFixtures.userContext()
        val command = WorkoutTestFixtures.addWeeklyWorkoutRequest(
            entries = listOf(
                WorkoutTestFixtures.weeklyWorkoutEntryRequestModel(
                    items = listOf(
                        WorkoutTestFixtures.workoutSelfWeightExerciseRequestModel(
                            exerciseId = GymExerciseId("not-existing-id"),
                        ),
                    ),
                ),

            ),
        ).toCommand(userContext)

        // act
        val result = handler.handle(command)

        // assert
        result.shouldBeInstanceOf<AddWeeklyWorkoutTemplateCommandResult.GymExerciseNotFound>()
    }
})
