package com.szastarek.gymz.cerbos

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

private const val MONGO_PORT = 27017

object MongoContainer {
    private val instance by lazy { startMongoContainer() }

    val url: String
        get() =  "mongodb://${instance.host}:${instance.getMappedPort(MONGO_PORT)}"

    private fun startMongoContainer() =
        GenericContainer("eventstore/eventstore:22.10.3-bionic")
            .apply {
                addExposedPorts(MONGO_PORT)
                setWaitStrategy(Wait.forListeningPort())
                start()
            }
}