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
        val request = spans.toExportRequest(service)
        val job = scope.launch {
            try {
                stub.export(request = request)
            } catch (_: Exception) {
                // Match existing exporter semantics: fire-and-forget; OtlpExporter
                // returns ofSuccess eagerly regardless of RPC outcome.
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
