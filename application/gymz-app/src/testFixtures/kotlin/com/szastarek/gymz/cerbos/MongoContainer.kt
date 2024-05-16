package com.szastarek.gymz.cerbos

import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

private const val MONGO_PORT = 27017

object MongoContainer {
    private val instance by lazy { startMongoContainer() }

    val url: String
        get() = instance.replicaSetUrl

    private fun startMongoContainer(): MongoDBContainer =
        MongoDBContainer(DockerImageName.parse("mongo:7.0.10-rc0-jammy"))
            .apply {
                addExposedPorts(MONGO_PORT)
                withCommand("--replSet rs0")
                setWaitStrategy(Wait.forListeningPorts())
//                setWaitStrategy(Wait.forLogMessage(".*waiting for connections on port.*", 1))
                start()
            }
}
