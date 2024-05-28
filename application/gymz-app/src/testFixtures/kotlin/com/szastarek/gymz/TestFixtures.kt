package com.szastarek.gymz

import com.szastarek.gymz.adapter.rest.exercise.request.AddGymExerciseRequest
import com.szastarek.gymz.adapter.rest.user.equipment.request.ChangeUserOwnedEquipmentRequest
import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.domain.model.equipment.SupportedEquipments
import com.szastarek.gymz.domain.model.muscle.group.MuscleGroup
import com.szastarek.gymz.domain.model.tag.Tag
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipmentsEvent
import com.szastarek.gymz.domain.service.exercise.command.AddGymExerciseCommand
import com.szastarek.gymz.file.storage.model.FileBasePath
import com.szastarek.gymz.file.storage.model.FileExtension
import com.szastarek.gymz.file.storage.model.FileKey
import com.szastarek.gymz.file.storage.model.StoredFile
import com.szastarek.gymz.shared.i18n.TranslationKey
import com.szastarek.gymz.shared.security.TestFixtures.userContext
import com.szastarek.gymz.shared.security.UserContext
import com.szastarek.gymz.shared.validation.getOrThrow
import io.kotest.common.DelicateKotest
import io.kotest.property.Arb
import io.kotest.property.arbitrary.distinct
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.intRange
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.of
import io.kotest.property.arbs.cars
import io.kotest.property.arbs.movies.harryPotterCharacter
import io.ktor.http.Url
import kotlinx.datetime.toKotlinInstant
import java.time.Instant
import java.util.UUID

object TestFixtures {

    object EquipmentIds {
        val dumbbells = EquipmentId("af292dea-b37e-4a0a-a4bb-b41174cbaeeb")
        val kettlebells = EquipmentId("eb03341b-2402-471f-b4d7-b252113e6d86")

        @OptIn(DelicateKotest::class)
        fun randomListGenerator(min: Int = 0, max: Int = 10) =
            Arb.list(Arb.of(dumbbells, kettlebells).distinct(), min..max)

        fun randomList(min: Int = 0, max: Int = 10) = randomListGenerator(min, max).next()
    }

    fun changeUserOwnedEquipmentRequest(equipmentsIds: List<EquipmentId> = EquipmentIds.randomList(1, 2)) =
        ChangeUserOwnedEquipmentRequest(
            equipmentsIds = equipmentsIds,
        )

    fun addGymExerciseRequest(
        name: TranslationKey = TranslationKey(Arb.Companion.cars().next().value),
        description: TranslationKey = TranslationKey(Arb.Companion.cars().next().value),
        imageUrl: String = "https://example.com/image.png",
        videoUrl: String = "https://example.com/video.mp4",
        primaryMusclesGroups: List<MuscleGroup> = Arb.list(Arb.enum<MuscleGroup>(), 1..3).next(),
        secondaryMusclesGroups: List<MuscleGroup> = Arb.list(Arb.of(MuscleGroup.entries.filter { it !in primaryMusclesGroups }), 1..3).next(),
        requiredEquipmentsIds: List<EquipmentId> = EquipmentIds.randomList(1, 2),
        tags: List<Tag> = Arb.list(Arb.Companion.harryPotterCharacter().map { Tag(it.firstName).getOrThrow() }, 1..5).next(),
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
        name: TranslationKey = TranslationKey(Arb.Companion.cars().next().value),
        description: TranslationKey = TranslationKey(Arb.Companion.cars().next().value),
        imageUrl: Url = Url("https://example.com/image.png"),
        videoUrl: Url = Url("https://example.com/video.mp4"),
        primaryMusclesGroups: List<MuscleGroup> = Arb.list(Arb.enum<MuscleGroup>(), 1..3).next(),
        secondaryMusclesGroups: List<MuscleGroup> = Arb.list(Arb.of(MuscleGroup.entries.filter { it !in primaryMusclesGroups }), 1..3).next(),
        requiredEquipmentsIds: List<EquipmentId> = EquipmentIds.randomList(1, 2),
        tags: List<Tag> = Arb.list(Arb.Companion.harryPotterCharacter().map { Tag(it.firstName).getOrThrow() }, 1..5).next(),
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

    fun equipment(
        id: EquipmentId = EquipmentId(UUID.randomUUID().toString()),
        name: TranslationKey = TranslationKey(Arb.Companion.cars().next().value),
        image: StoredFile = StoredFile(
            FileKey.new(),
            FileBasePath("equipments"),
            FileExtension("png"),
            Arb.instant(
                Instant.parse("2021-01-01T00:00:00Z"),
                Instant.parse("2023-01-01T00:00:00Z"),
            ).next().toKotlinInstant(),
        ),
    ) = Equipment(
        id,
        name,
        image,
    )

    fun supportedEquipments(equipments: List<Equipment> = Arb.intRange(1..10).next().map { equipment() }) =
        SupportedEquipments(equipments)

    fun userOwnedEquipmentsEventChanged(
        userContext: UserContext = userContext(),
        equipments: List<Equipment> = Arb.intRange(1..10).next().map { equipment() },
    ) = UserOwnedEquipmentsEvent.Changed(
        userId = userContext.userId,
        equipments = equipments,
        UserOwnedEquipmentsEvent.Changed.metadata(userContext.userId),
    )
}
