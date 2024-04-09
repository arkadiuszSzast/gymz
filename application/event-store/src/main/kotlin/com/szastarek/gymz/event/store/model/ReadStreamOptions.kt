package com.szastarek.gymz.event.store.model

data class ReadStreamOptions(
    val maxCount: Long = Long.MAX_VALUE,
    val direction: Direction = Direction.Forwards,
    val shouldResolveLinkTos: Boolean = true,
)

enum class Direction {
    Forwards,
    Backwards,
}
