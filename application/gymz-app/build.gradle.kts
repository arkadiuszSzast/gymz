import io.ktor.plugin.features.DockerImageRegistry

val appVersion = System.getenv("APP_VERSION")
val ghRegistryUser = System.getenv("GH_REGISTRY_USER")
val ghRegistryPassword = System.getenv("GH_REGISTRY_PASSWORD")

plugins {
    application
    alias(libs.plugins.ktor)
}

application {
    mainClass.set("com.szastarek.gymz.ApplicationKt")
}

ktor {
    docker {
        localImageName.set("gymz-app")
        imageTag.set(appVersion)
        jreVersion = JavaVersion.VERSION_21
        externalRegistry.set(
            DockerImageRegistry.externalRegistry(
                username = provider { ghRegistryUser },
                password = provider { ghRegistryPassword },
                project = provider { "gymz" },
                hostname = provider { "ghcr.io" },
                namespace = provider { "arkadiuszszast" },
            )
        )
        jib {
            extraDirectories {
                paths {
                    path {
                        setFrom("../../.open-telemetry")
                        into = "/app/libs/opentelemetry/"
                    }
                }
            }
            container {
                creationTime = "USE_CURRENT_TIMESTAMP"
                jvmFlags = listOf("-javaagent:/app/libs/opentelemetry/opentelemetry-javaagent.jar")
            }
        }
    }
}

dependencies {
    implementation(project(":application:shared"))
    implementation(project(":application:event-store"))
    implementation(project(":application:file-storage"))

    implementation(libs.mongo)
    implementation(libs.mongo.kotlinx)
    implementation(libs.mongock.reactive.driver)
    implementation(libs.mongock)

    testImplementation(testFixtures(project(":application:test-utils")))
    testImplementation(testFixtures(project(":application:event-store")))
    testImplementation(testFixtures(project(":application:file-storage")))
    testImplementation(testFixtures(project(":application:shared")))

    testFixturesImplementation(libs.kotest.property.jvm)
    testFixturesImplementation(libs.kotest.extra.arb)
    testFixturesImplementation(libs.testcontainers)
    testFixturesImplementation(libs.testcontainers.mongo)
}