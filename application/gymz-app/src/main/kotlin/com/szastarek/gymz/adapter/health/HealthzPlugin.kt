package com.szastarek.gymz.adapter.health

import com.eventstore.dbclient.EventStoreDBClient
import com.szastarek.gymz.shared.plugin.HealthzPlugin
import dev.cerbos.sdk.CerbosBlockingClient
import dev.cerbos.sdk.builders.Principal
import dev.cerbos.sdk.builders.Resource
import io.ktor.server.application.Application
import io.ktor.server.application.install
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking

internal fun Application.configureHealthz(
    eventStoreDBClient: EventStoreDBClient,
    cerbosClient: CerbosBlockingClient,
) {
    install(HealthzPlugin) {
        healthChecks {
            check("app") { true }
            check("eventstore") { eventStoreDBClient.check() }
            check("cerbos") { cerbosClient.check() }
        }
        readyChecks {
            check("app") { true }
            check("eventstore") { eventStoreDBClient.check() }
            check("cerbos") { cerbosClient.check() }
        }
    }
}

private suspend fun EventStoreDBClient.check(): Boolean =
    runCatching { serverVersion.await().isPresent }.getOrElse { false }

private fun CerbosBlockingClient.check(): Boolean =
    runBlocking(Dispatchers.IO) {
        val principal = Principal.newInstance("system")
        val resource = Resource.newInstance("health-check")
        runCatching { check(principal, resource, "get") }.isSuccess
    }
