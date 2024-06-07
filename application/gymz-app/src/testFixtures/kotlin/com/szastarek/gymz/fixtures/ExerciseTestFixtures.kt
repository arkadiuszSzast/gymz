package com.szastarek.gymz.fixtures

import com.szastarek.gymz.adapter.rest.exercise.request.AddGymExerciseRequest
import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.muscle.group.MuscleGroup
import com.szastarek.gymz.domain.model.tag.Tag
import com.szastarek.gymz.domain.service.exercise.command.AddGymExerciseCommand
import com.szastarek.gymz.domain.service.exercise.query.FindGymExerciseByIdQuery
import com.szastarek.gymz.file.storage.model.StoredFile
import com.szastarek.gymz.generators.EquipmentGenerator
import com.szastarek.gymz.generators.MuscleGroupGenerator
import com.szastarek.gymz.generators.StoredFileGenerator
import com.szastarek.gymz.generators.TagGenerator
import com.szastarek.gymz.generators.TranslationKeyGenerator
import com.szastarek.gymz.shared.i18n.TranslationKey
import com.szastarek.gymz.shared.security.TestFixtures.userContext
import com.szastarek.gymz.shared.security.UserContext
import io.kotest.common.DelicateKotest
import io.kotest.property.Arb
import io.kotest.property.arbitrary.distinct
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.ktor.http.Url

@OptIn(DelicateKotest::class)
object ExerciseTestFixtures {

    fun addGymExerciseRequest(
        name: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        description: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        imageUrl: String = "https://example.com/image.png",
        videoUrl: String = "https://example.com/video.mp4",
        primaryMusclesGroups: List<MuscleGroup> = MuscleGroupGenerator.primary().next(),
        secondaryMusclesGroups: List<MuscleGroup> = MuscleGroupGenerator.secondary(primaryMusclesGroups).next(),
        requiredEquipmentsIds: List<EquipmentId> = EquipmentTestFixtures.EquipmentIds.randomList(1, 2),
        tags: List<Tag> = Arb.list(TagGenerator.tag, 1..5).distinct().next(),
    ) = AddGymExerciseRequest(
        name = name,
        description = description,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        primaryMusclesGroups = primaryMusclesGroups,
        secondaryMusclesGroups = secondaryMusclesGroups,
        requiredEquipmentsIds = requiredEquipmentsIds,
        tags = tags,
    )

    fun addGymExerciseCommand(
        userContext: UserContext = userContext(),
        name: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        description: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        imageUrl: Url = Url("https://example.com/image.png"),
        videoUrl: Url = Url("https://example.com/video.mp4"),
        primaryMusclesGroups: List<MuscleGroup> = MuscleGroupGenerator.primary().next(),
        secondaryMusclesGroups: List<MuscleGroup> = MuscleGroupGenerator.secondary(primaryMusclesGroups).next(),
        requiredEquipmentsIds: List<EquipmentId> = EquipmentTestFixtures.EquipmentIds.randomList(1, 2),
        tags: List<Tag> = Arb.list(TagGenerator.tag, 1..5).distinct().next(),
    ) = AddGymExerciseCommand(
        userContext = userContext,
        name = name,
        description = description,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        primaryMusclesGroups = primaryMusclesGroups,
        secondaryMusclesGroups = secondaryMusclesGroups,
        requiredEquipmentsIds = requiredEquipmentsIds,
        tags = tags,
    )

    fun findGymExerciseByIdQuery(
        userContext: UserContext = userContext(),
        id: GymExerciseId = GymExerciseId.new(),
    ) = FindGymExerciseByIdQuery(
        userContext = userContext,
        id = id,
    )

    fun gymExercise(
        id: GymExerciseId = GymExerciseId.new(),
        name: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        description: TranslationKey = TranslationKeyGenerator.translationKey.next(),
        image: StoredFile = StoredFileGenerator.image.next(),
        video: StoredFile = StoredFileGenerator.video.next(),
        primaryMusclesGroups: List<MuscleGroup> = MuscleGroupGenerator.primary().next(),
        secondaryMusclesGroups: List<MuscleGroup> = MuscleGroupGenerator.secondary(primaryMusclesGroups).next(),
        requiredEquipments: List<Equipment> = Arb.list(EquipmentGenerator.equipment, 1..2).next(),
        tags: List<Tag> = Arb.list(TagGenerator.tag, 1..5).distinct().next(),
    ) = GymExercise(
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
