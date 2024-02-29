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
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory}/reports/kover/report.xml")
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
        kover(project(":application:gymz"))
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.diffplug.spotless")

    spotless {
        kotlin {
            ktlint()
        }
    }

    dependencies {
        implementation(rootProject.libs.ktor.server.core.jvm)
        implementation(rootProject.libs.ktor.server.auth.jvm)
        implementation(rootProject.libs.ktor.server.host.common.jvm)
        implementation(rootProject.libs.ktor.server.status.pages.jvm)
        implementation(rootProject.libs.ktor.server.content.negotiation.jvm)
        implementation(rootProject.libs.ktor.serialization.kotlinx.json.jvm)
        implementation(rootProject.libs.ktor.server.netty.jvm)

        implementation(rootProject.libs.logback.classic)
        implementation(rootProject.libs.kotlin.logging.jvm)
        implementation(rootProject.libs.kediatr.koin)

        implementation(rootProject.libs.arrow.core)

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


