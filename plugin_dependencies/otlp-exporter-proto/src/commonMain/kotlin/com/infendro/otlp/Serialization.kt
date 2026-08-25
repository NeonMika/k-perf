package com.infendro.otlp

import io.opentelemetry.kotlin.api.common.AttributeType
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest
import io.opentelemetry.proto.collector.trace.v1.exportTraceServiceRequest
import io.opentelemetry.proto.common.v1.AnyValue
import io.opentelemetry.proto.common.v1.anyValue
import io.opentelemetry.proto.common.v1.instrumentationScope
import io.opentelemetry.proto.common.v1.keyValue
import io.opentelemetry.proto.resource.v1.resource
import io.opentelemetry.proto.trace.v1.Span
import io.opentelemetry.proto.trace.v1.resourceSpans
import io.opentelemetry.proto.trace.v1.scopeSpans
import io.opentelemetry.proto.trace.v1.span

internal fun Iterable<SpanData>.toExportRequest(
    service: String
): ExportTraceServiceRequest = exportTraceServiceRequest {
    resourceSpansList.add(resourceSpans {
        resource = resource {
            attributesList.add(keyValue {
                key = "service.name"
                value = anyValue { value = AnyValue.Value.StringValue(service) }
            })
        }
        scopeSpansList.add(scopeSpans {
            scope = instrumentationScope {
                name = "otlp"
                version = "1.0.0"
            }
            this@toExportRequest.forEach { s ->
                spansList.add(s.toProto())
            }
        })
    })
}

private fun SpanData.toProto(): Span = span {
    traceId = spanContext.traceIdBytes
    spanId = spanContext.spanIdBytes
    parentSpanId = if (parentSpanContext.isValid) parentSpanContext.spanIdBytes else ByteArray(0)
    name = this@toProto.name
    kind = this@toProto.kind.toProtoKind()
    startTimeUnixNano = startEpochNanos.toULong()
    endTimeUnixNano = endEpochNanos.toULong()
    attributes.asMap().forEach { (key, value) ->
        attributesList.add(keyValue {
            this.key = key.key
            this.value = anyValue { this.value = key.type.toAnyValue(value) }
        })
    }
}

private fun SpanKind.toProtoKind(): Span.SpanKind = when (this) {
    SpanKind.INTERNAL -> Span.SpanKind.SpanKindInternal
    SpanKind.SERVER -> Span.SpanKind.SpanKindServer
    SpanKind.CLIENT -> Span.SpanKind.SpanKindClient
    SpanKind.PRODUCER -> Span.SpanKind.SpanKindProducer
    SpanKind.CONSUMER -> Span.SpanKind.SpanKindConsumer
}

private fun AttributeType.toAnyValue(value: Any): AnyValue.Value = when (this) {
    AttributeType.STRING -> AnyValue.Value.StringValue(value as String)
    AttributeType.LONG -> AnyValue.Value.IntValue((value as Long))
    AttributeType.DOUBLE -> AnyValue.Value.DoubleValue(value as Double)
    AttributeType.BOOLEAN -> AnyValue.Value.BoolValue(value as Boolean)
    else -> throw IllegalArgumentException("Unsupported attribute type: $this")
}
