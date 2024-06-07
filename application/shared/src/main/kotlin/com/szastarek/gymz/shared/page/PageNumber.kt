package com.szastarek.gymz.shared.page

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class PageNumber(val value: Int) {
    companion object {
        val first = PageNumber(1)
    }
}
