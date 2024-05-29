package com.szastarek.gymz.adapter.rest.exercise.response

import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.muscle.group.MuscleGroup
import com.szastarek.gymz.domain.model.tag.Tag
import com.szastarek.gymz.file.storage.FileUrlResolver
import com.szastarek.gymz.shared.UrlSerializer
import com.szastarek.gymz.shared.i18n.TranslationKey
import io.ktor.http.Url
import kotlinx.serialization.Serializable

@Serializable
data class GymExerciseResponse(
    val id: GymExerciseId,
    val name: TranslationKey,
    val description: TranslationKey,
    @Serializable(with = UrlSerializer::class)
    val image: Url,
    @Serializable(with = UrlSerializer::class)
    val video: Url,
    val primaryMusclesGroups: List<MuscleGroup>,
    val secondaryMusclesGroups: List<MuscleGroup>,
    val requiredEquipments: List<Equipment>,
    val tags: List<Tag>,
) {
    companion object {
        fun from(gymExercise: GymExercise, fileUrlResolver: FileUrlResolver) = GymExerciseResponse(
            id = gymExercise.id,
            name = gymExercise.name,
            description = gymExercise.description,
            image = fileUrlResolver.resolve(gymExercise.image),
            video = fileUrlResolver.resolve(gymExercise.video),
            primaryMusclesGroups = gymExercise.primaryMusclesGroups,
            secondaryMusclesGroups = gymExercise.secondaryMusclesGroups,
            requiredEquipments = gymExercise.requiredEquipments,
            tags = gymExercise.tags,
        )
    }
}
