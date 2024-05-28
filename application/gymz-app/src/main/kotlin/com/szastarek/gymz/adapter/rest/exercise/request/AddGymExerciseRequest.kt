package com.szastarek.gymz.adapter.rest.exercise.request

import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.domain.model.muscle.group.MuscleGroup
import com.szastarek.gymz.domain.model.tag.Tag
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.serialization.Serializable

@Serializable
data class AddGymExerciseRequest(
    val name: TranslationKey,
    val description: TranslationKey,
    val imageUrl: String,
    val videoUrl: String,
    val primaryMusclesGroups: List<MuscleGroup>,
    val secondaryMusclesGroups: List<MuscleGroup>,
    val requiredEquipmentsIds: List<EquipmentId>,
    val tags: List<Tag>,
)
