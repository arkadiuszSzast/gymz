package com.szastarek.gymz.domain.service.workout.query.handler

import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.ensure
import com.szastarek.gymz.domain.service.workout.WeeklyWorkoutPlanRepository
import com.szastarek.gymz.domain.service.workout.query.FindAllWeeklyWorkoutPlansQuery
import com.szastarek.gymz.domain.service.workout.query.FindAllWeeklyWorkoutPlansQueryResult
import com.trendyol.kediatr.QueryHandler

class FindAllWeeklyWorkoutPlansQueryHandler(
    private val accessManager: AccessManager,
    private val weeklyWorkoutPlanRepository: WeeklyWorkoutPlanRepository,
) : QueryHandler<FindAllWeeklyWorkoutPlansQuery, FindAllWeeklyWorkoutPlansQueryResult> {
    override suspend fun handle(query: FindAllWeeklyWorkoutPlansQuery): FindAllWeeklyWorkoutPlansQueryResult {
        val weeklyPlans = weeklyWorkoutPlanRepository.findAll(query.pageQueryParameters)
        accessManager.checkAll(query.userContext, weeklyPlans.data.map { it.resource }, Action.read).ensure()

        return FindAllWeeklyWorkoutPlansQueryResult(weeklyPlans)
    }
}
