package com.szastarek.gymz.domain.service.workout.query.handler

import com.szastarek.gymz.acl.AllowAllAccessManager
import com.szastarek.gymz.domain.service.workout.query.FindAllWeeklyWorkoutTemplatesQueryResult
import com.szastarek.gymz.fixtures.WorkoutTestFixtures
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageNumber
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.page.PageSize
import com.szastarek.gymz.shared.page.PageTotalElements
import com.szastarek.gymz.support.InMemoryWeeklyWorkoutTemplateRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FindAllWeeklyWorkoutTemplatesQueryHandlerTest : StringSpec({

    val repository = InMemoryWeeklyWorkoutTemplateRepository()
    val handler = FindAllWeeklyWorkoutTemplatesQueryHandler(AllowAllAccessManager, repository)

    beforeTest {
        repository.clear()
    }

    "should return empty page" {
        // arrange
        val pageQueryParameters = PageQueryParameters.default
        val query = WorkoutTestFixtures.findAllWeeklyWorkoutTemplatesQuery(pageQueryParameters = pageQueryParameters)

        // act
        val result = handler.handle(query)

        // assert
        result shouldBe FindAllWeeklyWorkoutTemplatesQueryResult(
            weeklyWorkoutTemplates = Page(
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
        val weeklyWorkoutTemplate1 = WorkoutTestFixtures.weeklyWorkoutTemplate()
            .also { repository.save(it) }
        val weeklyWorkoutTemplate2 = WorkoutTestFixtures.weeklyWorkoutTemplate()
            .also { repository.save(it) }
        val weeklyWorkoutTemplate3 = WorkoutTestFixtures.weeklyWorkoutTemplate()
            .also { repository.save(it) }
        val pageQueryParameters = PageQueryParameters(pageSize = PageSize(2), pageNumber = PageNumber.first)
        val query = WorkoutTestFixtures.findAllWeeklyWorkoutTemplatesQuery(pageQueryParameters = pageQueryParameters)

        // act
        val result = handler.handle(query)

        // assert
        result shouldBe FindAllWeeklyWorkoutTemplatesQueryResult(
            weeklyWorkoutTemplates = Page(
                data = listOf(weeklyWorkoutTemplate1, weeklyWorkoutTemplate2, weeklyWorkoutTemplate3)
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
        val weeklyWorkoutTemplate = WorkoutTestFixtures.weeklyWorkoutTemplate()
            .also { repository.save(it) }
        val pageQueryParameters = PageQueryParameters(pageSize = PageSize(2), pageNumber = PageNumber.first)
        val query = WorkoutTestFixtures.findAllWeeklyWorkoutTemplatesQuery(pageQueryParameters = pageQueryParameters)

        // act
        val result = handler.handle(query)

        // assert
        result shouldBe FindAllWeeklyWorkoutTemplatesQueryResult(
            weeklyWorkoutTemplates = Page(
                data = listOf(weeklyWorkoutTemplate),
                totalElements = PageTotalElements(1),
                pageSize = pageQueryParameters.pageSize,
                pageNumber = pageQueryParameters.pageNumber,
                isLastPage = true,
            ),
        )
    }
})
