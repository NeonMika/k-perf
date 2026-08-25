# Profile -- otel-proto-anchored (Protobuf/gRPC + SDK AnchoredClock) (jvm)

**Variant:** `otel-proto-anchored`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-06-13 15:57:09  
**Profile file last captured:** 2026-06-13 15:57:05  
**Profile file:** [otel-proto-anchored.jfr](otel-proto-anchored.jfr)  
**Wall time (capture run):** 22986 ms (incl. profiler overhead)  
**Workload-reported time:** 21092 ms  

---

## Top 30 frames

```
Profile: otel-proto-anchored.jfr
Wall (samples*10ms): 13.7 s, 1369 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
   5880  |     5890  |      588  | kotlin/jvm/internal/Intrinsics.areEqual
   2020  |     2390  |      202  | io/opentelemetry/proto/trace/v1/Span.<init>
    360  |      360  |       36  | kotlinx/io/Utf8Kt.writeString
    360  |      540  |       36  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
    320  |      320  |       32  | java/lang/String.charAt
    300  |      300  |       30  | ...tions/immutable/implementations/immutableList/TrieIterator.fillPath
    290  |      480  |       29  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
    290  |      310  |       29  | kotlinx/io/Buffer.write
    270  |     1100  |       27  | io/opentelemetry/proto/trace/v1/Span.serialize
    260  |      260  |       26  | kotlinx/io/Buffer.writableSegment
    260  |      510  |       26  | okio/Utf8.size$default
    210  |      940  |       21  | ...imortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
    200  |      260  |       20  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
    140  |     2140  |       14  | com/infendro/otlp/SerializationKt.toProto
    100  |      290  |       10  | com/infendro/otlp/SerializationKt.toExportRequest
     70  |      100  |        7  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch
     60  |      130  |        6  | kotlinx/coroutines/channels/BufferedChannel.updateCellReceive
     60  |       70  |        6  | io/opentelemetry/proto/trace/v1/ScopeSpans.<init>
     60  |       60  |        6  | kotlinx/coroutines/channels/BufferedChannel.expandBuffer
     60  |       60  |        6  | ...metry/kotlin/sdk/trace/RecordEventsReadableSpan$Companion.startSpan
     40  |       40  |        4  | .../timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize
     40  |       40  |        4  | kotlinx/io/SourcesKt.readByteArrayImpl
     40  |       40  |        4  | java/time/Clock.currentInstant
     30  |       30  |        3  | java/lang/Class.getComponentType
     30  |       30  |        3  | io/github/timortel/kmpgrpc/core/io/DataSize.computeTagSize
     30  |       30  |        3  | kotlin/coroutines/CombinedContext.get
     30  |       30  |        3  | kotlinx/coroutines/scheduling/CoroutineScheduler.parkedWorkersStackPop
     30  |       60  |        3  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.runWorker
     30  |       70  |        3  | ...telemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpan
     30  |       30  |        3  | ...icReferenceFieldUpdater$AtomicReferenceFieldUpdaterImpl.accessCheck

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
     10  |     6210  |        1  | kotlin/collections/AbstractList.indexOf
     30  |     6050  |        3  | ...table/implementations/immutableList/AbstractPersistentList.contains
   5880  |     5890  |      588  | kotlin/jvm/internal/Intrinsics.areEqual
      0  |     5770  |        0  | ...ementations/immutableList/AbstractPersistentList$removeAll$1.invoke
   2020  |     2390  |      202  | io/opentelemetry/proto/trace/v1/Span.<init>
      0  |     2180  |        0  | io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
     10  |     2170  |        1  | ...lemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
    140  |     2140  |       14  | com/infendro/otlp/SerializationKt.toProto
      0  |     2030  |        0  | io/opentelemetry/proto/trace/v1/SpanDSL.build
    270  |     1100  |       27  | io/opentelemetry/proto/trace/v1/Span.serialize
    210  |      940  |       21  | ...imortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
     10  |      790  |        1  | kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
    360  |      540  |       36  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
      0  |      540  |        0  | ...tel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeStringNoTag
      0  |      540  |        0  | ...timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeString
      0  |      530  |        0  | ...el/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessageArray
    260  |      510  |       26  | okio/Utf8.size$default
    290  |      480  |       29  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
      0  |      480  |        0  | ...llections/immutable/implementations/immutableList/TrieIterator.next
      0  |      480  |        0  | ...mutable/implementations/immutableList/PersistentVectorIterator.next
      0  |      470  |        0  | ...tlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
      0  |      460  |        0  | io/opentelemetry/proto/trace/v1/ScopeSpans.serialize
      0  |      450  |        0  | kotlinx/coroutines/DispatchedTaskKt.resume
     30  |      390  |        3  | kotlinx/io/Utf8Kt.writeString$default
      0  |      370  |        0  | kotlinx/coroutines/DispatchedTaskKt.resumeUnconfined
    360  |      360  |       36  | kotlinx/io/Utf8Kt.writeString
      0  |      360  |        0  | io/github/timortel/kmpgrpc/core/io/DataSize.computeStringSizeNoTag
      0  |      360  |        0  | io/github/timortel/kmpgrpc/core/io/DataSize.computeStringSize
    320  |      320  |       32  | java/lang/String.charAt
    290  |      310  |       29  | kotlinx/io/Buffer.write
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/ matched 17 samples
Aggregate: self=90 ms (on top), total=170 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           40  |             40  | java/time/Clock$SystemClock.instant
            0  |             30  | (root)
            0  |             20  | kotlinx/coroutines/channels/BufferedChannel$BufferedChannelIterator.hasNext
            0  |             20  | io/grpc/internal/MessageFramer.writeUncompressed
           20  |             20  | io/opentelemetry/proto/trace/v1/Span.<init>

=== 5 sample chains (root <- ... <- match) ===
[match: io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize]
  io/opentelemetry/proto/trace/v1/Status$Companion.invoke$default
    io/opentelemetry/proto/trace/v1/Status$Companion.invoke
      io/opentelemetry/proto/trace/v1/Status.<init>
        io/opentelemetry/proto/trace/v1/Status.<init>
          io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize

[match: io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize]
  io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
      io/opentelemetry/proto/trace/v1/Span.<init>
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize

[match: java/time/Clock.currentInstant]
  io/opentelemetry/kotlin/sdk/common/SystemClock.nanoTime
    kotlinx/datetime/Clock$System.now
      kotlinx/datetime/Instant$Companion.now
        java/time/Clock$SystemClock.instant
          java/time/Clock.currentInstant

[match: io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize]
  io/opentelemetry/proto/collector/trace/v1/ExportTraceServiceRequest$Companion.createPartial$default
    io/opentelemetry/proto/collector/trace/v1/ExportTraceServiceRequest$Companion.createPartial
      io/opentelemetry/proto/collector/trace/v1/ExportTraceServiceRequest.<init>
        io/opentelemetry/proto/collector/trace/v1/ExportTraceServiceRequest.<init>
          io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize

[match: java/time/Clock.currentInstant]
  io/opentelemetry/kotlin/sdk/common/SystemClock.nanoTime
    kotlinx/datetime/Clock$System.now
      kotlinx/datetime/Instant$Companion.now
        java/time/Clock$SystemClock.instant
          java/time/Clock.currentInstant
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 640 samples
Aggregate: self=40 ms (on top), total=6400 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           10  |           6020  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
            0  |            310  | (root)
            0  |             40  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.removeSpanDataFromBatch
           30  |             30  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke

=== 5 sample chains (root <- ... <- match) ===
[match: kotlin/collections/AbstractList.indexOf]
  kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.recyclableRemoveAll
    kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
      kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
        kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
          kotlin/collections/AbstractList.indexOf

[match: kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains]
  kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.removeAll
    kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.removeAll
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
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 333 samples
Aggregate: self=2760 ms (on top), total=3330 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
         1970  |           1970  | io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
           50  |            420  | io/opentelemetry/proto/trace/v1/Span.<init>
          360  |            370  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
          200  |            200  | MainKt._startSpan
            0  |            140  | (root)

=== 5 sample chains (root <- ... <- match) ===
[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  io/opentelemetry/proto/trace/v1/SpanDSL.build
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/opentelemetry/proto/trace/v1/Span.<init>

[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  io/opentelemetry/proto/trace/v1/SpanDSL.build
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/opentelemetry/proto/trace/v1/Span.<init>

[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  io/opentelemetry/proto/trace/v1/SpanDSL.build
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/opentelemetry/proto/trace/v1/Span.<init>

[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  com/infendro/otlp/SerializationKt.toProto
    io/opentelemetry/proto/trace/v1/SpanDSL.build
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
        io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
          io/opentelemetry/proto/trace/v1/Span.<init>

[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  io/opentelemetry/proto/trace/v1/SpanDSL.build
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/opentelemetry/proto/trace/v1/Span.<init>
```

---

## How to view interactively

Open ``otel-proto-anchored.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

