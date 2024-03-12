package com.szastarek.gymz.shared.mediator

enum class MediatrRequestType(val code: String) {
    Command("command"),
    CommandWithResult("command-with-result"),
    Query("query"),
    Notification("notification"),
    Unknown("unknown"),
}
