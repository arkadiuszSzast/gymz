package com.szastarek.gymz

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import io.kotest.core.spec.style.StringSpec

class ArchitectureTest : StringSpec({

    "Architecture test" {
        Konsist
            .scopeFromProject()
            .assertArchitecture {
                val domain = Layer("Domain", "com.szastarek.gymz.domain.model..")
                val domainService = Layer("Domain Service", "com.szastarek.gymz.domain.service..")
                val applicationService = Layer("Application Service", "com.szastarek.gymz.service..")
                val infrastructureService = Layer("Infrastructure Service", "com.szastarek.gymz.adapter..")

                domain.dependsOnNothing()
                domainService.dependsOn(domain)
                applicationService.dependsOn(domainService)
                infrastructureService.dependsOn(domain, domainService, applicationService)
            }
    }
})
