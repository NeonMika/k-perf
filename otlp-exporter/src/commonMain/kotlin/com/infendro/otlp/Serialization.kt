package com.infendro.otlp

import com.infendro.otlp.Value.*
import io.opentelemetry.kotlin.api.common.AttributeType
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal fun Iterable<SpanData>.serialize(
    service: String
): String {
    val resourceSpans = map { span ->
        ResourceSpans(
            resource = Resource(
                attributes = listOf(
                    Attribute(
                        key = "service.name",
                        value = StringValue(service)
                    )
                )
            ),
            scopeSpans = listOf(
                ScopeSpans(
                    scope = Scope(
                        name = "otlp",
                        version = "1.0.0",
                        attributes = listOf()
                    ),
                    spans = listOf(
                        Span(
                            traceId = span.traceId.toString(),
                            spanId = span.spanId.toString(),
                            parentSpanId = span.parentSpanId.toString(),
                            name = span.name,
                            startTimeUnixNano = "${span.startEpochNanos}",
                            endTimeUnixNano = "${span.endEpochNanos}",
                            kind = span.kind.ordinal,
                            attributes = span.attributes.asMap().map { (key, value) ->
                                when (key.type) {
                                    AttributeType.STRING -> Attribute(
                                        key = key.key,
                                        value = StringValue(value as String)
                                    )

                                    AttributeType.LONG -> Attribute(
                                        key = key.key,
                                        value = IntValue((value as Long).toString())
                                    )

                                    AttributeType.DOUBLE -> Attribute(
                                        key = key.key,
                                        value = DoubleValue(value as Double)
                                    )

                                    AttributeType.BOOLEAN -> Attribute(
                                        key = key.key,
                                        value = BoolValue(value as Boolean)
                                    )

                                    else -> throw Exception()
                                }
                            }
                        )
                    )
                )
            )
        )
    }

    val payload = Payload(resourceSpans = resourceSpans)
    return Json.encodeToString(payload)
}

@Serializable
internal data class Payload(
    val resourceSpans: List<ResourceSpans>
)

@Serializable
internal data class ResourceSpans(
    val resource: Resource,
    val scopeSpans: List<ScopeSpans>
)

@Serializable
internal data class Resource(
    val attributes: List<Attribute>
)

@Serializable
internal data class ScopeSpans(
    val scope: Scope,
    val spans: List<Span>
)

@Serializable
internal data class Scope(
    val name: String,
    val version: String,
    val attributes: List<Attribute>
)

@Serializable
internal data class Span(
    val traceId: String,
    val spanId: String,
    val parentSpanId: String,
    val name: String,
    val startTimeUnixNano: String,
    val endTimeUnixNano: String,
    val kind: Int,
    val attributes: List<Attribute>
)

@Serializable
internal data class Attribute(
    val key: String,
    val value: Value
)

@Serializable
internal sealed class Value {
    @Serializable
    data class StringValue(val stringValue: String) : Value()

    @Serializable
    data class IntValue(val intValue: String) : Value()

    @Serializable
    data class DoubleValue(val doubleValue: Double) : Value()

    @Serializable
    data class BoolValue(val boolValue: Boolean) : Value()
}
