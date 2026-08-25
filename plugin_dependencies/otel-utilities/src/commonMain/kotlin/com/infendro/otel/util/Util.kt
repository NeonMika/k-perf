package com.infendro.otel.util

import kotlinx.datetime.Instant

expect fun await(
    exporter: UtilityExporter
)

expect fun await(
    exporter: UtilityExporter,
    start: Instant
)

expect fun env(name: String): String?
