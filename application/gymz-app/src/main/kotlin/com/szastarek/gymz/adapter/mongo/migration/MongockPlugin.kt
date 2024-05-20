package com.szastarek.gymz.adapter.mongo.migration

import com.mongodb.reactivestreams.client.MongoClient
import com.szastarek.gymz.file.storage.FileStorage
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.install
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver
import io.mongock.runner.standalone.MongockStandalone
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

fun Application.configureMongock(mongoClient: MongoClient, database: String, fileStorage: FileStorage) {
    install(MongockPlugin) {
        this.mongoClient = mongoClient
        this.database = database
        this.fileStorage = fileStorage
    }
}

class MongockPluginConfiguration {
    var enabled = true
    var mongoClient: MongoClient? = null
    var database: String? = null
    var fileStorage: FileStorage? = null
}

val MongockPlugin = createApplicationPlugin(name = "Mongock", createConfiguration = ::MongockPluginConfiguration) {
    val config = this.pluginConfig
    on(MonitoringEvent(ApplicationStarted)) { _ ->
        val mongock = MongockStandalone.builder()
            .setDriver(
                MongoReactiveDriver.withDefaultLock(
                    config.mongoClient,
                    config.database,
                ),
            )
            .addMigrationScanPackage("com.szastarek.gymz.adapter.mongo.migration")
            .addDependency(config.mongoClient)
            .addDependency(FileStorage::class.java, config.fileStorage)
            .setMigrationStartedListener { logger.info { "Executing migration" } }
            .setMigrationSuccessListener { logger.info { "Finished executing migration" } }
            .setMigrationFailureListener {
                logger.error { "Failed to execute migration because of: ${it.exception.message}. Application terminated" }
                exitProcess(1)
            }
            .setTransactionEnabled(true)
            .setEnabled(config.enabled)
            .buildRunner()

        runBlocking { mongock.execute() }
    }
}
