package com.szastarek.gymz.adapter.rest.equipment

import com.szastarek.gymz.domain.model.equipment.SupportedEquipments
import com.szastarek.gymz.support.IntegrationTest
import com.szastarek.gymz.support.supportedEquipments
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

class SupportedEquipmentsRoutingKtTest : IntegrationTest() {

    init {

        "should return 401 when trying to access /equipments without access token" { client ->
            // act && assert
            client.get(SUPPORTED_EQUIPMENTS_API_PREFIX).status shouldBe HttpStatusCode.Unauthorized
        }

        "should return all equipments" { client ->
            // arrange
            val loginResponse = authenticate()

            // act
            val response = client.supportedEquipments(loginResponse.authToken)

            // assert
            response.status shouldBe HttpStatusCode.OK
            response.body<SupportedEquipments>().equipments.shouldNotBeEmpty()
        }
    }
}
