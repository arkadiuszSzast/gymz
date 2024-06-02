package com.szastarek.gymz.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.map
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant

object InstantGenerator {

    fun instant(
        min: Instant = Instant.parse("2021-01-01T00:00:00Z"),
        max: Instant = Instant.parse("2023-01-01T00:00:00Z"),
    ) = Arb.instant(min.toJavaInstant(), max.toJavaInstant()).map { it.toKotlinInstant() }
}
