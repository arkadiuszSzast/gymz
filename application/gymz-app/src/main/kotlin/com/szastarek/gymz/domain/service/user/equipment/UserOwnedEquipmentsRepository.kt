package com.szastarek.gymz.domain.service.user.equipment

import arrow.core.Option
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipments
import com.szastarek.gymz.shared.security.UserId

interface UserOwnedEquipmentsRepository {
    suspend fun find(userId: UserId): Option<UserOwnedEquipments>
}
