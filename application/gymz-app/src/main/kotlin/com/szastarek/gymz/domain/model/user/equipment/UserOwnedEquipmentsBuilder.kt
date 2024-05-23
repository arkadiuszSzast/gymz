package com.szastarek.gymz.domain.model.user.equipment

import arrow.core.Either
import arrow.core.Nel
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.raise.zipOrAccumulate
import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.event.store.model.EventsApplyFailedException
import com.szastarek.gymz.shared.security.UserId
import com.szastarek.gymz.shared.validation.ValidationError
import com.szastarek.gymz.shared.validation.ValidationErrors

class UserOwnedEquipmentsBuilder {
    private var userId: UserId? = null
    private var equipments: List<Equipment>? = null

    fun apply(events: Nel<UserOwnedEquipmentsEvent>): Either<EventsApplyFailedException, UserOwnedEquipments> {
        return events.fold(this) { acc, event -> acc.apply(event) }.build()
    }

    private fun apply(event: UserOwnedEquipmentsEvent): UserOwnedEquipmentsBuilder {
        return when (event) {
            is UserOwnedEquipmentsEvent.Changed -> applyChangedEvent(event)
        }
    }

    private fun applyChangedEvent(event: UserOwnedEquipmentsEvent.Changed): UserOwnedEquipmentsBuilder {
        return this.apply {
            userId = event.userId
            equipments = event.equipments
        }
    }

    private fun build() = either<ValidationErrors, UserOwnedEquipments> {
        zipOrAccumulate(
            { ensureNotNull(userId) { ValidationError(".userId", "user_id_null") } },
            { ensureNotNull(equipments) { ValidationError(".equipments", "equipments_null") } },
            { userIdV, equipmentsV -> UserOwnedEquipments(userIdV, equipmentsV) },
        )
    }.mapLeft { EventsApplyFailedException(it.toString()) }
}
