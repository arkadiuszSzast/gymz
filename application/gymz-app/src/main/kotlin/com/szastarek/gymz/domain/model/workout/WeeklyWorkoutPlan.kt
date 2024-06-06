package com.szastarek.gymz.domain.model.workout

import com.szastarek.gymz.shared.acl.AclResource
import com.szastarek.gymz.shared.i18n.TranslationKey
import dev.cerbos.sdk.builders.Resource
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import java.util.UUID

@JvmInline
@Serializable
value class WeeklyWorkoutPlanId(val value: String) {
    companion object {
        fun new(): WeeklyWorkoutPlanId = WeeklyWorkoutPlanId(UUID.randomUUID().toString())
    }
}

data class WeeklyWorkoutEntry(
    val day: DayOfWeek,
    val items: List<WorkoutItem>,
    val name: TranslationKey,
)

data class WeeklyWorkoutPlan(
    val id: WeeklyWorkoutPlanId,
    val name: TranslationKey,
    val description: TranslationKey,
    val entries: List<WeeklyWorkoutEntry>,
) : AclResource {
    companion object {
        val resource: Resource = Resource.newInstance("weekly-workout-plan:object")

        fun create(
            name: TranslationKey,
            description: TranslationKey,
            entries: List<WeeklyWorkoutEntry>,
        ): WeeklyWorkoutPlan = WeeklyWorkoutPlan(
            id = WeeklyWorkoutPlanId.new(),
            name = name,
            description = description,
            entries = entries,
        )
    }

    override val resource: Resource
        get() = Resource.newInstance("weekly-workout-plan:object", id.value)
}
