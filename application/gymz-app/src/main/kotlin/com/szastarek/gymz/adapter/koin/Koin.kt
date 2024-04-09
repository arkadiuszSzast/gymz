package com.szastarek.gymz.adapter.koin

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString.parseOrThrow
import com.szastarek.gymz.adapter.event.store.TracingEventStoreReadClient
import com.szastarek.gymz.adapter.event.store.TracingEventStoreSubscribeClient
import com.szastarek.gymz.adapter.event.store.TracingEventStoreWriteClient
import com.szastarek.gymz.config.CerbosProperties
import com.szastarek.gymz.config.JwtAuthTokenProperties
import com.szastarek.gymz.config.JwtIdTokenProperties
import com.szastarek.gymz.config.MonitoringProperties
import com.szastarek.gymz.config.ZitadelProperties
import com.szastarek.gymz.domain.service.query.handler.UserInfoQueryHandler
import com.szastarek.gymz.event.store.adapter.EventStoreDbReadClient
import com.szastarek.gymz.event.store.adapter.EventStoreDbSubscribeClient
import com.szastarek.gymz.event.store.adapter.EventStoreDbWriteClient
import com.szastarek.gymz.event.store.config.EventStoreProperties
import com.szastarek.gymz.service.auth.JwtAuthTokenProvider
import com.szastarek.gymz.service.auth.JwtIdTokenProvider
import com.szastarek.gymz.shared.config.ConfigMap
import com.szastarek.gymz.shared.json.JsonProvider
import com.szastarek.gymz.shared.mediator.TracingPipelineBehavior
import com.trendyol.kediatr.koin.KediatRKoin
import dev.cerbos.sdk.CerbosClientBuilder
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
}

internal fun coreModule(applicationEvents: Events) = module {
    single { JsonProvider.instance }
    singleOf(::TracingPipelineBehavior)
    single { KediatRKoin.getMediator() }
    single { Clock.System } bind Clock::class
    single { GlobalOpenTelemetry.get() }
    single { CerbosClientBuilder(get<CerbosProperties>().connectionString).withPlaintext().buildBlockingClient() }
    single { EventStoreDBClient.create(parseOrThrow(get<EventStoreProperties>().connectionString)) }
    single { TracingEventStoreReadClient(EventStoreDbReadClient(get(), get()), get()) }
    single { TracingEventStoreWriteClient(EventStoreDbWriteClient(get(), get()), get()) }
    single { TracingEventStoreSubscribeClient(EventStoreDbSubscribeClient(get(), get(), applicationEvents), get()) }
}

internal val gymzModule = module {
    singleOf(::JwtAuthTokenProvider)
    singleOf(::JwtIdTokenProvider)
    singleOf(::UserInfoQueryHandler)
}

internal fun Application.configureKoin(config: ConfigMap, applicationEvents: Events) {
    startKoin { modules(configurationModule(config), coreModule(applicationEvents), gymzModule) }
}
