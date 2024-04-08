package com.szastarek.gymz.event.store

import kotlinx.coroutines.delay
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

private const val EVENT_STORE_DB_PORT = 2113

class EventStoreContainer {
    private val instance by lazy { startEventStoreContainer() }
    private val host: String by lazy { instance.host }
    private val port: Int by lazy { instance.getMappedPort(EVENT_STORE_DB_PORT) }

    val url: String by lazy { "esdb://$host:$port?tls=false&discoveryInterval=100&maxDiscoverAttempts=10" }

    suspend fun restart() {
        instance.portBindings = listOf("${instance.getMappedPort(EVENT_STORE_DB_PORT)}:$EVENT_STORE_DB_PORT")
        instance.stop()
        instance.start()
        delay(50)
    }

    fun stop() {
        instance.stop()
    }

    private fun startEventStoreContainer() =
        GenericContainer("eventstore/eventstore:22.10.3-bionic")
            .apply {
                addExposedPorts(EVENT_STORE_DB_PORT)
                addEnv("EVENTSTORE_CLUSTER_SIZE", "1")
                addEnv("EVENTSTORE_RUN_PROJECTIONS", "All")
                addEnv("EVENTSTORE_START_STANDARD_PROJECTIONS", "true")
                addEnv("EVENTSTORE_EXT_TCP_PORT", "1113")
                addEnv("EVENTSTORE_HTTP_PORT", EVENT_STORE_DB_PORT.toString())
                addEnv("EVENTSTORE_INSECURE", "true")
                addEnv("EVENTSTORE_ENABLE_EXTERNAL_TCP", "true")
                addEnv("EVENTSTORE_ENABLE_ATOM_PUB_OVER_HTTP", "true")
                addEnv("EVENTSTORE_MEM_DB", "true")
                setWaitStrategy(Wait.forListeningPort())
                start()
            }
}