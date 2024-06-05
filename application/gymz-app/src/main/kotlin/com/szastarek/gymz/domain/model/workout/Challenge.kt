package com.szastarek.gymz.domain.model.workout

import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class DayCounter(val value: UInt) {
    companion object {
        fun first(): DayCounter = DayCounter(1U)
    }
}

data class ChallengeDay(
    val day: DayCounter,
    val items: List<WorkoutItem>,
)

data class Challenge(
    override val id: WorkoutPlanId,
    override val name: TranslationKey,
    override val description: TranslationKey,
    val days: List<ChallengeDay>,
) : WorkoutPlan
