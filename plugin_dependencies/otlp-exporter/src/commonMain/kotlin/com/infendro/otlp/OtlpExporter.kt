package com.infendro.otlp

import io.ktor.client.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.*
import io.ktor.http.*
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import io.opentelemetry.kotlin.sdk.trace.export.SpanExporter
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class OtlpExporter(
    val host: String,
    val service: String
) : SpanExporter {
    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 600_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 600_000
        }
        expectSuccess = false
    }
    private val scope = CoroutineScope(Dispatchers.Default)
    private val jobs: MutableList<Job> = mutableListOf()

    private val sendGate = Semaphore(permits = 64)

    var totalSpansExported: Long = 0L
        private set
    var totalExportBatches: Long = 0L
        private set
    var failedExportBatches: Long = 0L
        private set
    var failedExportSpans: Long = 0L
        private set
    var firstExportError: String? = null
        private set

    suspend fun await() {
        jobs.joinAll()
    }

    override fun export(
        spans: Collection<SpanData>
    ): CompletableResultCode {
        totalSpansExported += spans.size.toLong()
        totalExportBatches += 1L
        val job = scope.launch {
            try {
                val payload = spans.serialize(service)
                sendGate.withPermit {
                    client.post("http://$host/v1/traces") {
                        contentType(ContentType.Application.Json)
                        setBody(payload)
                    }
                }
            } catch (e: Exception) {
                failedExportBatches += 1L
                failedExportSpans += spans.size.toLong()
                if (firstExportError == null) {
                    firstExportError = "${e::class.simpleName}: ${e.message}"
                }
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
