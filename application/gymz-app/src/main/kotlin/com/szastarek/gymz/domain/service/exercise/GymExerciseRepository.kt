package com.szastarek.gymz.domain.service.exercise

import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.shared.SaveResult

interface GymExerciseRepository {
    suspend fun save(exercise: GymExercise): SaveResult
}
