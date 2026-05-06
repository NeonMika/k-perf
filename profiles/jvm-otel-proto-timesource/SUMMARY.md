# Profile -- otel-proto-timesource (Protobuf/gRPC + monotonic clock) (jvm)

**Variant:** `otel-proto-timesource`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-05-05 22:27:53  
**Profile file last captured:** 2026-05-05 22:27:51  
**Profile file:** [otel-proto-timesource.jfr](otel-proto-timesource.jfr)  
**Wall time (capture run):** 3508 ms (incl. profiler overhead)  
**Workload-reported time:** 1816 ms  

---

## Top 30 frames

```
Profile: otel-proto-timesource.jfr
Wall (samples*10ms): 1.1 s, 108 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
    220  |      230  |       22  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
    110  |      110  |       11  | java/lang/String.charAt
     60  |       70  |        6  | kotlin/jvm/internal/Intrinsics.areEqual
     50  |       50  |        5  | io/opentelemetry/proto/trace/v1/Span.<init>
     30  |      130  |        3  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
     20  |       20  |        2  | ...metry/kotlin/sdk/trace/RecordEventsReadableSpan$Companion.startSpan
     20  |       20  |        2  | .../timortel/kmpgrpc/shared/internal/io/WireformatKt.wireFormatMakeTag
     20  |       20  |        2  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch
     20  |       30  |        2  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.trySteal
     20  |       30  |        2  | io/grpc/okhttp/internal/framed/Hpack$Writer.writeHeaders
     20  |       30  |        2  | kotlinx/io/Buffer.write
     20  |       20  |        2  | io/opentelemetry/proto/trace/v1/ScopeSpans.<init>
     20  |       20  |        2  | java/lang/Class.cast
     10  |       10  |        1  | ...ntelemetry/kotlin/api/internal/OtelEncodingUtils.buildValidHexArray
     10  |       10  |        1  | kotlin/coroutines/ContinuationInterceptor$DefaultImpls.get
     10  |       30  |        1  | ...el/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessageArray
     10  |       10  |        1  | kotlinx/io/Segment.getLimit
     10  |       40  |        1  | kotlinx/io/Sink.write$default
     10  |       10  |        1  | ...emetry/kotlin/sdk/trace/SpanWrapper$Companion$Implementation.<init>
     10  |       10  |        1  | kotlinx/io/Segment.writeLong$kotlinx_io_core
     10  |       20  |        1  | kotlinx/io/SinksKt.writeULongLe-2TYgG_w
     10  |       10  |        1  | ...entelemetry/proto/trace/v1/Status$StatusCode$StatusCodeUnset.equals
     10  |       10  |        1  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder$links$2.invoke
     10  |       10  |        1  | ...telemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpan
     10  |       10  |        1  | ...ntelemetry/kotlin/api/internal/OtelEncodingUtils.longToBase16String
     10  |       10  |        1  | java/util/concurrent/atomic/AtomicReferenceArray.length
     10  |       10  |        1  | io/opentelemetry/kotlin/context/ArrayBasedContext.with
     10  |       60  |        1  | com/infendro/otlp/SerializationKt.toProto
     10  |       10  |        1  | kotlin/jvm/internal/Intrinsics.sanitizeStackTrace
     10  |       10  |        1  | ...tions/immutable/implementations/immutableList/TrieIterator.fillPath

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
      0  |      280  |        0  | kotlin/collections/AbstractList.indexOf
      0  |      270  |        0  | ...table/implementations/immutableList/AbstractPersistentList.contains
    220  |      230  |       22  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
      0  |      230  |        0  | ...llections/immutable/implementations/immutableList/TrieIterator.next
      0  |      230  |        0  | ...mutable/implementations/immutableList/PersistentVectorIterator.next
     30  |      130  |        3  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
    110  |      110  |       11  | java/lang/String.charAt
      0  |      100  |        0  | kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
      0  |       90  |        0  | ...tlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
      0  |       90  |        0  | io/opentelemetry/proto/trace/v1/Span.serialize
      0  |       80  |        0  | ...telemetry/kotlin/api/internal/OtelEncodingUtils.isValidBase16String
      0  |       80  |        0  | io/opentelemetry/kotlin/api/trace/TraceId.isValid
      0  |       80  |        0  | io/opentelemetry/kotlin/api/trace/SpanContext$DefaultImpls.isValid
      0  |       80  |        0  | io/opentelemetry/kotlin/api/internal/ImmutableSpanContext.isValid
      0  |       80  |        0  | ...imortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
      0  |       70  |        0  | kotlinx/coroutines/DispatchedTaskKt.resume
     60  |       70  |        6  | kotlin/jvm/internal/Intrinsics.areEqual
     10  |       60  |        1  | com/infendro/otlp/SerializationKt.toProto
      0  |       50  |        0  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
      0  |       50  |        0  | .../timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeBytes
      0  |       50  |        0  | ...ementations/immutableList/AbstractPersistentList$removeAll$1.invoke
     50  |       50  |        5  | io/opentelemetry/proto/trace/v1/Span.<init>
      0  |       50  |        0  | io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
      0  |       50  |        0  | ...lemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
      0  |       50  |        0  | io/opentelemetry/proto/trace/v1/SpanDSL.build
      0  |       40  |        0  | MainKt._startSpan
     10  |       40  |        1  | kotlinx/io/Sink.write$default
     10  |       40  |        1  | com/infendro/otlp/SerializationKt.toExportRequest
      0  |       40  |        0  | com/infendro/otlp/OtlpExporter.export
      0  |       40  |        0  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.exportCurrentBatch
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
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 28 samples
Aggregate: self=0 ms (on top), total=280 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
            0  |            270  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
            0  |             10  | (root)

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
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 28 samples
Aggregate: self=150 ms (on top), total=280 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           20  |             60  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
           30  |             50  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
            0  |             40  | (root)
           40  |             40  | io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
           20  |             30  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan

=== 5 sample chains (root <- ... <- match) ===
[match: io/opentelemetry/kotlin/sdk/trace/RecordEventsReadableSpan$Companion.startSpan]
  MainKt.fibonacci
    MainKt.fibonacci
      MainKt._startSpan
        io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
          io/opentelemetry/kotlin/sdk/trace/RecordEventsReadableSpan$Companion.startSpan

[match: io/opentelemetry/kotlin/sdk/trace/RecordEventsReadableSpan$Companion.startSpan]
  MainKt.fibonacci
    MainKt.fibonacci
      MainKt._startSpan
        io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
          io/opentelemetry/kotlin/sdk/trace/RecordEventsReadableSpan$Companion.startSpan

[match: io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run]
  kotlinx/coroutines/DispatchedTaskKt.resumeUnconfined
    kotlinx/coroutines/DispatchedTaskKt.resume
      kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
        io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
          io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run

[match: io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder$links$2.invoke]
  io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
    io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.getLinks
      kotlin/SynchronizedLazyImpl.getValue
        io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder$links$2.invoke
          io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder$links$2.invoke

[match: io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpan]
  MainKt._endSpan
    io/opentelemetry/kotlin/sdk/trace/RecordEventsReadableSpan.end
      io/opentelemetry/kotlin/sdk/trace/RecordEventsReadableSpan.endInternal
        io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor.onEnd
          io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpan
```

---

## How to view interactively

Open ``otel-proto-timesource.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

