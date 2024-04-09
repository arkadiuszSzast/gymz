package com.szastarek.gymz.event.store.adapter

import com.eventstore.dbclient.NackAction
import com.szastarek.gymz.event.store.model.NackAction as NackActionDomain

fun NackActionDomain.toEventStore(): NackAction = when (this) {
    NackActionDomain.Park -> NackAction.Park
    NackActionDomain.Retry -> NackAction.Retry
    NackActionDomain.Skip -> NackAction.Skip
    NackActionDomain.Stop -> NackAction.Stop
    NackActionDomain.Unknown -> NackAction.Unknown
}
