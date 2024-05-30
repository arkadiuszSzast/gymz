package com.szastarek.gymz

import com.szastarek.gymz.adapter.rest.exercise.request.AddGymExerciseRequest
import com.szastarek.gymz.adapter.rest.user.equipment.request.ChangeUserOwnedEquipmentRequest
import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.domain.model.equipment.SupportedEquipments
import com.szastarek.gymz.domain.model.exercise.GymExercise
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.model.muscle.group.MuscleGroup
import com.szastarek.gymz.domain.model.tag.Tag
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipmentsEvent
import com.szastarek.gymz.domain.service.exercise.command.AddGymExerciseCommand
import com.szastarek.gymz.domain.service.exercise.query.FindGymExerciseByIdQuery
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
import io.kotest.property.arbitrary.string
import io.kotest.property.arbs.cars
import io.kotest.property.arbs.movies.harryPotterCharacter
import io.ktor.http.Url
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import java.util.UUID
import java.time.Instant as JavaInstant

@OptIn(DelicateKotest::class)
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
        primaryMusclesGroups: List<MuscleGroup> = Arb.list(Arb.enum<MuscleGroup>(), 1..3).distinct().next(),
        secondaryMusclesGroups: List<MuscleGroup> = Arb.list(
            Arb.of(MuscleGroup.entries.filter { it !in primaryMusclesGroups }),
            1..3,
        ).distinct().next(),
        requiredEquipmentsIds: List<EquipmentId> = EquipmentIds.randomList(1, 2),
        tags: List<Tag> = Arb.list(Arb.Companion.harryPotterCharacter().map { Tag(it.firstName).getOrThrow() }, 1..5)
            .distinct()
            .next(),
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
        primaryMusclesGroups: List<MuscleGroup> = Arb.list(Arb.enum<MuscleGroup>(), 1..3).distinct().next(),
        secondaryMusclesGroups: List<MuscleGroup> = Arb.list(
            Arb.of(MuscleGroup.entries.filter { it !in primaryMusclesGroups }),
            1..3,
        ).distinct().next(),
        requiredEquipmentsIds: List<EquipmentId> = EquipmentIds.randomList(1, 2),
        tags: List<Tag> = Arb.list(Arb.Companion.harryPotterCharacter().map { Tag(it.firstName).getOrThrow() }, 1..5)
            .next(),
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
        id: GymExerciseId = GymExerciseId(UUID.randomUUID().toString()),
    ) = FindGymExerciseByIdQuery(
        userContext = userContext,
        id = id,
    )

    fun gymExercise(
        id: GymExerciseId = GymExerciseId(UUID.randomUUID().toString()),
        name: TranslationKey = TranslationKey(Arb.Companion.cars().next().value),
        description: TranslationKey = TranslationKey(Arb.Companion.cars().next().value),
        image: StoredFile = storedFile(),
        video: StoredFile = storedFile(),
        primaryMusclesGroups: List<MuscleGroup> = Arb.list(Arb.enum<MuscleGroup>(), 1..3).next(),
        secondaryMusclesGroups: List<MuscleGroup> = Arb.list(
            Arb.of(MuscleGroup.entries.filter { it !in primaryMusclesGroups }),
            1..3,
        ).next(),
        requiredEquipments: List<Equipment> = Arb.intRange(1..2).next().map { equipment() },
        tags: List<Tag> = Arb.list(Arb.Companion.harryPotterCharacter().map { Tag(it.firstName).getOrThrow() }, 1..5)
            .next(),
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

    fun equipment(
        id: EquipmentId = EquipmentId(UUID.randomUUID().toString()),
        name: TranslationKey = TranslationKey(Arb.Companion.cars().next().value),
        image: StoredFile = StoredFile(
            FileKey.new(),
            FileBasePath("equipments"),
            FileExtension("png"),
            Arb.instant(
                JavaInstant.parse("2021-01-01T00:00:00Z"),
                JavaInstant.parse("2023-01-01T00:00:00Z"),
            ).next().toKotlinInstant(),
        ),
    ) = Equipment(
        id,
        name,
        image,
    )

    fun storedFile(
        key: FileKey = FileKey.new(),
        basePath: FileBasePath = FileBasePath(Arb.string(1..10).next()),
        extension: FileExtension = FileExtension("png"),
        createdAt: Instant = Arb.instant(
            JavaInstant.parse("2021-01-01T00:00:00Z"),
            JavaInstant.parse("2023-01-01T00:00:00Z"),
        ).next().toKotlinInstant(),
    ) = StoredFile(
        key,
        basePath,
        extension,
        createdAt,
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
