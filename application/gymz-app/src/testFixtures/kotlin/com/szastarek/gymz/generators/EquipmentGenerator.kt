package com.szastarek.gymz.generators

import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.shared.i18n.TranslationKey
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string

object EquipmentGenerator {
    val equipment = arbitrary { randomSource ->
        Equipment(
            EquipmentId.new(),
            TranslationKey(Arb.Companion.string(3..15, Codepoint.az()).single(randomSource)),
            StoredFileGenerator.image.single(randomSource),
        )
    }
}
