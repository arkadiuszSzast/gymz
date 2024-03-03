package com.szastarek.gymz

import com.szastarek.gymz.plugins.configureRouting
import com.szastarek.gymz.plugins.configureSerialization
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
