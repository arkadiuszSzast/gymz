package com.szastarek.gymz.shared.mediator

import com.trendyol.kediatr.Command
import com.trendyol.kediatr.CommandWithResult
import com.trendyol.kediatr.Notification
import com.trendyol.kediatr.Query

object MediatrRequestTypeExtractor {
    fun <TRequest> extract(request: TRequest) =
        when (request) {
            is Command -> MediatrRequestType.Command
            is CommandWithResult<*> -> MediatrRequestType.CommandWithResult
            is Query<*> -> MediatrRequestType.Query
            is Notification -> MediatrRequestType.Notification
            else -> MediatrRequestType.Unknown
        }
}
