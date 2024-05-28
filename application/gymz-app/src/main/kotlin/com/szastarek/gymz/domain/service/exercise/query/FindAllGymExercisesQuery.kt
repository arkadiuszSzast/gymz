package com.szastarek.gymz.domain.service.exercise.query

import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.Query

data class FindAllGymExercisesQuery(
    val userContext: UserContext,
    val pageQueryParameters: PageQueryParameters,
) : Query<FindAllGymExercisesQueryResult>

data class FindAllGymExercisesQueryResult(
    val exercises: Page<GymExercise>,
)
