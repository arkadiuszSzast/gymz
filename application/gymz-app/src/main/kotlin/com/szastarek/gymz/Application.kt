package com.szastarek.gymz

import com.szastarek.gymz.plugins.configureRouting
import com.szastarek.gymz.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
