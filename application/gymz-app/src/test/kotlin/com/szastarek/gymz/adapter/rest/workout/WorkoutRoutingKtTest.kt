package com.szastarek.gymz.adapter.rest.workout

import com.szastarek.gymz.adapter.rest.workout.request.AddWeeklyWorkoutTemplateRequest
import com.szastarek.gymz.adapter.rest.workout.response.WeeklyWorkoutTemplatePageItem
import com.szastarek.gymz.adapter.rest.workout.response.WeeklyWorkoutTemplateResponse
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.domain.service.exercise.GymExerciseRepository
import com.szastarek.gymz.fixtures.ExerciseTestFixtures
import com.szastarek.gymz.fixtures.WorkoutTestFixtures
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageNumber
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.page.PageSize
import com.szastarek.gymz.shared.security.Jwt
import com.szastarek.gymz.support.IntegrationTest
import com.szastarek.gymz.support.addWeeklyWorkoutTemplate
import com.szastarek.gymz.support.getAllWeeklyWorkouts
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import org.koin.test.inject

class WorkoutRoutingKtTest : IntegrationTest() {

    private val gymExerciseRepository: GymExerciseRepository by inject()

    init {

        "should add weekly workout template" { client ->
            // arrange
            val selfWeightGymExercise = ExerciseTestFixtures.gymExercise().also { gymExerciseRepository.save(it) }
            val weightBasedGymExercise = ExerciseTestFixtures.gymExercise().also { gymExerciseRepository.save(it) }
            val authToken = authenticate(roles = listOf(Role.ContentEditor)).authToken
            val request = WorkoutTestFixtures.addWeeklyWorkoutRequest(
                entries = listOf(
                    WorkoutTestFixtures.weeklyWorkoutEntryRequestModel(
                        items = listOf(
                            WorkoutTestFixtures.workoutSelfWeightExerciseRequestModel(exerciseId = selfWeightGymExercise.id),
                            WorkoutTestFixtures.workoutWeightBasedExerciseRequestModel(exerciseId = weightBasedGymExercise.id),
                            WorkoutTestFixtures.workoutBreakRequestModel(),
                        ),
                    ),
                ),
            )

            // act && assert
            client.addWeeklyWorkoutTemplate(authToken, request).status.value shouldBe 201
        }

        "should not add weekly workout template when gym exercise not found" { client ->
            // arrange
            val authToken = authenticate(roles = listOf(Role.ContentEditor)).authToken
            val request = WorkoutTestFixtures.addWeeklyWorkoutRequest(
                entries = listOf(
                    WorkoutTestFixtures.weeklyWorkoutEntryRequestModel(
                        items = listOf(
                            WorkoutTestFixtures.workoutSelfWeightExerciseRequestModel(exerciseId = GymExerciseId("not-found")),
                        ),
                    ),
                ),
            )

            client.addWeeklyWorkoutTemplate(authToken, request).status.value shouldBe 400
        }

        "should return paginated weekly workout templates" { client ->
            // arrange
            val contentEditorAuthToken = authenticate(roles = listOf(Role.ContentEditor)).authToken
            val userAuthToken = authenticate(roles = listOf(Role.User)).authToken
            createWeeklyWorkoutTemplate(client, contentEditorAuthToken)
            createWeeklyWorkoutTemplate(client, contentEditorAuthToken)
            createWeeklyWorkoutTemplate(client, contentEditorAuthToken)
            val firstPageParameters = PageQueryParameters(PageSize(2), PageNumber(1))
            val secondPageParameters = PageQueryParameters(PageSize(2), PageNumber(2))

            // act
            val firstPageResponse = client.getAllWeeklyWorkouts(userAuthToken, firstPageParameters)
            val secondPageResponse = client.getAllWeeklyWorkouts(userAuthToken, secondPageParameters)

            // assert
            firstPageResponse.status.value shouldBe 200
            val firstPageBody = firstPageResponse.body<Page<WeeklyWorkoutTemplatePageItem>>()
            firstPageBody.data.size shouldBe 2
            firstPageBody.isLastPage shouldBe false

            secondPageResponse.status.value shouldBe 200
            val secondPageBody = secondPageResponse.body<Page<WeeklyWorkoutTemplatePageItem>>()
            secondPageBody.data.size shouldBe 1
            secondPageBody.isLastPage shouldBe true

            firstPageBody.data.shouldNotContainAnyOf(secondPageBody.data)
        }

        "should return weekly workout template by id" { client ->
            // given
            val contentEditorAuthToken = authenticate(roles = listOf(Role.ContentEditor)).authToken
            val userAuthToken = authenticate(roles = listOf(Role.User)).authToken
            val createWeeklyWorkoutRequest = WorkoutTestFixtures.addOnlyBreakWeeklyWorkoutRequest()
            val createWeeklyWorkoutTemplateResponse =
                createWeeklyWorkoutTemplate(client, contentEditorAuthToken, createWeeklyWorkoutRequest)
            val weeklyWorkoutTemplateUrl = createWeeklyWorkoutTemplateResponse.headers[HttpHeaders.Location]!!

            // when
            val response = client.get(weeklyWorkoutTemplateUrl) {
                bearerAuth(userAuthToken.value)
            }

            // then
            response.status.value shouldBe 200
            response.body<WeeklyWorkoutTemplateResponse>().shouldBeEqualTo(createWeeklyWorkoutRequest)
        }
    }

    private suspend fun createWeeklyWorkoutTemplate(
        client: HttpClient,
        authToken: Jwt,
        request: AddWeeklyWorkoutTemplateRequest = WorkoutTestFixtures.addOnlyBreakWeeklyWorkoutRequest(),
    ): HttpResponse =
        client.addWeeklyWorkoutTemplate(
            authToken,
            request,
        ).also { it.status shouldBe HttpStatusCode.Created }

    private fun WeeklyWorkoutTemplateResponse.shouldBeEqualTo(request: AddWeeklyWorkoutTemplateRequest) {
        this.should {
            this.name shouldBe request.name
            this.description shouldBe request.description
            this.entries.size shouldBe request.entries.size
        }
    }
}
