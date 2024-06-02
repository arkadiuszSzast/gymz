package com.szastarek.gymz

import com.mongodb.reactivestreams.client.MongoClients
import com.szastarek.gymz.adapter.health.configureHealthz
import com.szastarek.gymz.adapter.koin.configureKoin
import com.szastarek.gymz.adapter.mongo.migration.configureMongock
import com.szastarek.gymz.adapter.rest.configureOAuthRouting
import com.szastarek.gymz.adapter.rest.equipment.supportedEquipmentsRouting
import com.szastarek.gymz.adapter.rest.exercise.gymExercisesRouting
import com.szastarek.gymz.adapter.rest.swaggerRouting
import com.szastarek.gymz.adapter.rest.uploadsRouting
import com.szastarek.gymz.adapter.rest.user.equipment.userOwnedEquipmentsRouting
import com.szastarek.gymz.config.MongoProperties
import com.szastarek.gymz.service.plugins.configureAuthentication
import com.szastarek.gymz.service.plugins.configureMonitoring
import com.szastarek.gymz.service.plugins.configureSerialization
import com.szastarek.gymz.service.plugins.configureStatusPages
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

fun Application.module(authHttpClient: HttpClient = HttpClient(CIO), uploadsHttpClient: HttpClient = HttpClient(CIO)) {
    ConfigMap.init(HoconApplicationConfig(ConfigFactory.load()).mergeWith(environment.config))
    configureKoin(ConfigMap, environment.monitor, uploadsHttpClient)
    configureMongock(MongoClients.create(get<MongoProperties>().connectionString), get<MongoProperties>().database, get())
    configureMonitoring(get())
    configureSerialization(get())
    configureHealthz(get(), get())
    configureAuthentication(get(), get(), authHttpClient)
    configureStatusPages()
    swaggerRouting(get())
    configureOAuthRouting(get(), get())
    uploadsRouting()
    supportedEquipmentsRouting()
    userOwnedEquipmentsRouting()
    gymExercisesRouting()
}
