package com.szastarek.gymz.event.store.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class EventMetadataTest : StringSpec({

    "should generate metadata based on given causing metadata" {
        // arrange
        val causingMetadata =
            EventMetadataBuilder(
                AggregateId(UUID.randomUUID().toString()),
                EventCategory("account"),
                EventType("account-created"),
            ).build()

        // act
        val result =
            EventMetadataBuilder(
                AggregateId(UUID.randomUUID().toString()),
                EventCategory("email"),
                EventType("email-sent"),
            ).causedBy(causingMetadata).build()

        // assert
        result.correlationId shouldBe causingMetadata.correlationId
        result.causationId shouldBe CausationId(causingMetadata.eventId.value)
    }
})
