package com.szastarek.gymz.adapter.koin

import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.net.url.Url
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString.parseOrThrow
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.szastarek.gymz.adapter.cerbos.CerbosAccessManager
import com.szastarek.gymz.adapter.event.store.TracingEventStoreReadClient
import com.szastarek.gymz.adapter.event.store.TracingEventStoreSubscribeClient
import com.szastarek.gymz.adapter.event.store.TracingEventStoreWriteClient
import com.szastarek.gymz.adapter.eventstore.user.equipment.EventStoreUserOwnedEquipmentsRepository
import com.szastarek.gymz.adapter.mongo.equipment.MongoEquipment
import com.szastarek.gymz.adapter.mongo.equipment.SupportedEquipmentMongoRepository
import com.szastarek.gymz.adapter.mongo.exercise.GymExerciseMongoRepository
import com.szastarek.gymz.adapter.mongo.exercise.MongoGymExercise
import com.szastarek.gymz.adapter.mongo.workout.MongoWorkoutPlan
import com.szastarek.gymz.adapter.mongo.workout.WorkoutPlanMongoRepository
import com.szastarek.gymz.config.CerbosProperties
import com.szastarek.gymz.config.JwtAuthTokenProperties
import com.szastarek.gymz.config.JwtIdTokenProperties
import com.szastarek.gymz.config.MongoProperties
import com.szastarek.gymz.config.MonitoringProperties
import com.szastarek.gymz.config.SwaggerProperties
import com.szastarek.gymz.config.ZitadelProperties
import com.szastarek.gymz.domain.service.equipment.SupportedEquipmentRepository
import com.szastarek.gymz.domain.service.equipment.query.SupportedEquipmentsQueryHandler
import com.szastarek.gymz.domain.service.exercise.GymExerciseRepository
import com.szastarek.gymz.domain.service.exercise.command.handler.AddGymExerciseCommandHandler
import com.szastarek.gymz.domain.service.exercise.query.handler.FindAllGymExercisesQueryHandler
import com.szastarek.gymz.domain.service.exercise.query.handler.FindGymExerciseByIdQueryHandler
import com.szastarek.gymz.domain.service.upload.command.handler.UploadCommandHandler
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.equipment.UserOwnedEquipmentsRepository
import com.szastarek.gymz.domain.service.user.equipment.command.handler.ChangeUserOwnedEquipmentCommandHandler
import com.szastarek.gymz.domain.service.user.equipment.query.handler.UserOwnedEquipmentQueryHandler
import com.szastarek.gymz.domain.service.user.query.handler.UserInfoQueryHandler
import com.szastarek.gymz.domain.service.workout.WorkoutPlanRepository
import com.szastarek.gymz.domain.service.workout.command.handler.AddWeeklyWorkoutPlanCommandHandler
import com.szastarek.gymz.event.store.adapter.EventStoreDbReadClient
import com.szastarek.gymz.event.store.adapter.EventStoreDbSubscribeClient
import com.szastarek.gymz.event.store.adapter.EventStoreDbWriteClient
import com.szastarek.gymz.event.store.config.EventStoreProperties
import com.szastarek.gymz.event.store.service.EventStoreReadClient
import com.szastarek.gymz.event.store.service.EventStoreSubscribeClient
import com.szastarek.gymz.event.store.service.EventStoreWriteClient
import com.szastarek.gymz.file.storage.BucketNameResolver
import com.szastarek.gymz.file.storage.FileStorage
import com.szastarek.gymz.file.storage.FileUrlResolver
import com.szastarek.gymz.file.storage.PrefixBucketNameResolver
import com.szastarek.gymz.file.storage.S3FileStorage
import com.szastarek.gymz.file.storage.S3FileUrlResolver
import com.szastarek.gymz.file.storage.config.S3Properties
import com.szastarek.gymz.service.auth.JwtAuthTokenProvider
import com.szastarek.gymz.service.auth.JwtIdTokenProvider
import com.szastarek.gymz.shared.config.ConfigMap
import com.szastarek.gymz.shared.json.JsonProvider
import com.szastarek.gymz.shared.mediator.TracingPipelineBehavior
import com.trendyol.kediatr.koin.KediatRKoin
import dev.cerbos.sdk.CerbosClientBuilder
import io.ktor.client.HttpClient
import io.ktor.events.Events
import io.ktor.server.application.Application
import io.opentelemetry.api.GlobalOpenTelemetry
import kotlinx.datetime.Clock
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal fun configurationModule(config: ConfigMap) = module {
    single { JwtAuthTokenProperties.create(config) }
    single { JwtIdTokenProperties.create(config) }
    single { ZitadelProperties.create(config) }
    single { MonitoringProperties.create(config) }
    single { CerbosProperties.create(config) }
    single { EventStoreProperties.create(config) }
    single { S3Properties.create(config) }
    single { MongoProperties.create(config) }
    single { SwaggerProperties.create(config) }
}

