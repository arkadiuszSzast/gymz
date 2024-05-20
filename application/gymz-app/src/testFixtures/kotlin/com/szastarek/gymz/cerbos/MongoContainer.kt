package com.szastarek.gymz.cerbos

import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbs.cars
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

private const val MONGO_PORT = 27017

object MongoContainer {
    private val instance by lazy { startMongoContainer() }
    val dbName by lazy { Arb.cars().next().value }

    val url: String
        get() = instance.replicaSetUrl

    private fun startMongoContainer(): MongoDBContainer =
        MongoDBContainer(DockerImageName.parse("mongo:7.0.9"))
            .apply {
                addExposedPorts(MONGO_PORT)
                withCommand("--replSet rs0")
                withEnv("MONGO_INITDB_DATABASE", dbName)
                setWaitStrategy(Wait.forListeningPorts())
                start()
            }
}
