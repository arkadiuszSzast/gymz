package com.szastarek.gymz.adapter.rest.workout.response

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutEntry
import com.szastarek.gymz.file.storage.FileUrlResolver
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
data class WeeklyWorkoutEntryResponseModel(
    val day: DayOfWeek,
    val items: List<WorkoutItemResponseModel>,
    val name: TranslationKey,
) {
    companion object {
        fun fromDomain(weeklyWorkoutEntry: WeeklyWorkoutEntry, urlResolver: FileUrlResolver): WeeklyWorkoutEntryResponseModel =
            WeeklyWorkoutEntryResponseModel(
                day = weeklyWorkoutEntry.day,
                items = weeklyWorkoutEntry.items.map { WorkoutItemResponseModel.fromDomain(it, urlResolver) },
                name = weeklyWorkoutEntry.name,
            )
    }
}
