package at.jku.ssw.kperfotel.proto.utilities

import at.jku.ssw.kperfotel.otlp.proto.OtlpProtoExporter
import kotlinx.datetime.Instant

expect fun await(
    exporter: OtlpProtoExporter
)

expect fun await(
    exporter: OtlpProtoExporter,
    start: Instant
)

expect fun env(name: String): String?
