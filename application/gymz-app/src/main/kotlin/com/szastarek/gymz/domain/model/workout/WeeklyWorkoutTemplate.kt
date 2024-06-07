package com.szastarek.gymz.domain.model.workout

import com.szastarek.gymz.shared.acl.AclResource
import com.szastarek.gymz.shared.i18n.TranslationKey
import dev.cerbos.sdk.builders.Resource
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import java.util.UUID

@JvmInline
@Serializable
value class WeeklyWorkoutTemplateId(val value: String) {
    companion object {
        fun new(): WeeklyWorkoutTemplateId = WeeklyWorkoutTemplateId(UUID.randomUUID().toString())
    }
}

data class WeeklyWorkoutEntry(
    val day: DayOfWeek,
    val items: List<WorkoutItem>,
    val name: TranslationKey,
)

data class WeeklyWorkoutTemplate(
    val id: WeeklyWorkoutTemplateId,
    val name: TranslationKey,
    val description: TranslationKey,
    val entries: List<WeeklyWorkoutEntry>,
) : AclResource {
    companion object {
        val resource: Resource = Resource.newInstance("weekly-workout-template:object")

        fun create(
            name: TranslationKey,
            description: TranslationKey,
            entries: List<WeeklyWorkoutEntry>,
        ): WeeklyWorkoutTemplate = WeeklyWorkoutTemplate(
            id = WeeklyWorkoutTemplateId.new(),
            name = name,
            description = description,
            entries = entries,
        )
    }

    override val resource: Resource
        get() = Resource.newInstance("weekly-workout-template:object", id.value)
}