internal fun coreModule(applicationEvents: Events) = module {
    single { JsonProvider.instance }
    singleOf(::TracingPipelineBehavior)
    single { KediatRKoin.getMediator() }
    single { Clock.System } bind Clock::class
    single { GlobalOpenTelemetry.get() }
    single { CerbosClientBuilder(get<CerbosProperties>().connectionString).withPlaintext().buildBlockingClient() }
    single { CerbosAccessManager(get()) } bind AccessManager::class
    single { EventStoreDBClient.create(parseOrThrow(get<EventStoreProperties>().connectionString)) }
    single { TracingEventStoreReadClient(EventStoreDbReadClient(get(), get()), get()) } bind EventStoreReadClient::class
    single {
        TracingEventStoreWriteClient(
            EventStoreDbWriteClient(get(), get()),
            get(),
        )
    } bind EventStoreWriteClient::class
    single {
        TracingEventStoreSubscribeClient(
            EventStoreDbSubscribeClient(get(), get(), applicationEvents),
            get(),
        )
    } bind EventStoreSubscribeClient::class
}

internal fun uploadsModule(uploadsHttpClient: HttpClient) = module {
    single { PrefixBucketNameResolver(get<S3Properties>().bucketPrefix) } bind BucketNameResolver::class
    single { S3FileUrlResolver(get(), get()) } bind FileUrlResolver::class
    single {
        S3Client {
            forcePathStyle = true
            endpointUrl = Url.parse(get<S3Properties>().s3Endpoint)
            region = get<S3Properties>().region
            credentialsProvider = get<S3Properties>().credentialsProvider
        }
    }
    single { S3FileStorage(uploadsHttpClient, get(), get(), get()) } bind FileStorage::class
    singleOf(::UploadCommandHandler)
}

internal val mongoModule = module {
    single { MongoClient.create(get<MongoProperties>().connectionString) }
    single { get<MongoClient>().getDatabase(get<MongoProperties>().database) }
    single {
        SupportedEquipmentMongoRepository(
            get<MongoDatabase>().getCollection<MongoEquipment>(SupportedEquipmentMongoRepository.COLLECTION_NAME),
        )
    } bind SupportedEquipmentRepository::class
    single {
        GymExerciseMongoRepository(get<MongoDatabase>().getCollection<MongoGymExercise>(GymExerciseMongoRepository.COLLECTION_NAME))
    } bind GymExerciseRepository::class
    single {
        WorkoutPlanMongoRepository(
            get<MongoDatabase>().getCollection<MongoWorkoutPlan>(WorkoutPlanMongoRepository.COLLECTION_NAME),
        )
    } bind WorkoutPlanRepository::class
}

internal val gymzModule = module {
    singleOf(::JwtAuthTokenProvider)
    singleOf(::JwtIdTokenProvider)
    singleOf(::UserInfoQueryHandler)
    singleOf(::SupportedEquipmentsQueryHandler)
    singleOf(::EventStoreUserOwnedEquipmentsRepository) bind UserOwnedEquipmentsRepository::class
    singleOf(::ChangeUserOwnedEquipmentCommandHandler)
    singleOf(::UserOwnedEquipmentQueryHandler)
    singleOf(::AddGymExerciseCommandHandler)
    singleOf(::FindAllGymExercisesQueryHandler)
    singleOf(::FindGymExerciseByIdQueryHandler)
    singleOf(::AddWeeklyWorkoutPlanCommandHandler)
}

internal fun Application.configureKoin(config: ConfigMap, applicationEvents: Events, uploadsHttpClient: HttpClient) {
    startKoin {
        modules(
            configurationModule(config),
            coreModule(applicationEvents),
            uploadsModule(uploadsHttpClient),
            mongoModule,
            gymzModule,
        )
    }
}
