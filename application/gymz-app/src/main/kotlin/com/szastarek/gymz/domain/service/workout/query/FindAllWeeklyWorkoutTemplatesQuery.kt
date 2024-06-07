package com.szastarek.gymz.domain.service.workout.query

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplate
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.Query

data class FindAllWeeklyWorkoutTemplatesQuery(
    val userContext: UserContext,
    val pageQueryParameters: PageQueryParameters,
) : Query<FindAllWeeklyWorkoutTemplatesQueryResult>

data class FindAllWeeklyWorkoutTemplatesQueryResult(
    val weeklyWorkoutTemplates: Page<WeeklyWorkoutTemplate>,
)
