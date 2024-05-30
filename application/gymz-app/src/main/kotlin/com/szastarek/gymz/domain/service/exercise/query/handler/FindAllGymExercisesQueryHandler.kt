package com.szastarek.gymz.domain.service.exercise.query.handler

import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.service.exercise.GymExerciseRepository
import com.szastarek.gymz.domain.service.exercise.query.FindAllGymExercisesQuery
import com.szastarek.gymz.domain.service.exercise.query.FindAllGymExercisesQueryResult
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.ensure
import com.trendyol.kediatr.QueryHandler

class FindAllGymExercisesQueryHandler(
    private val gymExerciseRepository: GymExerciseRepository,
    private val accessManager: AccessManager,
) : QueryHandler<FindAllGymExercisesQuery, FindAllGymExercisesQueryResult> {
    override suspend fun handle(query: FindAllGymExercisesQuery): FindAllGymExercisesQueryResult {
        accessManager.check(query.userContext, GymExercise.resource, Action.read).ensure()
        return FindAllGymExercisesQueryResult(gymExerciseRepository.findAll(query.pageQueryParameters))
    }
}
