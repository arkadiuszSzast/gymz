plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kover)
    alias(libs.plugins.sonarqube)
}

group = "com.szastarek"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

sonar {
    properties {
        property("sonar.projectKey", "arkadiuszSzast_gymz")
        property("sonar.organization", "arkadiuszszast")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/kover/report.xml")
        property("sonar.coverage.exclusions", "**/Application.kt,**/plugin/**")
    }
}

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.kotlinx.kover")

    dependencies {
        kover(project(":application:shared"))
        kover(project(":application:gymz-app"))
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "org.gradle.java-test-fixtures")

    spotless {
        kotlin {
            ktlint().editorConfigOverride(
                mapOf(
                    "multiline-expression-wrapping" to "disabled",
                    "max-line-length" to "140",
                    "ktlint_standard_annotation" to "disabled"
                )
            )
        }
    }

    dependencies {
        implementation(rootProject.libs.ktor.server.core.jvm)
        implementation(rootProject.libs.ktor.server.auth.jvm)
        implementation(rootProject.libs.ktor.server.auth.jwt.jvm)
        implementation(rootProject.libs.ktor.server.host.common.jvm)
        implementation(rootProject.libs.ktor.server.status.pages.jvm)
        implementation(rootProject.libs.ktor.server.content.negotiation.jvm)
        implementation(rootProject.libs.ktor.serialization.kotlinx.json.jvm)
        implementation(rootProject.libs.ktor.server.netty.jvm)
        implementation(rootProject.libs.ktor.client.core)
        implementation(rootProject.libs.ktor.client.cio)
        implementation(rootProject.libs.ktor.client.content.negotiation)
        implementation(rootProject.libs.koin.ktor)
        implementation(rootProject.libs.arrow.core)
        implementation(rootProject.libs.logback.classic)
        implementation(rootProject.libs.kotlin.logging.jvm)
        implementation(rootProject.libs.kediatr.koin)
        implementation(rootProject.libs.kotlinx.datetime)
        implementation(rootProject.libs.codified.enums)
        implementation(rootProject.libs.codified.enums.serializer)
        implementation(rootProject.libs.codified.enums.serializer)
        implementation(rootProject.libs.grpc.all)
        implementation(rootProject.libs.cerbos)

        implementation(rootProject.libs.opentelemetry.logback)
        implementation(rootProject.libs.opentelemetry.ktor)
        implementation(rootProject.libs.opentelemetry.kotlin)
        implementation(rootProject.libs.ktor.server.metrics.micrometer)
        implementation(rootProject.libs.micrometer.registry.otlp)

        testImplementation(rootProject.libs.ktor.server.tests.jvm)
        testImplementation(rootProject.libs.ktor.mock.engine)
        testImplementation(rootProject.libs.kotest.runner.junit5)
        testImplementation(rootProject.libs.kotest.assertions.core)
        testImplementation(rootProject.libs.kotest.assertions.arrow)
        testImplementation(rootProject.libs.kotest.property.jvm)
        testImplementation(rootProject.libs.kotest.extra.arb)
        testImplementation(rootProject.libs.testcontainers)
        testImplementation(rootProject.libs.kotest.extensions.koin)
        testImplementation(rootProject.libs.koin.test)
        testImplementation(rootProject.libs.konsist)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        jvmArgs("--add-opens=java.base/java.util=ALL-UNNAMED")
    }

    kotlin.target.compilations["testFixtures"].associateWith(kotlin.target.compilations["main"])
}


