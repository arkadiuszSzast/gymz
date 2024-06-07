package com.szastarek.gymz.adapter.mongo.workout

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutEntry
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplate
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplateId
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MongoWeeklyWorkoutEntry(
    val day: DayOfWeek,
    val items: List<MongoWorkoutItem>,
    val name: TranslationKey,
) {

    companion object {
        fun fromDomain(weeklyWorkoutEntry: WeeklyWorkoutEntry) = MongoWeeklyWorkoutEntry(
            day = weeklyWorkoutEntry.day,
            items = weeklyWorkoutEntry.items.map { MongoWorkoutItem.fromDomain(it) },
            name = weeklyWorkoutEntry.name,
        )
    }

    fun toDomain(): WeeklyWorkoutEntry = WeeklyWorkoutEntry(
        day = day,
        items = items.map { it.toDomain() },
        name = name,
    )
}

@Serializable
@SerialName("WeeklyWorkoutTemplate")
data class MongoWeeklyWorkoutTemplate(
    @SerialName("_id")
    @Contextual
    val id: WeeklyWorkoutTemplateId,
    val name: TranslationKey,
    val description: TranslationKey,
    val entries: List<MongoWeeklyWorkoutEntry>,
) {

    companion object {
        fun fromDomain(weeklyWorkoutTemplate: WeeklyWorkoutTemplate) = MongoWeeklyWorkoutTemplate(
            id = weeklyWorkoutTemplate.id,
            name = weeklyWorkoutTemplate.name,
            description = weeklyWorkoutTemplate.description,
            entries = weeklyWorkoutTemplate.entries.map { MongoWeeklyWorkoutEntry.fromDomain(it) },
        )
    }

    fun toDomain(): WeeklyWorkoutTemplate = WeeklyWorkoutTemplate(
        id = id,
        name = name,
        description = description,
        entries = entries.map { it.toDomain() },
    )
}
