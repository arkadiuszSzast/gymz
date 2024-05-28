package com.szastarek.gymz.adapter.rest.exercise

import com.szastarek.gymz.TestFixtures
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.support.IntegrationTest
import com.szastarek.gymz.support.addGymExercise
import com.szastarek.gymz.support.upload
import io.kotest.matchers.shouldBe

class GymExercisesRoutingKtTest : IntegrationTest() {

    private val imageFileContent = ClassLoader.getSystemResource("./static-files/bench_press.jpg").readBytes()
    private val videoFileContent = ClassLoader.getSystemResource("./static-files/bench_press_video.mp4").readBytes()

    init {
        "should add new gym exercise" { client ->
            // given
            val authToken = authenticate(roles = listOf(Role.ContentEditor)).authToken
            val imageFileUrl = client.upload(authToken, imageFileContent).headers["Location"]!!
            val videoFileUrl = client.upload(authToken, videoFileContent).headers["Location"]!!

            val request = TestFixtures.addGymExerciseRequest(
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

            val request = TestFixtures.addGymExerciseRequest()

            // when
            val response = client.addGymExercise(authToken, request)

            // then
            response.status.value shouldBe 401
        }
    }
}
