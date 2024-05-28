package com.szastarek.gymz.adapter.rest.equipment.response

import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.file.storage.FileUrlResolver
import com.szastarek.gymz.shared.UrlSerializer
import com.szastarek.gymz.shared.i18n.TranslationKey
import io.ktor.http.Url
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentListItem(
    val id: EquipmentId,
    val name: TranslationKey,
    @Serializable(with = UrlSerializer::class)
    val imageUrl: Url,
) {
    companion object {
        fun from(equipment: Equipment, fileUrlResolver: FileUrlResolver) = EquipmentListItem(
            id = equipment.id,
            name = equipment.name,
            imageUrl = fileUrlResolver.resolve(equipment.image),
        )
    }
}
