package com.infendro.otlp

import io.github.timortel.kmpgrpc.core.Channel
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import io.opentelemetry.kotlin.sdk.trace.export.SpanExporter
import io.opentelemetry.proto.collector.trace.v1.TraceServiceStub
import kotlinx.coroutines.*

class OtlpExporter(
    val host: String,
    val service: String
) : SpanExporter {
    private val channel: Channel
    private val stub: TraceServiceStub
    private val scope = CoroutineScope(Dispatchers.Default)
    private val jobs: MutableList<Job> = mutableListOf()

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

    init {
        val (h, p) = parseHostPort(host, defaultPort = 4317)
        channel = Channel.Builder.forAddress(h, p).usePlaintext().build()
        stub = TraceServiceStub(channel)
    }

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
                val request = spans.toExportRequest(service)
                stub.export(request = request)
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

private fun parseHostPort(host: String, defaultPort: Int): Pair<String, Int> {
    val idx = host.lastIndexOf(':')
    if (idx < 0) return host to defaultPort
    val h = host.substring(0, idx)
    val p = host.substring(idx + 1).toIntOrNull() ?: defaultPort
    return h to p
}
