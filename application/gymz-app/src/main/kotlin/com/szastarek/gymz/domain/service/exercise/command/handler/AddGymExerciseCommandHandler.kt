package com.szastarek.gymz.domain.service.exercise.command.handler

import arrow.core.Either
import arrow.core.getOrElse
import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.service.equipment.SupportedEquipmentRepository
import com.szastarek.gymz.domain.service.exercise.GymExerciseRepository
import com.szastarek.gymz.domain.service.exercise.command.AddGymExerciseCommand
import com.szastarek.gymz.domain.service.exercise.command.AddGymExerciseCommandResult
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.ensure
import com.szastarek.gymz.file.storage.FileStorage
import com.szastarek.gymz.file.storage.model.FileBasePath
import com.szastarek.gymz.file.storage.model.FileCandidate
import com.szastarek.gymz.file.storage.model.FileKey
import com.szastarek.gymz.shared.SaveResult
import com.szastarek.gymz.shared.validation.getOrThrow
import com.trendyol.kediatr.CommandWithResultHandler
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class AddGymExerciseCommandHandler(
    private val equipmentRepository: SupportedEquipmentRepository,
    private val gymExerciseRepository: GymExerciseRepository,
    private val accessManager: AccessManager,
    private val fileStorage: FileStorage,
) : CommandWithResultHandler<AddGymExerciseCommand, AddGymExerciseCommandResult> {

    override suspend fun handle(command: AddGymExerciseCommand): AddGymExerciseCommandResult {
        accessManager.check(command.userContext, GymExercise.resource, Action.create).ensure()

        val equipments = equipmentRepository.get()
        val foundEquipments = equipments.tryFind(command.requiredEquipmentsIds)
        val notFoundEquipments = command.requiredEquipmentsIds - foundEquipments.map { it.id }.toSet()
        if (notFoundEquipments.isNotEmpty()) {
            return AddGymExerciseCommandResult.AddGymExerciseCommandError.EquipmentNotFound(notFoundEquipments)
        }

        val imageFileCandidate = FileCandidate.ExternalUrlFileCandidate(
            FileBasePath("exercises"),
            FileKey.new(),
            command.imageUrl,
        )
        val videoFileCandidate = FileCandidate.ExternalUrlFileCandidate(
            FileBasePath("exercises"),
            FileKey.new(),
            command.videoUrl,
        )

        val image = Either.catch { fileStorage.uploadPublic(imageFileCandidate) }
            .getOrElse {
                logger.error(it) { "Failed to upload image" }
                return AddGymExerciseCommandResult.AddGymExerciseCommandError.UnknownError(it)
            }

        val video = Either.catch { fileStorage.uploadPublic(videoFileCandidate) }
            .getOrElse {
                logger.error(it) { "Failed to upload video" }
                return AddGymExerciseCommandResult.AddGymExerciseCommandError.UnknownError(it)
            }

        val exercise = GymExercise.new(
            command.name,
            command.description,
            image,
            video,
            command.primaryMusclesGroups,
            command.secondaryMusclesGroups,
            foundEquipments,
            command.tags,
        ).getOrThrow()

        return when (val saveResult = gymExerciseRepository.save(exercise)) {
            is SaveResult.Ok -> AddGymExerciseCommandResult.AddGymExerciseSuccessResult(exercise.id)
            is SaveResult.UnknownError ->
                AddGymExerciseCommandResult.AddGymExerciseCommandError.UnknownError(saveResult.error)
                    .also { logger.error(it.throwable) { "Failed to add new exercise" } }
        }
    }
}
