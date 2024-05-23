package com.szastarek.gymz.domain.model.user.equipment

import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.shared.acl.AclResource
import com.szastarek.gymz.shared.security.UserId
import dev.cerbos.sdk.builders.AttributeValue
import dev.cerbos.sdk.builders.Resource

data class UserOwnedEquipments(
    val userId: UserId,
    val equipments: List<Equipment>,
) : AclResource {

    override val resource: Resource
        get() = Resource.newInstance("user-owned-equipments:object", userId.value)
            .withAttribute("ownerId", AttributeValue.stringValue(userId.value))

    companion object {

        fun empty(userId: UserId) = UserOwnedEquipments(userId, emptyList())

        fun initialize(
            userId: UserId,
            equipments: List<Equipment>,
        ) = UserOwnedEquipmentsEvent.Changed(
            userId,
            equipments,
            UserOwnedEquipmentsEvent.Changed.metadata(userId),
        )

        val resource: Resource = Resource.newInstance("user-owned-equipments:object")
    }

    fun change(equipments: List<Equipment>) = UserOwnedEquipmentsEvent.Changed(
        userId,
        equipments,
        UserOwnedEquipmentsEvent.Changed.metadata(userId),
    )
}
