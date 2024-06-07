package com.szastarek.gymz.domain.service.workout.query

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplate
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplateId
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.Query

data class FindWeeklyWorkoutTemplateByIdQuery(
    val userContext: UserContext,
    val id: WeeklyWorkoutTemplateId,
) : Query<FindWeeklyWorkoutTemplateByIdQueryResult>

sealed interface FindWeeklyWorkoutTemplateByIdQueryResult {
    data class Found(val weeklyWorkoutTemplate: WeeklyWorkoutTemplate) : FindWeeklyWorkoutTemplateByIdQueryResult
    data class NotFound(val id: WeeklyWorkoutTemplateId) : FindWeeklyWorkoutTemplateByIdQueryResult
}
