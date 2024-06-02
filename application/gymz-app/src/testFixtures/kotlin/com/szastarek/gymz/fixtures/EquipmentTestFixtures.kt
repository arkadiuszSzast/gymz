package com.szastarek.gymz.fixtures

import com.szastarek.gymz.adapter.rest.user.equipment.request.ChangeUserOwnedEquipmentRequest
import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.domain.model.equipment.SupportedEquipments
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipmentsEvent
import com.szastarek.gymz.file.storage.model.StoredFile
import com.szastarek.gymz.fixtures.EquipmentTestFixtures.EquipmentIds.randomList
import com.szastarek.gymz.generators.EquipmentGenerator
import com.szastarek.gymz.generators.StoredFileGenerator
import com.szastarek.gymz.shared.i18n.TranslationKey
import com.szastarek.gymz.shared.security.TestFixtures.userContext
import com.szastarek.gymz.shared.security.UserContext
import io.kotest.common.DelicateKotest
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.distinct
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.string
import java.util.UUID

@OptIn(DelicateKotest::class)
object EquipmentTestFixtures {

    object EquipmentIds {
        val dumbbells = EquipmentId("af292dea-b37e-4a0a-a4bb-b41174cbaeeb")
        val kettlebells = EquipmentId("eb03341b-2402-471f-b4d7-b252113e6d86")

        fun randomList(min: Int = 0, max: Int = 10) =
            Arb.list(Arb.of(dumbbells, kettlebells).distinct(), min..max).next()
    }

    fun changeUserOwnedEquipmentRequest(equipmentsIds: List<EquipmentId> = randomList(1, 2)) =
        ChangeUserOwnedEquipmentRequest(equipmentsIds = equipmentsIds)

    fun equipment(
        id: EquipmentId = EquipmentId(UUID.randomUUID().toString()),
        name: TranslationKey = TranslationKey(Arb.Companion.string(3..15, Codepoint.az()).next()),
        image: StoredFile = StoredFileGenerator.image.next(),
    ) = Equipment(
        id,
        name,
        image,
    )

    fun supportedEquipments(equipments: List<Equipment> = Arb.list(EquipmentGenerator.equipment, 1..10).next()) =
        SupportedEquipments(equipments)

    fun userOwnedEquipmentsEventChanged(
        userContext: UserContext = userContext(),
        equipments: List<Equipment> = Arb.list(EquipmentGenerator.equipment, 1..10).next(),
    ) = UserOwnedEquipmentsEvent.Changed(
        userId = userContext.userId,
        equipments = equipments,
        UserOwnedEquipmentsEvent.Changed.metadata(userContext.userId),
    )
}
