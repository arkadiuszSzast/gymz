package com.szastarek.gymz.adapter.mongo.exercise

import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.muscle.group.MuscleGroup
import com.szastarek.gymz.domain.model.tag.Tag
import com.szastarek.gymz.file.storage.model.StoredFile
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MongoGymExercise(
    @SerialName("_id")
    @Contextual
    val id: GymExerciseId,
    val name: TranslationKey,
    val description: TranslationKey,
    val image: StoredFile,
    val video: StoredFile,
    val primaryMusclesGroups: List<MuscleGroup>,
    val secondaryMusclesGroups: List<MuscleGroup>,
    val requiredEquipments: List<Equipment>,
    val tags: List<Tag>,
) {

    companion object {
        fun fromDomain(exercise: GymExercise) = MongoGymExercise(
            id = exercise.id,
            name = exercise.name,
            description = exercise.description,
            image = exercise.image,
            video = exercise.video,
            primaryMusclesGroups = exercise.primaryMusclesGroups,
            secondaryMusclesGroups = exercise.secondaryMusclesGroups,
            requiredEquipments = exercise.requiredEquipments,
            tags = exercise.tags,
        )
    }

    fun toDomain() = GymExercise(
        id = id,
        name = name,
        description = description,
        image = image,
        video = video,
        primaryMusclesGroups = primaryMusclesGroups,
        secondaryMusclesGroups = secondaryMusclesGroups,
        requiredEquipments = requiredEquipments,
        tags = tags,
    )
}
