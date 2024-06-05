package com.szastarek.gymz.domain.model.workout

import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.datetime.DayOfWeek

data class WeeklyWorkoutEntry(
    val day: DayOfWeek,
    val items: List<WorkoutItem>,
    val name: TranslationKey,
)

data class WeeklyWorkoutPlan(
    override val id: WorkoutPlanId,
    override val name: TranslationKey,
    override val description: TranslationKey,
    val entries: List<WeeklyWorkoutEntry>,
) : WorkoutPlan {
    companion object {
        fun create(
            name: TranslationKey,
            description: TranslationKey,
            entries: List<WeeklyWorkoutEntry>,
        ): WeeklyWorkoutPlan = WeeklyWorkoutPlan(
            id = WorkoutPlanId.new(),
            name = name,
            description = description,
            entries = entries,
        )
    }
}
