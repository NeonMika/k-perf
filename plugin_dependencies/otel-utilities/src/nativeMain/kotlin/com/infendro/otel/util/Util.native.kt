package com.infendro.otel.util

import com.infendro.otlp.OtlpExporter
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import platform.posix.getenv

private fun printExportStats(exporter: OtlpExporter) {
    println("### exported_spans: ${exporter.totalSpansExported}")
    println("### export_batches: ${exporter.totalExportBatches}")
    println("### export_failures: ${exporter.failedExportBatches}")
    println("### export_failed_spans: ${exporter.failedExportSpans}")
    exporter.firstExportError?.let { println("### first_export_error: $it") }
}

actual fun await(
    exporter: OtlpExporter
) = runBlocking {
    exporter.await()
    printExportStats(exporter)
}

actual fun await(
    exporter: OtlpExporter,
    start: Instant
) = runBlocking {
    exporter.await()

    val end = Clock.System.now()
    val ms = (end - start).inWholeMilliseconds
    println("Flush finished - $ms ms elapsed")
    printExportStats(exporter)
}

@OptIn(ExperimentalForeignApi::class)
actual fun env(name: String): String? {
    return getenv(name)?.toKString()
}
