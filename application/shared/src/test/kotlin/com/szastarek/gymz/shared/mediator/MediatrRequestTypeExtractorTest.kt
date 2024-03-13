package com.szastarek.gymz.shared.mediator

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MediatrRequestTypeExtractorTest : StringSpec({

    "should extract command type" {
        // arrange && act
        val result = MediatrRequestTypeExtractor.extract(SimpleCommand())

        // assert
        result shouldBe MediatrRequestType.Command
    }

    "should extract commandWithResult type" {
        // arrange && act
        val result = MediatrRequestTypeExtractor.extract(SimpleCommandWithResult())

        // assert
        result shouldBe MediatrRequestType.CommandWithResult
    }

    "should extract query type" {
        // arrange && act
        val result = MediatrRequestTypeExtractor.extract(SimpleQuery())

        // assert
        result shouldBe MediatrRequestType.Query
    }

    "should extract notification type" {
        // arrange && act
        val result = MediatrRequestTypeExtractor.extract(SimpleNotification())

        // assert
        result shouldBe MediatrRequestType.Notification
    }

    "should return unknown when not found" {
        // arrange && act
        val result = MediatrRequestTypeExtractor.extract(UnknownRequest())

        // assert
        result shouldBe MediatrRequestType.Unknown
    }
})
