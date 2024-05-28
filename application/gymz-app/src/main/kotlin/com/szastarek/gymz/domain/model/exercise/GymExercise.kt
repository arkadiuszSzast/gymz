package com.szastarek.gymz.domain.model.exercise

import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.muscle.group.MuscleGroup
import com.szastarek.gymz.domain.model.tag.Tag
import com.szastarek.gymz.file.storage.model.StoredFile
import com.szastarek.gymz.shared.acl.AclResource
import com.szastarek.gymz.shared.i18n.TranslationKey
import com.szastarek.gymz.shared.validation.ValidationError
import com.szastarek.gymz.shared.validation.ValidationErrors
import dev.cerbos.sdk.builders.Resource
import kotlinx.serialization.Serializable
import java.util.UUID

@JvmInline
@Serializable
value class GymExerciseId(val value: String) {
    companion object {
        fun new(): GymExerciseId = GymExerciseId(UUID.randomUUID().toString())
    }
}

data class GymExercise(
    val id: GymExerciseId,
    val name: TranslationKey,
    val description: TranslationKey,
    val image: StoredFile,
    val video: StoredFile,
    val primaryMusclesGroups: List<MuscleGroup>,
    val secondaryMusclesGroups: List<MuscleGroup>,
    val requiredEquipments: List<Equipment>,
    val tags: List<Tag>,
) : AclResource {

    companion object {
        val resource: Resource = Resource.newInstance("gym-exercise-resource:object")

        fun new(
            name: TranslationKey,
            description: TranslationKey,
            image: StoredFile,
            video: StoredFile,
            primaryMusclesGroups: List<MuscleGroup>,
            secondaryMusclesGroups: List<MuscleGroup>,
            requiredEquipments: List<Equipment>,
            tags: List<Tag>,
        ) = either<ValidationErrors, GymExercise> {
            zipOrAccumulate(
                {
                    ensure(primaryMusclesGroups.isNotEmpty()) {
                        ValidationError(".primaryMusclesGroups", "primaryMusclesGroups_empty")
                    }
                },
                {
                    ensure(primaryMusclesGroups.none { it in secondaryMusclesGroups }) {
                        ValidationError(".musclesGroup", "musclesGroup_misconfigured")
                    }
                },
                { _, _ ->
                    GymExercise(
                        GymExerciseId.new(),
                        name,
                        description,
                        image,
                        video,
                        primaryMusclesGroups.distinct(),
                        secondaryMusclesGroups.distinct(),
                        requiredEquipments.distinct(),
                        tags.distinct(),
                    )
                },
            )
        }
    }

    override val resource: Resource
        get() = Resource.newInstance("gym-exercise-resource:object", id.value)
}
