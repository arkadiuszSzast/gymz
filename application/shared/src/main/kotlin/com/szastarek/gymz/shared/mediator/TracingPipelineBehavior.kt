package com.szastarek.gymz.shared.mediator

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.szastarek.gymz.shared.monitoring.execute
import com.trendyol.kediatr.PipelineBehavior
import io.github.oshai.kotlinlogging.KotlinLogging
import io.opentelemetry.api.OpenTelemetry
import java.util.UUID

class TracingPipelineBehavior(private val openTelemetry: OpenTelemetry) : PipelineBehavior {
    private val logger = KotlinLogging.logger {}

    override suspend fun <TRequest, TResponse> handle(
        request: TRequest,
        next: suspend (TRequest) -> TResponse,
    ): TResponse {
        val tracer = openTelemetry.getTracer("mediator")
        val requestType = MediatrRequestTypeExtractor.extract(request).code
        val requestSimpleName = request?.let { it::class.simpleName } ?: "not-known-request"
        val requestId = UUID.randomUUID().toString()

        return tracer.spanBuilder(requestSimpleName)
            .setAttribute(requestType, requestSimpleName)
            .setAttribute("requestId", requestId.toString())
            .startSpan()
            .execute {
                try {
                    logger.debug { "Executing $requestType $requestSimpleName [$requestId]. Payload: [$request]" }
                    val result = next(request)
                    if (result is Either<*, *>) {
                        when (result) {
                            is Left<*> -> logger.error { "Error while executing $requestType $requestSimpleName [$requestId]. Result: $result" }
                            is Right<*> -> logger.debug { "$requestType: $requestSimpleName [$requestId] executed successfully. Result: $result" }
                        }
                    } else {
                        logger.debug { "$requestType: $requestSimpleName [$requestId] executed successfully. Result: $result" }
                    }
                    result
                } catch (e: Throwable) {
                    logger.error(e) { "Error while executing $requestType $requestSimpleName [$requestId]." }
                    throw e
                }
            }
    }
}
