package com.szastarek.gymz.adapter.mongo.workout

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutEntry
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutPlan
import com.szastarek.gymz.domain.model.workout.WorkoutPlan
import com.szastarek.gymz.domain.model.workout.WorkoutPlanId
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.datetime.DayOfWeek
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
data class MongoWeeklyWorkoutPlan(
    override val id: WorkoutPlanId,
    override val name: TranslationKey,
    override val description: TranslationKey,
    val entries: List<MongoWeeklyWorkoutEntry>,
) : MongoWorkoutPlan {

    companion object {
        fun fromDomain(weeklyWorkoutPlan: WeeklyWorkoutPlan) = MongoWeeklyWorkoutPlan(
            id = weeklyWorkoutPlan.id,
            name = weeklyWorkoutPlan.name,
            description = weeklyWorkoutPlan.description,
            entries = weeklyWorkoutPlan.entries.map { MongoWeeklyWorkoutEntry.fromDomain(it) },
        )
    }

    override fun toDomain(): WorkoutPlan = WeeklyWorkoutPlan(
        id = id,
        name = name,
        description = description,
        entries = entries.map { it.toDomain() },
    )
}
