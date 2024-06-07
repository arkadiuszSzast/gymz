package com.szastarek.gymz.adapter.rest.workout.response

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplate
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplateId
import com.szastarek.gymz.file.storage.FileUrlResolver
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.serialization.Serializable

@Serializable
data class WeeklyWorkoutTemplateResponse(
    val id: WeeklyWorkoutTemplateId,
    val name: TranslationKey,
    val description: TranslationKey,
    val entries: List<WeeklyWorkoutEntryResponseModel>,
) {
    companion object {
        fun from(weeklyWorkoutTemplate: WeeklyWorkoutTemplate, urlResolver: FileUrlResolver) = WeeklyWorkoutTemplateResponse(
            id = weeklyWorkoutTemplate.id,
            name = weeklyWorkoutTemplate.name,
            description = weeklyWorkoutTemplate.description,
            entries = weeklyWorkoutTemplate.entries.map { WeeklyWorkoutEntryResponseModel.fromDomain(it, urlResolver) },
        )
    }
}
