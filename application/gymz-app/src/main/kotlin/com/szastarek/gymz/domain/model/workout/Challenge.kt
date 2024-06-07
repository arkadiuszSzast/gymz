package com.szastarek.gymz.domain.model.workout

import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.serialization.Serializable
import java.util.UUID

@JvmInline
@Serializable
value class ChallengeId(val value: String) {
    companion object {
        fun new(): ChallengeId = ChallengeId(UUID.randomUUID().toString())
    }
}

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
    val id: ChallengeId,
    val name: TranslationKey,
    val description: TranslationKey,
    val days: List<ChallengeDay>,
)
