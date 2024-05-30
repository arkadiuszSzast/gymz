package com.szastarek.gymz.domain.service.exercise.command.handler

import com.szastarek.gymz.TestFixtures
import com.szastarek.gymz.acl.AllowAllAccessManager
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.domain.model.muscle.group.MuscleGroup
import com.szastarek.gymz.domain.service.exercise.command.AddGymExerciseCommandResult
import com.szastarek.gymz.file.storage.RecordingFileStorage
import com.szastarek.gymz.shared.validation.ValidationError
import com.szastarek.gymz.shared.validation.ValidationException
import com.szastarek.gymz.support.InMemoryGymExerciseRepository
import com.szastarek.gymz.support.InMemorySupportedEquipmentRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class AddGymExerciseCommandHandlerTest : StringSpec({

    val supportedEquipmentRepository = InMemorySupportedEquipmentRepository()
    val gymExerciseRepository = InMemoryGymExerciseRepository()
    val fileStorage = RecordingFileStorage()

    val handler = AddGymExerciseCommandHandler(
        supportedEquipmentRepository,
        gymExerciseRepository,
        AllowAllAccessManager,
        fileStorage,
    )

    beforeTest {
        supportedEquipmentRepository.clear()
        gymExerciseRepository.clear()
    }

    "should not add gym exercise with not existing equipment" {
        // arrange
        val command = TestFixtures.addGymExerciseCommand(
            requiredEquipmentsIds = listOf(EquipmentId("not-existing-equipment")),
        )

        // act
        val result = handler.handle(command)

        // assert
        result shouldBe AddGymExerciseCommandResult.AddGymExerciseCommandError.EquipmentNotFound(
            notFoundEquipments = listOf(EquipmentId("not-existing-equipment")),
        )
    }

    "should not add gym exercise with empty primary muscles groups" {
        // arrange
        val command = TestFixtures.addGymExerciseCommand(
            requiredEquipmentsIds = emptyList(),
            primaryMusclesGroups = emptyList(),

        )

        // act
        val exception = shouldThrow<ValidationException> {
            handler.handle(command)
        }

        // assert
        exception.validationErrors shouldBe listOf(
            ValidationError(".primaryMusclesGroups", "primaryMusclesGroups_empty"),
        )
    }

    "should not add gym exercise with muscle group added as primary and secondary" {
        // arrange
        val command = TestFixtures.addGymExerciseCommand(
            requiredEquipmentsIds = emptyList(),
            primaryMusclesGroups = listOf(MuscleGroup.Biceps),
            secondaryMusclesGroups = listOf(MuscleGroup.Biceps),
        )

        // act
        val exception = shouldThrow<ValidationException> {
            handler.handle(command)
        }

        // assert
        exception.validationErrors shouldBe listOf(
            ValidationError(".musclesGroup", "musclesGroup_misconfigured"),
        )
    }

    "should add gym exercise" {
        // arrange
        val availableEquipment = TestFixtures.equipment().also {
            supportedEquipmentRepository.add(it)
        }
        val command = TestFixtures.addGymExerciseCommand(
            requiredEquipmentsIds = listOf(availableEquipment.id),
        )

        // act
        val result = handler.handle(command)

        // assert
        result.shouldBeInstanceOf<AddGymExerciseCommandResult.AddGymExerciseSuccessResult>()
    }
})
