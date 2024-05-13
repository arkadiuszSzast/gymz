package com.szastarek.gymz.cerbos

import dev.cerbos.sdk.CerbosContainer
import org.testcontainers.containers.BindMode
import org.testcontainers.images.RemoteDockerImage
import org.testcontainers.utility.DockerImageName

object CerbosContainer {
    private val instance by lazy { startCerbosContainer() }

    val url: String
        get() = instance.target

    private fun startCerbosContainer() =
        CerbosContainer()
            .withClasspathResourceMapping("policies", "/policies", BindMode.READ_ONLY)
            .apply {
                image = RemoteDockerImage(DockerImageName.parse("docker.io/cerbos/cerbos:0.34.0"))
                start()
            }
}
