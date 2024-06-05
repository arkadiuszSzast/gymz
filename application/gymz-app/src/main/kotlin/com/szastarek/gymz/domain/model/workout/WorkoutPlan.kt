package com.szastarek.gymz.domain.model.workout

import com.szastarek.gymz.shared.acl.AclResource
import com.szastarek.gymz.shared.i18n.TranslationKey
import dev.cerbos.sdk.builders.Resource
import kotlinx.serialization.Serializable
import java.util.UUID

@JvmInline
@Serializable
value class WorkoutPlanId(val value: String) {
    companion object {
        fun new(): WorkoutPlanId = WorkoutPlanId(UUID.randomUUID().toString())
    }
}

sealed interface WorkoutPlan : AclResource {
    val id: WorkoutPlanId
    val name: TranslationKey
    val description: TranslationKey

    override val resource: Resource
        get() = Resource.newInstance("workout-plan:object", id.value)

    companion object {
        val resource: Resource = Resource.newInstance("workout-plan:object")
    }
}
