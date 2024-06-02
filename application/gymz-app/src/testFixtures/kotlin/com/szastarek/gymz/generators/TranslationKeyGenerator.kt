package com.szastarek.gymz.generators

import com.szastarek.gymz.shared.i18n.TranslationKey
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

object TranslationKeyGenerator {
    val tag = Arb.Companion.string(3..15, Codepoint.az()).map { TranslationKey(it) }
}
