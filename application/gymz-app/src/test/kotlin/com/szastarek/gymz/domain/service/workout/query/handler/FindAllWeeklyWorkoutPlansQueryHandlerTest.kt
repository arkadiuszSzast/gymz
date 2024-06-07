package com.szastarek.gymz.domain.service.workout.query.handler

import com.szastarek.gymz.acl.AllowAllAccessManager
import com.szastarek.gymz.domain.service.workout.query.FindAllWeeklyWorkoutPlansQueryResult
import com.szastarek.gymz.fixtures.WorkoutTestFixtures
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageNumber
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.page.PageSize
import com.szastarek.gymz.shared.page.PageTotalElements
import com.szastarek.gymz.support.InMemoryWeeklyWorkoutPlanRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FindAllWeeklyWorkoutPlansQueryHandlerTest : StringSpec({

    val repository = InMemoryWeeklyWorkoutPlanRepository()
    val handler = FindAllWeeklyWorkoutPlansQueryHandler(AllowAllAccessManager, repository)

    beforeTest {
        repository.clear()
    }

    "should return empty page" {
        // arrange
        val pageQueryParameters = PageQueryParameters.default
        val query = WorkoutTestFixtures.findAllWeeklyWorkoutPlansQuery(pageQueryParameters = pageQueryParameters)

        // act
        val result = handler.handle(query)

        // assert
        result shouldBe FindAllWeeklyWorkoutPlansQueryResult(
            weeklyWorkoutPlans = Page(
                data = emptyList(),
                totalElements = PageTotalElements(0L),
                pageSize = pageQueryParameters.pageSize,
                pageNumber = PageNumber.first,
                isLastPage = true,
            ),
        )
    }

    "should return 2 of 3 items" {
        // arrange
        val weeklyWorkoutPlan1 = WorkoutTestFixtures.weeklyWorkoutPlan()
            .also { repository.save(it) }
        val weeklyWorkoutPlan2 = WorkoutTestFixtures.weeklyWorkoutPlan()
            .also { repository.save(it) }
        val weeklyWorkoutPlan3 = WorkoutTestFixtures.weeklyWorkoutPlan()
            .also { repository.save(it) }
        val pageQueryParameters = PageQueryParameters(pageSize = PageSize(2), pageNumber = PageNumber.first)
        val query = WorkoutTestFixtures.findAllWeeklyWorkoutPlansQuery(pageQueryParameters = pageQueryParameters)

        // act
        val result = handler.handle(query)

        // assert
        result shouldBe FindAllWeeklyWorkoutPlansQueryResult(
            weeklyWorkoutPlans = Page(
                data = listOf(weeklyWorkoutPlan1, weeklyWorkoutPlan2, weeklyWorkoutPlan3)
                    .sortedBy { it.id.value }
                    .take(2),
                totalElements = PageTotalElements(3),
                pageSize = pageQueryParameters.pageSize,
                pageNumber = pageQueryParameters.pageNumber,
                isLastPage = false,
            ),
        )
    }

    "should return all items when requested more than total" {
        // arrange
        val weeklyWorkoutPlan = WorkoutTestFixtures.weeklyWorkoutPlan()
            .also { repository.save(it) }
        val pageQueryParameters = PageQueryParameters(pageSize = PageSize(2), pageNumber = PageNumber.first)
        val query = WorkoutTestFixtures.findAllWeeklyWorkoutPlansQuery(pageQueryParameters = pageQueryParameters)

        // act
        val result = handler.handle(query)

        // assert
        result shouldBe FindAllWeeklyWorkoutPlansQueryResult(
            weeklyWorkoutPlans = Page(
                data = listOf(weeklyWorkoutPlan),
                totalElements = PageTotalElements(1),
                pageSize = pageQueryParameters.pageSize,
                pageNumber = pageQueryParameters.pageNumber,
                isLastPage = true,
            ),
        )
    }
})
