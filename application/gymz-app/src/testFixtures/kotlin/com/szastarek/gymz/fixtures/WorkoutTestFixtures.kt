package com.szastarek.gymz.fixtures

import com.szastarek.gymz.adapter.rest.workout.request.AddWeeklyWorkoutRequest
import com.szastarek.gymz.adapter.rest.workout.request.WeeklyWorkoutEntryRequestModel
import com.szastarek.gymz.adapter.rest.workout.request.WorkoutBreakRequestModel
import com.szastarek.gymz.adapter.rest.workout.request.WorkoutItemRequestModel
import com.szastarek.gymz.adapter.rest.workout.request.WorkoutSelfWeightExerciseRequestModel
import com.szastarek.gymz.adapter.rest.workout.request.WorkoutWeightBasedExerciseRequestModel
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.weight.Weight
import com.szastarek.gymz.generators.TranslationKeyGenerator
import com.szastarek.gymz.shared.i18n.TranslationKey
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.duration
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.uInt
import io.kotest.property.arbitrary.uLong
import kotlinx.datetime.DayOfWeek
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object WorkoutTestFixtures {

    fun addWeeklyWorkoutRequest(
        name: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        description: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        entries: List<WeeklyWorkoutEntryRequestModel> = Arb.list(arbitrary { weeklyWorkoutEntryRequestModel() }, 1..5).next(),
    ) = AddWeeklyWorkoutRequest(
        name,
        description,
        entries,
    )

    fun weeklyWorkoutEntryRequestModel(
        day: DayOfWeek = Arb.enum<DayOfWeek>().next(),
        name: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        items: List<WorkoutItemRequestModel> = Arb.list(arbitrary { workoutItemRequestModel() }, 1..10).next(),
    ) = WeeklyWorkoutEntryRequestModel(
        day = day,
        name = name,
        items = items,
    )

    fun workoutItemRequestModel(): WorkoutItemRequestModel =
        listOf(
            workoutSelfWeightExerciseRequestModel(),
            workoutWeightBasedExerciseRequestModel(),
            workoutBreakRequestModel(),
        ).random()

    fun workoutSelfWeightExerciseRequestModel(
        exerciseId: GymExerciseId = GymExerciseId.new(),
        targetRepeats: UInt = Arb.uInt(1U..20U).next(),
    ) = WorkoutSelfWeightExerciseRequestModel(
        exerciseId = exerciseId,
        targetRepeats = targetRepeats,
    )

    fun workoutWeightBasedExerciseRequestModel(
        exerciseId: GymExerciseId = GymExerciseId.new(),
        targetRepeats: UInt = Arb.uInt(1U..20U).next(),
        weight: Weight = Arb.uLong(1UL..100UL).next().let { Weight(it) },
    ) = WorkoutWeightBasedExerciseRequestModel(
        exerciseId = exerciseId,
        targetRepeats = targetRepeats,
        weight = weight,
    )

    fun workoutBreakRequestModel(
        duration: Duration = Arb.duration(10.seconds..5.minutes).next(),
    ) = WorkoutBreakRequestModel(
        duration = duration,
    )
}
