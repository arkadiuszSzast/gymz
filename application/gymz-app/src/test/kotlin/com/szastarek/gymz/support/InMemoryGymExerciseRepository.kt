package com.szastarek.gymz.support

import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.service.exercise.GymExerciseRepository
import com.szastarek.gymz.shared.SaveResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryGymExerciseRepository : GymExerciseRepository {
    private val db = mutableMapOf<GymExerciseId, GymExercise>()
    private val mutex = Mutex()

    override suspend fun save(exercise: GymExercise): SaveResult = mutex.withLock {
        db[exercise.id] = exercise
        return SaveResult.Ok
    }

    suspend fun clear() = mutex.withLock {
        db.clear()
    }
}
