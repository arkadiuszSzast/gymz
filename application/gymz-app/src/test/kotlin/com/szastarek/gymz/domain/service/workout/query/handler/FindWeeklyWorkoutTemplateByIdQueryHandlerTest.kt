package com.szastarek.gymz.domain.service.workout.query.handler

import com.szastarek.gymz.acl.AllowAllAccessManager
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplateId
import com.szastarek.gymz.domain.service.workout.query.FindWeeklyWorkoutTemplateByIdQueryResult
import com.szastarek.gymz.fixtures.WorkoutTestFixtures
import com.szastarek.gymz.support.InMemoryWeeklyWorkoutTemplateRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class FindWeeklyWorkoutTemplateByIdQueryHandlerTest : StringSpec({

    val repository = InMemoryWeeklyWorkoutTemplateRepository()
    val handler = FindWeeklyWorkoutTemplateByIdQueryHandler(AllowAllAccessManager, repository)

    beforeTest {
        repository.clear()
    }

    "should return not found when weekly workout template does not exist" {
        // arrange
        val query = WorkoutTestFixtures.findWeeklyWorkoutTemplateByIdQuery(id = WeeklyWorkoutTemplateId("not-existing-id"))

        // act
        val result = handler.handle(query)

        // assert
        result.shouldBeInstanceOf<FindWeeklyWorkoutTemplateByIdQueryResult.NotFound>().id shouldBe WeeklyWorkoutTemplateId("not-existing-id")
    }

    "should return found weekly workout template" {
        // arrange
        val weeklyWorkoutTemplate = WorkoutTestFixtures.weeklyWorkoutTemplate()
            .also { repository.save(it) }
        val query = WorkoutTestFixtures.findWeeklyWorkoutTemplateByIdQuery(id = weeklyWorkoutTemplate.id)

        // act
        val result = handler.handle(query)

        // assert
        result.shouldBeInstanceOf<FindWeeklyWorkoutTemplateByIdQueryResult.Found>().weeklyWorkoutTemplate shouldBe weeklyWorkoutTemplate
    }
})
