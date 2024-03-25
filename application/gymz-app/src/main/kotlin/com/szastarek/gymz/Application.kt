package com.szastarek.gymz

import com.szastarek.gymz.adapter.rest.configureRouting
import com.szastarek.gymz.plugins.configureAuthentication
import com.szastarek.gymz.plugins.configureKoin
import com.szastarek.gymz.plugins.configureMonitoring
import com.szastarek.gymz.plugins.configureSerialization
import com.szastarek.gymz.plugins.configureStatusPages
import com.szastarek.gymz.shared.config.ConfigMap
import com.typesafe.config.ConfigFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.server.application.Application
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.config.mergeWith
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.get

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module(authHttpClient: HttpClient = HttpClient(CIO)) {
    ConfigMap.init(HoconApplicationConfig(ConfigFactory.load()).mergeWith(environment.config))
    configureKoin(ConfigMap)
    configureMonitoring(get())
    configureSerialization(get())
    configureAuthentication(get(), get(), authHttpClient)
    configureStatusPages()
    configureRouting(get(), get())
}
