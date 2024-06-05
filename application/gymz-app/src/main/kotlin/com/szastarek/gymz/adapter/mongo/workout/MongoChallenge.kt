package com.szastarek.gymz.adapter.mongo.workout

import com.szastarek.gymz.domain.model.workout.Challenge
import com.szastarek.gymz.domain.model.workout.ChallengeDay
import com.szastarek.gymz.domain.model.workout.DayCounter
import com.szastarek.gymz.domain.model.workout.WorkoutPlan
import com.szastarek.gymz.domain.model.workout.WorkoutPlanId
import com.szastarek.gymz.shared.i18n.TranslationKey
import kotlinx.serialization.Serializable

@Serializable
data class MongoChallengeDay(
    val day: DayCounter,
    val items: List<MongoWorkoutItem>,
) {

    companion object {
        fun fromDomain(challengeDay: ChallengeDay) = MongoChallengeDay(
            day = challengeDay.day,
            items = challengeDay.items.map { MongoWorkoutItem.fromDomain(it) },
        )
    }

    fun toDomain(): ChallengeDay = ChallengeDay(
        day = day,
        items = items.map { it.toDomain() },
    )
}

@Serializable
data class MongoChallenge(
    override val id: WorkoutPlanId,
    override val name: TranslationKey,
    override val description: TranslationKey,
    val days: List<MongoChallengeDay>,
) : MongoWorkoutPlan {

    companion object {
        fun fromDomain(challenge: Challenge) = MongoChallenge(
            id = challenge.id,
            name = challenge.name,
            description = challenge.description,
            days = challenge.days.map { MongoChallengeDay(day = it.day, items = it.items.map { MongoWorkoutItem.fromDomain(it) }) },
        )
    }

    override fun toDomain(): WorkoutPlan = Challenge(
        id = id,
        name = name,
        description = description,
        days = days.map { it.toDomain() },
    )
}
