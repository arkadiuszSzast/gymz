package com.szastarek.gymz.domain.model.equipment

import com.szastarek.gymz.file.storage.model.StoredFile
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.serialization.Serializable
import java.util.UUID

@JvmInline
@Serializable
value class EquipmentId(val value: String) {
    companion object {
        fun new(): EquipmentId = EquipmentId(UUID.randomUUID().toString())
    }
}

@Serializable
data class Equipment(
    val id: EquipmentId,
    val name: TranslationKey,
    val image: StoredFile,
)
