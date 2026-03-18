import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.trace.SdkTracerProvider
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import io.opentelemetry.kotlin.sdk.trace.export.BatchSpanProcessor
import io.opentelemetry.kotlin.sdk.trace.export.SpanExporter
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

val exporter = NoopExporter()
val processor = BatchSpanProcessor
    .builder(exporter)
    .setMaxQueueSize(Int.MAX_VALUE)
    .setMaxExportBatchSize(2048)
    .build()
val provider = SdkTracerProvider
    .builder()
    .addSpanProcessor(processor)
    .build()
val tracer = provider
    .tracerBuilder("")
    .build()

fun startSpan(name: String, context: Context): Span {
    val span = tracer
        .spanBuilder(name)
        .setParent(context)
        .setStartTimestamp(Clock.System.now())
        .startSpan()
    context.with(span).makeCurrent()
    return span
}

fun endSpan(span: Span, context: Context) {
    span.end(Clock.System.now())
    context.makeCurrent()
}

fun <T> span(name: String, action: () -> T): T {
    val context = Context.current()
    val span = startSpan(name, context)
    try {
        return action()
    } finally {
        endSpan(span, context)
    }
}

expect fun await(exporter: NoopExporter, start: Instant)

class NoopExporter() : SpanExporter {
    var exported = 0

    suspend fun await() {
        println("exported $exported spans")
    }

    override fun export(
        spans: Collection<SpanData>
    ): CompletableResultCode {
        exported += spans.size
        return CompletableResultCode.ofSuccess()
    }

    override fun flush(): CompletableResultCode {
        return CompletableResultCode.ofSuccess()
    }

    override fun shutdown() = flush()
}
