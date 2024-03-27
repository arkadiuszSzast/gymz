package com.szastarek.gymz.shared.security

class UnauthorizedException(override val message: String) : RuntimeException(message)
