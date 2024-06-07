package com.szastarek.gymz.domain.service.workout.query

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlan
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlanId
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.Query

data class FindWeeklyWorkoutPlanByIdQuery(
    val userContext: UserContext,
    val id: WeeklyWorkoutPlanId,
) : Query<FindWeeklyWorkoutPlanByIdQueryResult>

sealed interface FindWeeklyWorkoutPlanByIdQueryResult {
    data class Found(val weeklyWorkoutPlan: WeeklyWorkoutPlan) : FindWeeklyWorkoutPlanByIdQueryResult
    data class NotFound(val id: WeeklyWorkoutPlanId) : FindWeeklyWorkoutPlanByIdQueryResult
}
