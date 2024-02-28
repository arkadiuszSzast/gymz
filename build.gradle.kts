plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.szastarek"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "kotlin")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    dependencies {
        implementation(rootProject.libs.ktor.server.core.jvm)
        implementation(rootProject.libs.ktor.server.auth.jvm)
        implementation(rootProject.libs.ktor.server.host.common.jvm)
        implementation(rootProject.libs.ktor.server.status.pages.jvm)
        implementation(rootProject.libs.ktor.server.content.negotiation.jvm)
        implementation(rootProject.libs.ktor.serialization.kotlinx.json.jvm)
        implementation(rootProject.libs.ktor.server.cio.jvm)

        implementation(rootProject.libs.logback.classic)

//        implementation(rootProject.libs.arrow.core)

        testImplementation(rootProject.libs.ktor.server.tests.jvm)
        testImplementation(rootProject.libs.kotest.runner.junit5)
        testImplementation(rootProject.libs.kotest.assertions.core)
        testImplementation(rootProject.libs.kotest.property.jvm)
        testImplementation(rootProject.libs.testcontainers)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        jvmArgs("--add-opens=java.base/java.util=ALL-UNNAMED")
    }
}


