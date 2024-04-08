package com.szastarek.gymz.event.store.model

enum class NackAction {
    Unknown,
    Park,
    Retry,
    Skip,
    Stop,
}
