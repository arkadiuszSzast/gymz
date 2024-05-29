package com.szastarek.gymz.domain.service.exercise.query

import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.Query

data class FindGymExerciseByIdQuery(val userContext: UserContext, val id: GymExerciseId) : Query<FindGymExerciseByIdQueryResult>

sealed interface FindGymExerciseByIdQueryResult {
    data class Found(val exercise: GymExercise) : FindGymExerciseByIdQueryResult
    data class NotFound(val id: GymExerciseId) : FindGymExerciseByIdQueryResult
}
