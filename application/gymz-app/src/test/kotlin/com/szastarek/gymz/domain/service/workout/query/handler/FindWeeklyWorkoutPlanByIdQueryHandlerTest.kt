package com.szastarek.gymz.domain.service.workout.query.handler

import com.szastarek.gymz.acl.AllowAllAccessManager
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlanId
import com.szastarek.gymz.domain.service.workout.query.FindWeeklyWorkoutPlanByIdQueryResult
import com.szastarek.gymz.fixtures.WorkoutTestFixtures
import com.szastarek.gymz.support.InMemoryWeeklyWorkoutPlanRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class FindWeeklyWorkoutPlanByIdQueryHandlerTest : StringSpec({

    val repository = InMemoryWeeklyWorkoutPlanRepository()
    val handler = FindWeeklyWorkoutPlanByIdQueryHandler(AllowAllAccessManager, repository)

    beforeTest {
        repository.clear()
    }

    "should return not found when weekly workout plan does not exist" {
        // arrange
        val query = WorkoutTestFixtures.findWeeklyWorkoutPlanByIdQuery(id = WeeklyWorkoutPlanId("not-existing-id"))

        // act
        val result = handler.handle(query)

        // assert
        result.shouldBeInstanceOf<FindWeeklyWorkoutPlanByIdQueryResult.NotFound>().id shouldBe WeeklyWorkoutPlanId("not-existing-id")
    }

    "should return found weekly workout plan" {
        // arrange
        val weeklyWorkoutPlan = WorkoutTestFixtures.weeklyWorkoutPlan()
            .also { repository.save(it) }
        val query = WorkoutTestFixtures.findWeeklyWorkoutPlanByIdQuery(id = weeklyWorkoutPlan.id)

        // act
        val result = handler.handle(query)

        // assert
        result.shouldBeInstanceOf<FindWeeklyWorkoutPlanByIdQueryResult.Found>().weeklyWorkoutPlan shouldBe weeklyWorkoutPlan
    }
})
