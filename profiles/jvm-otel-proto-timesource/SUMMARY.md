# Profile -- otel-proto-timesource (Protobuf/gRPC + monotonic clock) (jvm)

**Variant:** `otel-proto-timesource`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-05-20 20:42:33  
**Profile file last captured:** 2026-05-20 20:42:31  
**Profile file:** [otel-proto-timesource.jfr](otel-proto-timesource.jfr)  
**Wall time (capture run):** 2395 ms (incl. profiler overhead)  
**Workload-reported time:** 783 ms  

---

## Top 30 frames

```
Profile: otel-proto-timesource.jfr
Wall (samples*10ms): 0.3 s, 34 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
    190  |      190  |       19  | java/lang/String.charAt
     30  |       50  |        3  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
     30  |       80  |        3  | MainKt._startSpan
     10  |       10  |        1  | java/lang/String.indexOf
     10  |       10  |        1  | kotlinx/coroutines/channels/BufferedChannel.trySend-JP2dKIU
     10  |       10  |        1  | kotlin/jvm/internal/Intrinsics.sanitizeStackTrace
     10  |       10  |        1  | io/opentelemetry/kotlin/context/Context$DefaultImpls.getOrElse
     10  |       10  |        1  | io/opentelemetry/kotlin/api/internal/TemporaryBuffers.chars
     10  |       10  |        1  | MainKt.main
     10  |       10  |        1  | io/opentelemetry/proto/trace/v1/Span.<init>
     10  |       10  |        1  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
     10  |       10  |        1  | .../timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeBytes

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
    190  |      190  |       19  | java/lang/String.charAt
      0  |      190  |        0  | ...telemetry/kotlin/api/internal/OtelEncodingUtils.isValidBase16String
      0  |      190  |        0  | io/opentelemetry/kotlin/api/trace/TraceId.isValid
      0  |      190  |        0  | io/opentelemetry/kotlin/api/trace/SpanContext$DefaultImpls.isValid
      0  |      190  |        0  | io/opentelemetry/kotlin/api/internal/ImmutableSpanContext.isValid
     30  |       80  |        3  | MainKt._startSpan
      0  |       60  |        0  | MainKt.fibonacci
     30  |       50  |        3  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
     10  |       10  |        1  | java/lang/String.indexOf
      0  |       10  |        0  | sun/net/www/protocol/jar/Handler.canonicalizeString
      0  |       10  |        0  | sun/net/www/protocol/jar/Handler.parseURL
      0  |       10  |        0  | java/net/URL.<init>
     10  |       10  |        1  | kotlinx/coroutines/channels/BufferedChannel.trySend-JP2dKIU
      0  |       10  |        0  | ...telemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpan
      0  |       10  |        0  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor.onEnd
      0  |       10  |        0  | io/opentelemetry/kotlin/sdk/trace/RecordEventsReadableSpan.endInternal
      0  |       10  |        0  | io/opentelemetry/kotlin/sdk/trace/RecordEventsReadableSpan.end
     10  |       10  |        1  | kotlin/jvm/internal/Intrinsics.sanitizeStackTrace
      0  |       10  |        0  | kotlin/jvm/internal/Intrinsics.throwJavaNpe
      0  |       10  |        0  | kotlin/jvm/internal/Intrinsics.checkNotNull
      0  |       10  |        0  | kotlin/SynchronizedLazyImpl.getValue
      0  |       10  |        0  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.getLinks
     10  |       10  |        1  | io/opentelemetry/kotlin/context/Context$DefaultImpls.getOrElse
      0  |       10  |        0  | io/opentelemetry/kotlin/context/ArrayBasedContext.getOrElse
      0  |       10  |        0  | io/opentelemetry/kotlin/api/trace/Span$Companion.fromContext
     10  |       10  |        1  | io/opentelemetry/kotlin/api/internal/TemporaryBuffers.chars
      0  |       10  |        0  | io/opentelemetry/kotlin/api/trace/SpanId.fromLong
      0  |       10  |        0  | io/opentelemetry/kotlin/sdk/trace/RandomIdGenerator.generateSpanId
     10  |       10  |        1  | MainKt.main
     10  |       10  |        1  | io/opentelemetry/proto/trace/v1/Span.<init>
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/ matched 0 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 1 samples
Aggregate: self=0 ms (on top), total=10 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
            0  |             10  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains

=== 5 sample chains (root <- ... <- match) ===
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Pattern /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/ matched 0 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 8 samples
Aggregate: self=40 ms (on top), total=80 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           30  |             50  | MainKt._startSpan
            0  |             10  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor.onEnd
            0  |             10  | (root)
           10  |             10  | io/opentelemetry/proto/trace/v1/Span.<init>

=== 5 sample chains (root <- ... <- match) ===
[match: io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan]
  MainKt.fibonacci
    MainKt.fibonacci
      MainKt.fibonacci
        MainKt._startSpan
          io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan

[match: io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan]
  MainKt.fibonacci
    MainKt.fibonacci
      MainKt.fibonacci
        MainKt._startSpan
          io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan

[match: io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan]
  MainKt.fibonacci
    MainKt.fibonacci
      MainKt.fibonacci
        MainKt._startSpan
          io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan

[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  io/opentelemetry/proto/trace/v1/SpanDSL.build
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/opentelemetry/proto/trace/v1/Span.<init>
```

---

## How to view interactively

Open ``otel-proto-timesource.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

