package com.szastarek.gymz.domain.service.exercise.query.handler

import com.szastarek.gymz.domain.service.exercise.GymExerciseRepository
import com.szastarek.gymz.domain.service.exercise.query.FindGymExerciseByIdQuery
import com.szastarek.gymz.domain.service.exercise.query.FindGymExerciseByIdQueryResult
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.check
import com.szastarek.gymz.domain.service.user.ensure
import com.trendyol.kediatr.QueryHandler

class FindGymExerciseByIdQueryHandler(
    private val accessManager: AccessManager,
    private val gymExerciseRepository: GymExerciseRepository,
) : QueryHandler<FindGymExerciseByIdQuery, FindGymExerciseByIdQueryResult> {
    override suspend fun handle(query: FindGymExerciseByIdQuery): FindGymExerciseByIdQueryResult {
        val gymExercise = gymExerciseRepository.findById(query.id) ?: return FindGymExerciseByIdQueryResult.NotFound(query.id)
        accessManager.check(query.userContext, gymExercise, Action.read).ensure()

        return FindGymExerciseByIdQueryResult.Found(gymExercise)
    }
}
