package com.infendro.otel.utilities

import com.infendro.otlp.json.OtlpJsonExporter
import kotlinx.datetime.Instant

expect fun await(
    exporter: OtlpJsonExporter
)

expect fun await(
    exporter: OtlpJsonExporter,
    start: Instant
)

expect fun env(name: String): String?
