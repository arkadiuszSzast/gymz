plugins {
    application
    alias(libs.plugins.ktor)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}