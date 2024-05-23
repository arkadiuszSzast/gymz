package com.szastarek.gymz.domain.service.user.equipment.command

import arrow.core.EitherNel
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.CommandWithResult

typealias ChangeUserOwnedEquipmentCommandResult = EitherNel<ChangeUserOwnedEquipmentCommandError, ChangeUserOwnedEquipmentCommandSuccessResult>

data object ChangeUserOwnedEquipmentCommandSuccessResult

data class ChangeUserOwnedEquipmentCommand(
    val userContext: UserContext,
    val equipmentsIds: List<EquipmentId>,
) : CommandWithResult<ChangeUserOwnedEquipmentCommandResult>

enum class ChangeUserOwnedEquipmentCommandError {
    EquipmentNotFound,
}
