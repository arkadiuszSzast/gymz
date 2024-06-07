package com.szastarek.gymz.domain.service.workout.query.handler

import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.ensure
import com.szastarek.gymz.domain.service.workout.WeeklyWorkoutTemplateRepository
import com.szastarek.gymz.domain.service.workout.query.FindAllWeeklyWorkoutTemplatesQuery
import com.szastarek.gymz.domain.service.workout.query.FindAllWeeklyWorkoutTemplatesQueryResult
import com.trendyol.kediatr.QueryHandler

class FindAllWeeklyWorkoutTemplatesQueryHandler(
    private val accessManager: AccessManager,
    private val weeklyWorkoutTemplateRepository: WeeklyWorkoutTemplateRepository,
) : QueryHandler<FindAllWeeklyWorkoutTemplatesQuery, FindAllWeeklyWorkoutTemplatesQueryResult> {
    override suspend fun handle(query: FindAllWeeklyWorkoutTemplatesQuery): FindAllWeeklyWorkoutTemplatesQueryResult {
        val weeklyTemplates = weeklyWorkoutTemplateRepository.findAll(query.pageQueryParameters)
        accessManager.checkAll(query.userContext, weeklyTemplates.data.map { it.resource }, Action.read).ensure()

        return FindAllWeeklyWorkoutTemplatesQueryResult(weeklyTemplates)
    }
}
