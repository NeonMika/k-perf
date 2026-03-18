import com.infendro.otlp.OtlpExporter
import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.trace.SdkTracerProvider
import io.opentelemetry.kotlin.sdk.trace.export.BatchSpanProcessor
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

val host = "localhost:4318"
val service = "manual"
val exporter = OtlpExporter(host, service)
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

expect fun await(exporter: OtlpExporter, start: Instant)
