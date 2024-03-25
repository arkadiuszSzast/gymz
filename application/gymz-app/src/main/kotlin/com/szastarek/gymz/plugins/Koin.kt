package com.szastarek.gymz.plugins

import com.szastarek.gymz.auth.JwtAuthTokenProvider
import com.szastarek.gymz.auth.JwtIdTokenProvider
import com.szastarek.gymz.config.JwtAuthTokenProperties
import com.szastarek.gymz.config.JwtIdTokenProperties
import com.szastarek.gymz.config.MonitoringProperties
import com.szastarek.gymz.config.ZitadelProperties
import com.szastarek.gymz.query.handler.UserInfoQueryHandler
import com.szastarek.gymz.shared.config.ConfigMap
import com.szastarek.gymz.shared.json.JsonProvider
import com.szastarek.gymz.shared.mediator.TracingPipelineBehavior
import com.trendyol.kediatr.koin.KediatRKoin
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
}

internal val coreModule = module {
    single { JsonProvider.instance }
    singleOf(::TracingPipelineBehavior)
    single { KediatRKoin.getMediator() }
    single { Clock.System } bind Clock::class
    single { GlobalOpenTelemetry.get() }
}

internal val gymzModule = module {
    singleOf(::JwtAuthTokenProvider)
    singleOf(::JwtIdTokenProvider)
    singleOf(::UserInfoQueryHandler)
}

internal fun Application.configureKoin(config: ConfigMap) {
    startKoin { modules(configurationModule(config), coreModule, gymzModule) }
}
