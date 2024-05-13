package com.szastarek.gymz.file.storage

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

private const val LOCALSTACK_PORT = 4566
private const val LOCALSTACK_DEFAULT_REGION = "us-east-1"

object LocalstackContainer {
    private val localStackImage = DockerImageName.parse("localstack/localstack:3.3")

    private val instance by lazy { startLocalstackContainer() }
    private val host by lazy { instance.host }
    val s3Endpoint by lazy { "http://$host:${instance.getMappedPort(LOCALSTACK_PORT)}" }
    val accessKey by lazy { "accessKey" }
    val secretKey by lazy { "secretKey" }
    val region by lazy { LOCALSTACK_DEFAULT_REGION }

    private fun startLocalstackContainer() = GenericContainer(localStackImage)
        .apply {
            withExposedPorts(LOCALSTACK_PORT)
            withEnv("SERVICES", "s3")
            withEnv("DEFAULT_REGION", LOCALSTACK_DEFAULT_REGION)
            setWaitStrategy(Wait.forListeningPort())
            start()
        }
}
