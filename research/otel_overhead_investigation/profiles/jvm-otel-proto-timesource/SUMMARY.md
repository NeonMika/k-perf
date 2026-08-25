# Profile -- otel-proto-timesource (Protobuf/gRPC + monotonic clock) (jvm)

**Variant:** `otel-proto-timesource`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-06-13 15:56:19  
**Profile file last captured:** 2026-06-13 15:56:14  
**Profile file:** [otel-proto-timesource.jfr](otel-proto-timesource.jfr)  
**Wall time (capture run):** 22122 ms (incl. profiler overhead)  
**Workload-reported time:** 20443 ms  

---

## Top 30 frames

```
Profile: otel-proto-timesource.jfr
Wall (samples*10ms): 15.4 s, 1541 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
   5830  |     6100  |      583  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
   1720  |     1910  |      172  | io/opentelemetry/proto/trace/v1/SpanDSL.build
    900  |     1600  |       90  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
    480  |      480  |       48  | io/opentelemetry/kotlin/api/trace/Span$Companion.fromContext
    460  |      860  |       46  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
    370  |      380  |       37  | kotlinx/io/Buffer.write
    350  |      350  |       35  | java/lang/String.charAt
    340  |      340  |       34  | ...tions/immutable/implementations/immutableList/TrieIterator.fillPath
    280  |      590  |       28  | ...el/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessageArray
    210  |      210  |       21  | kotlinx/io/Utf8Kt.writeString
    210  |      260  |       21  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch
    200  |      200  |       20  | kotlinx/io/Buffer.writableSegment
    200  |      440  |       20  | okio/Utf8.size$default
    180  |      410  |       18  | io/opentelemetry/proto/trace/v1/Span.<init>
    170  |      990  |       17  | io/opentelemetry/proto/trace/v1/Span.serialize
    170  |      180  |       17  | ...metry/kotlin/sdk/trace/RecordEventsReadableSpan$Companion.startSpan
    140  |      170  |       14  | kotlinx/coroutines/channels/BufferedChannel.findSegmentSend
    130  |      310  |       13  | ...telemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpan
    110  |      130  |       11  | kotlin/jvm/internal/Intrinsics.throwParameterIsNullNPE
    110  |     1820  |       11  | MainKt._startSpan
    110  |      120  |       11  | io/opentelemetry/kotlin/sdk/trace/SdkTracer.spanBuilder
    100  |     1070  |       10  | ...imortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
    100  |      100  |       10  | kotlin/jvm/internal/Intrinsics.areEqual
     90  |       90  |        9  | ...icReferenceFieldUpdater$AtomicReferenceFieldUpdaterImpl.accessCheck
     80  |     1990  |        8  | com/infendro/otlp/SerializationKt.toExportRequest
     80  |      100  |        8  | kotlinx/coroutines/channels/BufferedChannel.expandBuffer
     70  |       70  |        7  | okio/SegmentPool.take
     60  |      160  |        6  | kotlinx/coroutines/channels/BufferedChannel.receive$suspendImpl
     50  |       50  |        5  | ...AtomicIntegerFieldUpdater$AtomicIntegerFieldUpdaterImpl.accessCheck
     50  |       60  |        5  | io/opentelemetry/proto/trace/v1/ResourceSpans.<init>

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
      0  |     6140  |        0  | kotlin/collections/AbstractList.indexOf
   5830  |     6100  |      583  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
      0  |     6100  |        0  | ...llections/immutable/implementations/immutableList/TrieIterator.next
      0  |     6100  |        0  | ...mutable/implementations/immutableList/PersistentVectorIterator.next
     40  |     5900  |        4  | ...table/implementations/immutableList/AbstractPersistentList.contains
     10  |     2750  |        1  | kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
     40  |     2000  |        4  | com/infendro/otlp/SerializationKt.toProto
     80  |     1990  |        8  | com/infendro/otlp/SerializationKt.toExportRequest
      0  |     1990  |        0  | com/infendro/otlp/OtlpExporter$export$job$1.invokeSuspend
   1720  |     1910  |      172  | io/opentelemetry/proto/trace/v1/SpanDSL.build
     50  |     1870  |        5  | MainKt.fibonacci
    110  |     1820  |       11  | MainKt._startSpan
    900  |     1600  |       90  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
    100  |     1070  |       10  | ...imortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
    170  |      990  |       17  | io/opentelemetry/proto/trace/v1/Span.serialize
    460  |      860  |       46  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
     10  |      850  |        1  | ...tlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
     10  |      730  |        1  | kotlinx/coroutines/DispatchedTaskKt.resume
    280  |      590  |       28  | ...el/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessageArray
     10  |      590  |        1  | io/opentelemetry/proto/trace/v1/ScopeSpans.serialize
     10  |      500  |        1  | kotlinx/coroutines/DispatchedTaskKt.resumeUnconfined
    480  |      480  |       48  | io/opentelemetry/kotlin/api/trace/Span$Companion.fromContext
      0  |      450  |        0  | ...telemetry/kotlin/sdk/trace/samplers/ParentBasedSampler.shouldSample
    200  |      440  |       20  | okio/Utf8.size$default
     10  |      420  |        1  | ...timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeString
    180  |      410  |       18  | io/opentelemetry/proto/trace/v1/Span.<init>
     10  |      400  |        1  | .../timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeBytes
    370  |      380  |       37  | kotlinx/io/Buffer.write
      0  |      380  |        0  | kotlinx/io/Sink.write$default
      0  |      380  |        0  | ...tel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeStringNoTag
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/ matched 9 samples
Aggregate: self=20 ms (on top), total=90 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
            0  |             30  | io/grpc/internal/MessageFramer.writeUncompressed
            0  |             20  | kotlinx/coroutines/channels/BufferedChannel$BufferedChannelIterator.hasNext
            0  |             20  | (root)
           10  |             10  | io/opentelemetry/proto/trace/v1/ResourceSpans.<init>
           10  |             10  | io/opentelemetry/kotlin/sdk/common/SystemClock.nanoTime

=== 5 sample chains (root <- ... <- match) ===
[match: io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize]
  io/opentelemetry/proto/trace/v1/ResourceSpans$Companion.createPartial$default
    io/opentelemetry/proto/trace/v1/ResourceSpans$Companion.createPartial
      io/opentelemetry/proto/trace/v1/ResourceSpans.<init>
        io/opentelemetry/proto/trace/v1/ResourceSpans.<init>
          io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize

[match: kotlinx/datetime/Clock$System.now]
  io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
    io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
      io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.updateNextExportTime
        io/opentelemetry/kotlin/sdk/common/SystemClock.nanoTime
          kotlinx/datetime/Clock$System.now
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 634 samples
Aggregate: self=40 ms (on top), total=6340 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
            0  |           5850  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
            0  |            350  | (root)
            0  |             60  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.iterator
           40  |             50  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
            0  |             30  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.removeSpanDataFromBatch

=== 5 sample chains (root <- ... <- match) ===
[match: kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains]
  kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.removeAll
    kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.recyclableRemoveAll
      kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
        kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
          kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
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
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 324 samples
Aggregate: self=2090 ms (on top), total=3240 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
          900  |           1420  | MainKt._startSpan
          460  |            550  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
          230  |            300  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
          130  |            290  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor.onEnd
           20  |            250  | io/opentelemetry/proto/trace/v1/Span.<init>

=== 5 sample chains (root <- ... <- match) ===
[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  io/opentelemetry/proto/trace/v1/SpanDSL.build
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/opentelemetry/proto/trace/v1/Span.<init>

[match: io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.<init>]
  MainKt.fibonacci
    MainKt.fibonacci
      MainKt._startSpan
        io/opentelemetry/kotlin/sdk/trace/SdkTracer.spanBuilder
          io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.<init>

[match: io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run]
  kotlinx/coroutines/DispatchedTaskKt.resumeUnconfined
    kotlinx/coroutines/DispatchedTaskKt.resume
      kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
        io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
          io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run

[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  io/opentelemetry/proto/trace/v1/SpanDSL.build
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/opentelemetry/proto/trace/v1/Span.<init>

[match: io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan]
  MainKt.fibonacci
    MainKt.fibonacci
      MainKt.fibonacci
        MainKt._startSpan
          io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
```

---

## How to view interactively

Open ``otel-proto-timesource.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

