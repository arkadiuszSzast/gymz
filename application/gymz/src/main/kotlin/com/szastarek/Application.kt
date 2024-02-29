package com.szastarek

import com.szastarek.plugins.configureRouting
import com.szastarek.plugins.configureSecurity
import com.szastarek.plugins.configureSerialization
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureRouting()
}
