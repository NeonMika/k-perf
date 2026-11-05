package com.infendro.otel.proto.utilities

import com.infendro.otlp.proto.OtlpProtoExporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import node.process.process

private fun printExportStats(exporter: OtlpProtoExporter) {
    println("### exported_spans: ${exporter.totalSpansExported}")
    println("### export_batches: ${exporter.totalExportBatches}")
    println("### export_failures: ${exporter.failedExportBatches}")
    println("### export_failed_spans: ${exporter.failedExportSpans}")
    exporter.firstExportError?.let { println("### first_export_error: $it") }
}

actual fun await(
    exporter: OtlpProtoExporter
) {
    CoroutineScope(Dispatchers.Default).launch {
        exporter.await()
        printExportStats(exporter)
        process.exit()
    }
}

actual fun await(
    exporter: OtlpProtoExporter,
    start: Instant
) {
    CoroutineScope(Dispatchers.Default).launch {
        exporter.await()

        val end = Clock.System.now()
        val ms = (end - start).inWholeMilliseconds
        println("Flush finished - $ms ms elapsed")
        printExportStats(exporter)

        process.exit()
    }
}

actual fun env(name: String): String? {
    return process.env[name]
}
