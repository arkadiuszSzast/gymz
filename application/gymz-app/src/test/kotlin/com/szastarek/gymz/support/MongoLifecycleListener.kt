package com.szastarek.gymz.support

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.szastarek.gymz.cerbos.MongoContainer
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import kotlinx.coroutines.flow.toList

class MongoLifecycleListener(private val container: MongoContainer) : TestListener {

    private val client = MongoClient.create(container.url)

    override suspend fun beforeEach(testCase: TestCase) {
        val db = client.getDatabase(container.dbName)
        db.listCollectionNames().toList().map { db.getCollection<Any>(it).drop() }
        super.beforeEach(testCase)
    }
}
