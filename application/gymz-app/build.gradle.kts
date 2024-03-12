import io.ktor.plugin.features.DockerImageRegistry

val appVersion = System.getenv("APP_VERSION")
val ghRegistryUser = System.getenv("GH_REGISTRY_USER")
val ghRegistryPassword = System.getenv("GH_REGISTRY_PASSWORD")

plugins {
    application
    alias(libs.plugins.ktor)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
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
}