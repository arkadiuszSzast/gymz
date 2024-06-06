package com.szastarek.gymz.adapter.mongo.workout

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutEntry
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlan
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlanId
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.datetime.DayOfWeek
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
@SerialName("WeeklyWorkoutPlan")
data class MongoWeeklyWorkoutPlan(
    val id: WeeklyWorkoutPlanId,
    val name: TranslationKey,
    val description: TranslationKey,
    val entries: List<MongoWeeklyWorkoutEntry>,
) {

    companion object {
        fun fromDomain(weeklyWorkoutPlan: WeeklyWorkoutPlan) = MongoWeeklyWorkoutPlan(
            id = weeklyWorkoutPlan.id,
            name = weeklyWorkoutPlan.name,
            description = weeklyWorkoutPlan.description,
            entries = weeklyWorkoutPlan.entries.map { MongoWeeklyWorkoutEntry.fromDomain(it) },
        )
    }

    fun toDomain(): WeeklyWorkoutPlan = WeeklyWorkoutPlan(
        id = id,
        name = name,
        description = description,
        entries = entries.map { it.toDomain() },
    )
}
