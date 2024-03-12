package com.szastarek.gymz

import com.szastarek.gymz.adapter.rest.configureRouting
import com.szastarek.gymz.plugins.configureAuthentication
import com.szastarek.gymz.plugins.configureKoin
import com.szastarek.gymz.plugins.configureMonitoring
import com.szastarek.gymz.plugins.configureSerialization
import com.szastarek.gymz.plugins.configureStatusPages
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.koin.ktor.ext.get

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureKoin()
    configureMonitoring(get(), get())
    configureSerialization(get())
    configureAuthentication(get(), get())
    configureStatusPages()
    configureRouting(get(), get())
}
