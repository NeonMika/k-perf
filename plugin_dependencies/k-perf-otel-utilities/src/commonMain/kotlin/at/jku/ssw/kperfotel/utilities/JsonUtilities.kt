package at.jku.ssw.kperfotel.utilities

import at.jku.ssw.kperfotel.otlp.json.OtlpJsonExporter
import kotlinx.datetime.Instant

expect fun await(
    exporter: OtlpJsonExporter
)

expect fun await(
    exporter: OtlpJsonExporter,
    start: Instant
)

expect fun env(name: String): String?
