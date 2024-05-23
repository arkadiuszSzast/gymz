package com.szastarek.gymz.adapter.rest.user.equipment

import com.szastarek.gymz.TestFixtures
import com.szastarek.gymz.adapter.rest.user.equipment.request.ChangeUserOwnedEquipmentRequest
import com.szastarek.gymz.adapter.rest.user.equipment.response.UserOwnedEquipmentResponse
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.support.IntegrationTest
import com.szastarek.gymz.support.changeUserOwnedEquipment
import com.szastarek.gymz.support.userOwnedEquipment
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

class UserOwnedEquipmentRoutingKtTest : IntegrationTest() {

    init {

        "should change user owned equipment" { client ->
            // arrange
            val authToken = authenticate().authToken
            val request = TestFixtures.changeUserOwnedEquipmentRequest()

            client.userOwnedEquipment(authToken)
                .body<UserOwnedEquipmentResponse>().equipments.shouldBeEmpty()

            // act
            val response = client.changeUserOwnedEquipment(authToken, request)

            // assert
            response.status shouldBe HttpStatusCode.OK
            client.userOwnedEquipment(authToken)
                .body<UserOwnedEquipmentResponse>()
                .equipments.map { it.id }.shouldContainAll(request.equipmentsIds)
        }

        "should return bad request when changing user owned equipment with invalid equipment id" { client ->
            // arrange
            val authToken = authenticate().authToken
            val request = ChangeUserOwnedEquipmentRequest(
                equipmentsIds = listOf(EquipmentId("invalid")),
            )
            // act
            val response = client.changeUserOwnedEquipment(authToken, request)

            // assert
            response.status shouldBe HttpStatusCode.BadRequest
            client.userOwnedEquipment(authToken)
                .body<UserOwnedEquipmentResponse>().equipments.shouldBeEmpty()
        }
    }
}
