package com.szastarek.gymz.adapter.rest.exercise.response

import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.muscle.group.MuscleGroup
import com.szastarek.gymz.domain.model.tag.Tag
import com.szastarek.gymz.file.storage.model.StoredFile
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.serialization.Serializable

@Serializable
data class GymExerciseResponse(
    val id: GymExerciseId,
    val name: TranslationKey,
    val description: TranslationKey,
    val image: StoredFile,
    val video: StoredFile,
    val primaryMusclesGroups: List<MuscleGroup>,
    val secondaryMusclesGroups: List<MuscleGroup>,
    val requiredEquipments: List<Equipment>,
    val tags: List<Tag>,
)
