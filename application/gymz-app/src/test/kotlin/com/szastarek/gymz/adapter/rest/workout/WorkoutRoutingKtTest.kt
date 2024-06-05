package com.szastarek.gymz.adapter.rest.workout

import com.szastarek.gymz.adapter.rest.workout.request.AddWeeklyWorkoutRequest
import com.szastarek.gymz.adapter.rest.workout.request.RequestWeeklyWorkoutEntry
import com.szastarek.gymz.adapter.rest.workout.request.RequestWorkoutBreak
import com.szastarek.gymz.adapter.rest.workout.request.RequestWorkoutWeightBasedExercise
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.weight.Weight
import com.szastarek.gymz.shared.i18n.TranslationKey
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.support.IntegrationTest
import com.szastarek.gymz.support.addWeeklyWorkoutPlan
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DayOfWeek
import kotlin.time.Duration.Companion.seconds

class WorkoutRoutingKtTest : IntegrationTest() {

    init {

        "should add weekly workout plan" { client ->
            // arrange
            val authToken = authenticate(roles = listOf(Role.ContentEditor)).authToken
            val request = AddWeeklyWorkoutRequest(
                name = TranslationKey("Test workout"),
                description = TranslationKey("Test workout description"),
                entries = listOf(
                    RequestWeeklyWorkoutEntry(
                        day = DayOfWeek.MONDAY,
                        name = TranslationKey("Monday workout"),
                        items = listOf(
                            RequestWorkoutBreak(duration = 30.seconds),
                            RequestWorkoutWeightBasedExercise(
                                GymExerciseId.new(),
                                weight = Weight(50U),
                                targetRepeats = 10U,
                            ),
                        ),
                    ),
                ),
            )

            client.addWeeklyWorkoutPlan(authToken, request).status.value shouldBe 201
        }
    }
}
