package com.infendro.otel.proto.utilities

import com.infendro.otlp.proto.OtlpProtoExporter
import kotlinx.datetime.Instant

expect fun await(
    exporter: OtlpProtoExporter
)

expect fun await(
    exporter: OtlpProtoExporter,
    start: Instant
)

expect fun env(name: String): String?
