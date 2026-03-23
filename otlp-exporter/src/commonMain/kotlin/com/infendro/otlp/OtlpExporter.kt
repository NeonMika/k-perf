package com.infendro.otlp

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import io.opentelemetry.kotlin.sdk.trace.export.SpanExporter
import kotlinx.coroutines.*

class OtlpExporter(
    val host: String,
    val service: String
) : SpanExporter {
    private val client = HttpClient()
    private val scope = CoroutineScope(Dispatchers.Default)
    private val jobs: MutableList<Job> = mutableListOf()

    suspend fun await() {
        jobs.joinAll()
    }

    override fun export(
        spans: Collection<SpanData>
    ): CompletableResultCode {
        val job = scope.launch {
            val payload = spans.serialize(service)
            client.post("http://$host/v1/traces") {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
        jobs.add(job)

        return CompletableResultCode.ofSuccess()
    }

    override fun flush(): CompletableResultCode {
        return CompletableResultCode.ofSuccess()
    }

    override fun shutdown() = flush()
}
