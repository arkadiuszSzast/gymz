package com.szastarek.gymz.domain.service.workout.query

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlan
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.Query

data class FindAllWeeklyWorkoutPlansQuery(
    val userContext: UserContext,
    val pageQueryParameters: PageQueryParameters,
) : Query<FindAllWeeklyWorkoutPlansQueryResult>

data class FindAllWeeklyWorkoutPlansQueryResult(
    val weeklyWorkoutPlans: Page<WeeklyWorkoutPlan>,
)
