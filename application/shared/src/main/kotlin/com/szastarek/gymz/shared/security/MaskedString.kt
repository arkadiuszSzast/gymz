package com.szastarek.gymz.shared.security

@JvmInline
value class MaskedString(val value: String) {
    override fun toString(): String {
        return "*masked*"
    }
}
