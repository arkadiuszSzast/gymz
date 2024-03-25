package com.szastarek.gymz.shared.mediator

import com.szastarek.gymz.utils.InMemoryOpenTelemetry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.trace.StatusCode

class TracingPipelineBehaviorTest : StringSpec({

    val openTelemetry = InMemoryOpenTelemetry()

    val tracingPipelineBehaviour = TracingPipelineBehavior(openTelemetry.get())

    beforeTest {
        openTelemetry.reset()
    }

    "should execute Command within new span" {
        // arrange && act
        tracingPipelineBehaviour.handle(SimpleCommand()) {
            // simple command handler
        }

        // assert
        openTelemetry.getFinishedSpans() shouldHaveSize 1

        val span = openTelemetry.getFinishedSpans().single()
        span.name shouldBe "SimpleCommand"
        span.attributes.asMap() shouldContain Pair(AttributeKey.stringKey("command"), "SimpleCommand")
        span.attributes.asMap().shouldContainKey(AttributeKey.stringKey("requestId"))
    }

    "should execute CommandWithResult within new span" {
        // arrange && act
        tracingPipelineBehaviour.handle(SimpleCommandWithResult()) {
            // simple command with result handler
        }

        // assert
        openTelemetry.getFinishedSpans() shouldHaveSize 1

        val span = openTelemetry.getFinishedSpans().single()
        span.name shouldBe "SimpleCommandWithResult"
        span.attributes.asMap() shouldContain Pair(AttributeKey.stringKey("command-with-result"), "SimpleCommandWithResult")
        span.attributes.asMap() shouldContainKey AttributeKey.stringKey("requestId")
    }

    "should execute Notification within new span" {
        // arrange && act
        tracingPipelineBehaviour.handle(SimpleNotification()) {
            // simple notification handler
        }

        // assert
        openTelemetry.getFinishedSpans() shouldHaveSize 1

        val span = openTelemetry.getFinishedSpans().single()
        span.name shouldBe "SimpleNotification"
        span.attributes.asMap() shouldContain Pair(AttributeKey.stringKey("notification"), "SimpleNotification")
        span.attributes.asMap() shouldContainKey AttributeKey.stringKey("requestId")
    }

    "should execute Query within new span" {
        // arrange && act
        tracingPipelineBehaviour.handle(SimpleQuery()) {
            // simple query handler
        }

        // assert
        openTelemetry.getFinishedSpans() shouldHaveSize 1

        val span = openTelemetry.getFinishedSpans().single()
        span.name shouldBe "SimpleQuery"
        span.attributes.asMap() shouldContain Pair(AttributeKey.stringKey("query"), "SimpleQuery")
        span.attributes.asMap() shouldContainKey AttributeKey.stringKey("requestId")
    }

    "should rethrow exception and mark span as error" {
        // arrange && act && assert
        shouldThrow<IllegalArgumentException> {
            tracingPipelineBehaviour.handle(SimpleCommand()) {
                throw IllegalArgumentException("test exception")
            }
        }

        openTelemetry.getFinishedSpans() shouldHaveSize 1

        val span = openTelemetry.getFinishedSpans().single()
        span.name shouldBe "SimpleCommand"
        span.attributes.asMap() shouldContain Pair(AttributeKey.stringKey("command"), "SimpleCommand")
        span.attributes.asMap() shouldContainKey AttributeKey.stringKey("requestId")
        span.status.statusCode shouldBe StatusCode.ERROR
    }
})
