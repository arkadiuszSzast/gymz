package com.szastarek.gymz.domain.service.workout.query.handler

import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.check
import com.szastarek.gymz.domain.service.user.ensure
import com.szastarek.gymz.domain.service.workout.WeeklyWorkoutPlanRepository
import com.szastarek.gymz.domain.service.workout.query.FindWeeklyWorkoutPlanByIdQuery
import com.szastarek.gymz.domain.service.workout.query.FindWeeklyWorkoutPlanByIdQueryResult
import com.trendyol.kediatr.QueryHandler

class FindWeeklyWorkoutPlanByIdQueryHandler(
    private val accessManager: AccessManager,
    private val weeklyWorkoutPlanRepository: WeeklyWorkoutPlanRepository,
) : QueryHandler<FindWeeklyWorkoutPlanByIdQuery, FindWeeklyWorkoutPlanByIdQueryResult> {
    override suspend fun handle(query: FindWeeklyWorkoutPlanByIdQuery): FindWeeklyWorkoutPlanByIdQueryResult {
        val weeklyWorkoutPlan = weeklyWorkoutPlanRepository.findById(query.id)
            ?: return FindWeeklyWorkoutPlanByIdQueryResult.NotFound(query.id)
        accessManager.check(query.userContext, weeklyWorkoutPlan, Action.read).ensure()

        return FindWeeklyWorkoutPlanByIdQueryResult.Found(weeklyWorkoutPlan)
    }
}
