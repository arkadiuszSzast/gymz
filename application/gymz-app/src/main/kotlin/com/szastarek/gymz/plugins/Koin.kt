package com.szastarek.gymz.plugins

import com.szastarek.gymz.auth.JwtAuthTokenProvider
import com.szastarek.gymz.auth.JwtIdTokenProvider
import com.szastarek.gymz.config.JwtAuthTokenProperties
import com.szastarek.gymz.config.JwtIdTokenProperties
import com.szastarek.gymz.config.MonitoringProperties
import com.szastarek.gymz.config.ZitadelProperties
import com.szastarek.gymz.query.handler.UserInfoQueryHandler
import com.szastarek.gymz.shared.config.ConfigKey
import com.szastarek.gymz.shared.config.getBooleanProperty
import com.szastarek.gymz.shared.config.getStringProperty
import com.szastarek.gymz.shared.json.JsonProvider
import com.szastarek.gymz.shared.mediator.TracingPipelineBehavior
import com.szastarek.gymz.shared.security.ClientId
import com.szastarek.gymz.shared.security.JwtAudience
import com.szastarek.gymz.shared.security.JwtIssuer
import com.szastarek.gymz.shared.security.JwtRealm
import com.szastarek.gymz.shared.security.MaskedString
import com.trendyol.kediatr.koin.KediatRKoin
import io.ktor.http.Url
import io.ktor.server.application.Application
import io.opentelemetry.api.GlobalOpenTelemetry
import kotlinx.datetime.Clock
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val configurationModule = module {
    single {
        JwtAuthTokenProperties(
            audience = JwtAudience(getStringProperty(ConfigKey("jwt.authentication.audience"))),
            issuer = JwtIssuer(getStringProperty(ConfigKey("jwt.authentication.issuer"))),
            realm = JwtRealm(getStringProperty(ConfigKey("jwt.authentication.realm"))),
            secret = MaskedString(getStringProperty(ConfigKey("jwt.authentication.secret"))),
        )
    }
    single {
        JwtIdTokenProperties(
            audience = JwtAudience(getStringProperty(ConfigKey("jwt.id.audience"))),
            issuer = JwtIssuer(getStringProperty(ConfigKey("jwt.id.issuer"))),
            realm = JwtRealm(getStringProperty(ConfigKey("jwt.id.realm"))),
            secret = MaskedString(getStringProperty(ConfigKey("jwt.id.secret"))),
        )
    }
    single {
        ZitadelProperties(
            baseUrl = Url(getStringProperty(ConfigKey("zitadel.baseUrl"))),
            clientSecret = MaskedString(getStringProperty(ConfigKey("zitadel.clientSecret"))),
            clientId = ClientId(getStringProperty(ConfigKey("zitadel.clientId"))),
        )
    }
    single {
        MonitoringProperties(
            enabled = getBooleanProperty(ConfigKey("monitoring.enabled")),
            otelMetricsUrl = getStringProperty(ConfigKey("monitoring.otel.metrics.url")),
        )
    }
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

internal fun Application.configureKoin() {
    startKoin { modules(configurationModule, coreModule, gymzModule) }
}
