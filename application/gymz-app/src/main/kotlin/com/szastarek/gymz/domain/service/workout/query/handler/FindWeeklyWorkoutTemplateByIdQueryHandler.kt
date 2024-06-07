package com.szastarek.gymz.domain.service.workout.query.handler

import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.check
import com.szastarek.gymz.domain.service.user.ensure
import com.szastarek.gymz.domain.service.workout.WeeklyWorkoutTemplateRepository
import com.szastarek.gymz.domain.service.workout.query.FindWeeklyWorkoutTemplateByIdQuery
import com.szastarek.gymz.domain.service.workout.query.FindWeeklyWorkoutTemplateByIdQueryResult
import com.trendyol.kediatr.QueryHandler

class FindWeeklyWorkoutTemplateByIdQueryHandler(
    private val accessManager: AccessManager,
    private val weeklyWorkoutTemplateRepository: WeeklyWorkoutTemplateRepository,
) : QueryHandler<FindWeeklyWorkoutTemplateByIdQuery, FindWeeklyWorkoutTemplateByIdQueryResult> {
    override suspend fun handle(query: FindWeeklyWorkoutTemplateByIdQuery): FindWeeklyWorkoutTemplateByIdQueryResult {
        val weeklyWorkoutTemplate = weeklyWorkoutTemplateRepository.findById(query.id)
            ?: return FindWeeklyWorkoutTemplateByIdQueryResult.NotFound(query.id)
        accessManager.check(query.userContext, weeklyWorkoutTemplate, Action.read).ensure()

        return FindWeeklyWorkoutTemplateByIdQueryResult.Found(weeklyWorkoutTemplate)
    }
}
