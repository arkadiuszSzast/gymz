package com.szastarek.gymz.domain.service.exercise.command

import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.muscle.group.MuscleGroup
import com.szastarek.gymz.domain.model.tag.Tag
import com.szastarek.gymz.shared.i18n.TranslationKey
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.CommandWithResult
import io.ktor.http.Url

data class AddGymExerciseCommand(
    val userContext: UserContext,
    val name: TranslationKey,
    val description: TranslationKey,
    val imageUrl: Url,
    val videoUrl: Url,
    val primaryMusclesGroups: List<MuscleGroup>,
    val secondaryMusclesGroups: List<MuscleGroup>,
    val requiredEquipmentsIds: List<EquipmentId>,
    val tags: List<Tag>,
) : CommandWithResult<AddGymExerciseCommandResult>

sealed interface AddGymExerciseCommandResult {
    data class AddGymExerciseSuccessResult(val exerciseId: GymExerciseId) : AddGymExerciseCommandResult
    sealed interface AddGymExerciseCommandError : AddGymExerciseCommandResult {
        data class EquipmentNotFound(val notFoundEquipments: List<EquipmentId>) : AddGymExerciseCommandError
        data class UnknownError(val throwable: Throwable) : AddGymExerciseCommandError
    }
}
