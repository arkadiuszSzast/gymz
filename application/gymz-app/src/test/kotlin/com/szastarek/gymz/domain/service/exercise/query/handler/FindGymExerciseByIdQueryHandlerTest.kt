package com.szastarek.gymz.domain.service.exercise.query.handler

import com.szastarek.gymz.acl.AllowAllAccessManager
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.service.exercise.query.FindGymExerciseByIdQueryResult
import com.szastarek.gymz.fixtures.ExerciseTestFixtures
import com.szastarek.gymz.support.InMemoryGymExerciseRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class FindGymExerciseByIdQueryHandlerTest : StringSpec({

    val gymExerciseRepository = InMemoryGymExerciseRepository()
    val handler = FindGymExerciseByIdQueryHandler(AllowAllAccessManager, gymExerciseRepository)

    beforeTest {
        gymExerciseRepository.clear()
    }

    "should return not found when exercise does not exist" {
        // arrange
        val query = ExerciseTestFixtures.findGymExerciseByIdQuery(id = GymExerciseId("not-existing-id"))

        // act
        val result = handler.handle(query)

        // assert
        result shouldBe FindGymExerciseByIdQueryResult.NotFound(query.id)
    }

    "should return found gym exercise" {
        // arrange
        val exercise = ExerciseTestFixtures.gymExercise()
            .also { gymExerciseRepository.save(it) }
        val query = ExerciseTestFixtures.findGymExerciseByIdQuery(id = exercise.id)

        // act
        val result = handler.handle(query)

        // assert
        result.shouldBeInstanceOf<FindGymExerciseByIdQueryResult.Found>().exercise shouldBe exercise
    }
})
