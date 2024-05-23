package com.szastarek.gymz.domain.service.user.equipment.query

import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipments
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.Query

data class UserOwnedEquipmentQuery(val userContext: UserContext) : Query<UserOwnedEquipments>
