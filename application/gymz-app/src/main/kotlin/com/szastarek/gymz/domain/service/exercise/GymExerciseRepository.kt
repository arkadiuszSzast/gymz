package com.szastarek.gymz.domain.service.exercise

import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.shared.SaveResult
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters

interface GymExerciseRepository {
    suspend fun save(exercise: GymExercise): SaveResult
    suspend fun findAll(pageQueryParameters: PageQueryParameters): Page<GymExercise>
}
