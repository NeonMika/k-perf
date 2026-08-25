package com.infendro.otel.util

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

private fun printExportStats(exporter: UtilityExporter) {
    println("### exported_spans: ${exporter.totalSpansExported}")
    println("### export_batches: ${exporter.totalExportBatches}")
    println("### export_failures: ${exporter.failedExportBatches}")
    println("### export_failed_spans: ${exporter.failedExportSpans}")
    exporter.firstExportError?.let { println("### first_export_error: $it") }
}

actual fun await(
    exporter: UtilityExporter
) = runBlocking {
    exporter.await()
    printExportStats(exporter)
}

actual fun await(
    exporter: UtilityExporter,
    start: Instant
) = runBlocking {
    exporter.await()

    val end = Clock.System.now()
    val ms = (end - start).inWholeMilliseconds
    println("Flush finished - $ms ms elapsed")
    printExportStats(exporter)
}

actual fun env(name: String): String? {
    return System.getenv(name)
}
