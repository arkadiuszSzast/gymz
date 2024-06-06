package com.szastarek.gymz.adapter.rest.exercise

import com.szastarek.gymz.adapter.rest.exercise.request.AddGymExerciseRequest
import com.szastarek.gymz.adapter.rest.exercise.response.GymExercisePageItem
import com.szastarek.gymz.adapter.rest.exercise.response.GymExerciseResponse
import com.szastarek.gymz.fixtures.ExerciseTestFixtures
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageNumber
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.page.PageSize
import com.szastarek.gymz.support.IntegrationTest
import com.szastarek.gymz.support.addGymExercise
import com.szastarek.gymz.support.getAllGymExercises
import com.szastarek.gymz.support.upload
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders

class GymExercisesRoutingKtTest : IntegrationTest() {

    private val imageFileContent = ClassLoader.getSystemResource("./static-files/bench_press.jpg").readBytes()
    private val videoFileContent = ClassLoader.getSystemResource("./static-files/bench_press_video.mp4").readBytes()

    init {
        "should add new gym exercise" { client ->
            // given
            val authToken = authenticate(roles = listOf(Role.ContentEditor)).authToken
            val imageFileUrl = client.upload(authToken, imageFileContent).headers["Location"]!!
            val videoFileUrl = client.upload(authToken, videoFileContent).headers["Location"]!!

            val request = ExerciseTestFixtures.addGymExerciseRequest(
                imageUrl = imageFileUrl,
                videoUrl = videoFileUrl,
            )

            // when
            val response = client.addGymExercise(authToken, request)

            // then
            response.status.value shouldBe 201
        }

        "user cannot add new exercise" { client ->
            // given
            val authToken = authenticate(roles = listOf(Role.User)).authToken

            val request = ExerciseTestFixtures.addGymExerciseRequest()

            // when
            val response = client.addGymExercise(authToken, request)

            // then
            response.status.value shouldBe 401
        }

        "should return paginated gym exercises" { client ->
            // given
            val contentEditorAuthToken = authenticate(roles = listOf(Role.ContentEditor)).authToken
            val userAuthToken = authenticate(roles = listOf(Role.User)).authToken
            client.addGymExercise(contentEditorAuthToken, ExerciseTestFixtures.addGymExerciseRequest())
            client.addGymExercise(contentEditorAuthToken, ExerciseTestFixtures.addGymExerciseRequest())
            client.addGymExercise(contentEditorAuthToken, ExerciseTestFixtures.addGymExerciseRequest())
            val firstPageParameters = PageQueryParameters(PageSize(2), PageNumber(1))
            val secondPageParameters = PageQueryParameters(PageSize(2), PageNumber(2))

            // when
            val firstPageResponse = client.getAllGymExercises(userAuthToken, firstPageParameters)
            val secondPageResponse = client.getAllGymExercises(userAuthToken, secondPageParameters)

            // assert
            firstPageResponse.status.value shouldBe 200
            val firstPageBody = firstPageResponse.body<Page<GymExercisePageItem>>()
            firstPageBody.data.size shouldBe 2
            firstPageBody.isLastPage shouldBe false

            secondPageResponse.status.value shouldBe 200
            val secondPageBody = secondPageResponse.body<Page<GymExercisePageItem>>()
            secondPageBody.data.size shouldBe 1
            secondPageBody.isLastPage shouldBe true

            firstPageBody.data.shouldNotContainAnyOf(secondPageBody.data)
        }

        "should return gym exercise by id" { client ->
            // given
            val contentEditorAuthToken = authenticate(roles = listOf(Role.ContentEditor)).authToken
            val userAuthToken = authenticate(roles = listOf(Role.User)).authToken
            val addGymExerciseRequest = ExerciseTestFixtures.addGymExerciseRequest()
            val addGymExerciseResponse = client.addGymExercise(contentEditorAuthToken, addGymExerciseRequest)
            val gymExerciseUrl = addGymExerciseResponse.headers[HttpHeaders.Location]!!

            // when
            val response = client.get(gymExerciseUrl) {
                bearerAuth(userAuthToken.value)
            }

            // then
            response.status.value shouldBe 200
            response.body<GymExerciseResponse>().shouldBeEqualTo(addGymExerciseRequest)
        }
    }
}

private fun GymExerciseResponse.shouldBeEqualTo(request: AddGymExerciseRequest) {
    this.should {
        this.name shouldBe request.name
        this.description shouldBe request.description
        this.primaryMusclesGroups shouldBe request.primaryMusclesGroups
        this.secondaryMusclesGroups shouldBe request.secondaryMusclesGroups
        this.requiredEquipments.map { it.id } shouldBe request.requiredEquipmentsIds
        this.tags shouldBe request.tags
    }
}
