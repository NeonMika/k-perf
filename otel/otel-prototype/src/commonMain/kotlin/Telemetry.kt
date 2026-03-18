import com.infendro.otlp.OtlpExporter
import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.trace.SdkTracerProvider
import io.opentelemetry.kotlin.sdk.trace.SpanProcessor
import io.opentelemetry.kotlin.sdk.trace.export.BatchSpanProcessor
import kotlinx.datetime.Clock

val exporter = OtlpExporter("http://localhost:4318", "prototype")
val processor: SpanProcessor = BatchSpanProcessor.builder(exporter).build()
val provider = SdkTracerProvider.builder().addSpanProcessor(processor).build()
val tracer = provider.tracerBuilder("").build()

fun <T> span(
    name: String,
    action: (Span) -> T
): T {
    val context = Context.current()
    val span = tracer.spanBuilder(name)
        .setParent(context)
        .setStartTimestamp(Clock.System.now())
        .startSpan()
    context.with(span).makeCurrent()

    val result = action(span)

    context.makeCurrent()
    span.end(Clock.System.now())

    return result
}
