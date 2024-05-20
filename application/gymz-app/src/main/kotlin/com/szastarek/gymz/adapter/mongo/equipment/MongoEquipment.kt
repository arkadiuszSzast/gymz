package com.szastarek.gymz.adapter.mongo.equipment

import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.file.storage.model.StoredFile
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MongoEquipment(
    @SerialName("_id")
    @Contextual
    val id: EquipmentId,
    val name: TranslationKey,
    val image: StoredFile,
) {
    fun toDomain() = Equipment(
        id = id,
        name = name,
        image = image,
    )
}
