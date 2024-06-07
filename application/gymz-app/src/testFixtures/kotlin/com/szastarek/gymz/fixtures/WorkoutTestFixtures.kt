package com.szastarek.gymz.fixtures

import com.szastarek.gymz.adapter.rest.workout.request.AddWeeklyWorkoutTemplateRequest
import com.szastarek.gymz.adapter.rest.workout.request.WeeklyWorkoutEntryRequestModel
import com.szastarek.gymz.adapter.rest.workout.request.WorkoutBreakRequestModel
import com.szastarek.gymz.adapter.rest.workout.request.WorkoutItemRequestModel
import com.szastarek.gymz.adapter.rest.workout.request.WorkoutSelfWeightExerciseRequestModel
import com.szastarek.gymz.adapter.rest.workout.request.WorkoutWeightBasedExerciseRequestModel
import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.weight.Weight
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutEntry
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplate
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplateId
import com.szastarek.gymz.domain.model.workout.WorkoutBreak
import com.szastarek.gymz.domain.model.workout.WorkoutItem
import com.szastarek.gymz.domain.model.workout.WorkoutSelfWeightExercise
import com.szastarek.gymz.domain.model.workout.WorkoutWeightBasedExercise
import com.szastarek.gymz.domain.service.workout.query.FindAllWeeklyWorkoutTemplatesQuery
import com.szastarek.gymz.domain.service.workout.query.FindWeeklyWorkoutTemplateByIdQuery
import com.szastarek.gymz.generators.TranslationKeyGenerator
import com.szastarek.gymz.shared.i18n.TranslationKey
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.security.TestFixtures.userContext
import com.szastarek.gymz.shared.security.UserContext
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
        entries: List<WeeklyWorkoutEntryRequestModel> = Arb.list(arbitrary { weeklyWorkoutEntryRequestModel() }, 1..5)
            .next(),
    ) = AddWeeklyWorkoutTemplateRequest(
        name,
        description,
        entries,
    )

    fun addOnlyBreakWeeklyWorkoutRequest(
        name: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        description: TranslationKey = TranslationKeyGenerator.translationKey.next(),
    ) = AddWeeklyWorkoutTemplateRequest(
        name,
        description,
        listOf(weeklyWorkoutEntryRequestModel(items = listOf(workoutBreakRequestModel(duration = 5.minutes)))),
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

    fun findWeeklyWorkoutTemplateByIdQuery(
        userContext: UserContext = userContext(),
        id: WeeklyWorkoutTemplateId = WeeklyWorkoutTemplateId.new(),
    ) = FindWeeklyWorkoutTemplateByIdQuery(
        userContext = userContext,
        id = id,
    )

    fun findAllWeeklyWorkoutTemplatesQuery(
        userContext: UserContext = userContext(),
        pageQueryParameters: PageQueryParameters = PageQueryParameters.default,
    ) = FindAllWeeklyWorkoutTemplatesQuery(
        userContext = userContext,
        pageQueryParameters = pageQueryParameters,
    )

    fun weeklyWorkoutTemplate(
        id: WeeklyWorkoutTemplateId = WeeklyWorkoutTemplateId.new(),
        name: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        description: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        entries: List<WeeklyWorkoutEntry> = Arb.list(arbitrary { weeklyWorkoutEntry() }, 1..5).next(),
    ) = WeeklyWorkoutTemplate(
        id = id,
        name = name,
        description = description,
        entries = entries,
    )

    fun weeklyWorkoutEntry(
        day: DayOfWeek = Arb.enum<DayOfWeek>().next(),
        name: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        items: List<WorkoutItem> = Arb.list(arbitrary { workoutItem() }, 1..10).next(),
    ) = WeeklyWorkoutEntry(
        day = day,
        name = name,
        items = items,
    )

    fun workoutItem(): WorkoutItem =
        listOf(
            workoutSelfWeightExercise(),
            workoutWeightBasedExercise(),
            workoutBreak(),
        ).random()

    fun workoutSelfWeightExercise(
        exercise: GymExercise = ExerciseTestFixtures.gymExercise(),
        targetRepeats: UInt = Arb.uInt(1U..20U).next(),
    ) = WorkoutSelfWeightExercise(
        exercise = exercise,
        targetRepeats = targetRepeats,
    )

    fun workoutWeightBasedExercise(
        exercise: GymExercise = ExerciseTestFixtures.gymExercise(),
        targetRepeats: UInt = Arb.uInt(1U..20U).next(),
        weight: Weight = Arb.uLong(1UL..100UL).next().let { Weight(it) },
    ) = WorkoutWeightBasedExercise(
        exercise = exercise,
        targetRepeats = targetRepeats,
        weight = weight,
    )

    fun workoutBreak(
        duration: Duration = Arb.duration(10.seconds..5.minutes).next(),
    ) = WorkoutBreak(
        duration = duration,
    )
}
