package com.szastarek.gymz.adapter.rest.workout.response

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlan
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlanId
import com.szastarek.gymz.file.storage.FileUrlResolver
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.serialization.Serializable

@Serializable
data class WeeklyWorkoutPlanResponse(
    val id: WeeklyWorkoutPlanId,
    val name: TranslationKey,
    val description: TranslationKey,
    val entries: List<WeeklyWorkoutEntryResponseModel>,
) {
    companion object {
        fun from(weeklyWorkoutPlan: WeeklyWorkoutPlan, urlResolver: FileUrlResolver) = WeeklyWorkoutPlanResponse(
            id = weeklyWorkoutPlan.id,
            name = weeklyWorkoutPlan.name,
            description = weeklyWorkoutPlan.description,
            entries = weeklyWorkoutPlan.entries.map { WeeklyWorkoutEntryResponseModel.fromDomain(it, urlResolver) },
        )
    }
}
