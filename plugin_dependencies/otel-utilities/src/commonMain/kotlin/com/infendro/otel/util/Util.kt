package com.infendro.otel.util

import com.infendro.otlp.OtlpExporter
import kotlinx.datetime.Instant

expect fun await(
    exporter: OtlpExporter
)

expect fun await(
    exporter: OtlpExporter,
    start: Instant
)

expect fun env(name: String): String?
