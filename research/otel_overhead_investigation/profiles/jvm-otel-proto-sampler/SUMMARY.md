# Profile -- otel-proto-sampler (Protobuf/gRPC + alwaysOn sampler) (jvm)

**Variant:** `otel-proto-sampler`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-06-13 15:55:31  
**Profile file last captured:** 2026-06-13 15:55:27  
**Profile file:** [otel-proto-sampler.jfr](otel-proto-sampler.jfr)  
**Wall time (capture run):** 20549 ms (incl. profiler overhead)  
**Workload-reported time:** 18829 ms  

---

## Top 30 frames

```
Profile: otel-proto-sampler.jfr
Wall (samples*10ms): 13.4 s, 1344 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
   5760  |     5770  |      576  | kotlin/jvm/internal/Intrinsics.areEqual
   1820  |     2110  |      182  | io/opentelemetry/proto/trace/v1/Span.<init>
    500  |      820  |       50  | ...el/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessageArray
    440  |      740  |       44  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
    340  |      340  |       34  | java/lang/String.charAt
    290  |      300  |       29  | kotlinx/io/Utf8Kt.writeString
    260  |      260  |       26  | ...tions/immutable/implementations/immutableList/TrieIterator.fillPath
    240  |      420  |       24  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
    210  |      210  |       21  | io/opentelemetry/kotlin/api/trace/Span$Companion.fromContext
    200  |      210  |       20  | kotlinx/io/Buffer.write
    200  |      450  |       20  | okio/Utf8.size$default
    160  |     2080  |       16  | com/infendro/otlp/SerializationKt.toProto
    120  |      360  |       12  | com/infendro/otlp/SerializationKt.toExportRequest
    120  |      180  |       12  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch
    100  |      100  |       10  | kotlinx/coroutines/channels/BufferedChannel.expandBuffer
    100  |     1180  |       10  | io/opentelemetry/proto/trace/v1/Span.serialize
    100  |     1150  |       10  | ...imortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
     90  |       90  |        9  | io/opentelemetry/proto/trace/v1/ScopeSpans.<init>
     80  |       80  |        8  | java/util/Arrays.copyOf
     60  |       90  |        6  | kotlin/jvm/internal/Intrinsics.throwParameterIsNullNPE
     60  |      130  |        6  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.runWorker
     60  |      360  |        6  | kotlinx/io/Utf8Kt.writeString$default
     60  |       60  |        6  | java/util/ArrayList$Itr.next
     50  |       50  |        5  | ...til/concurrent/atomic/AtomicLongFieldUpdater$CASUpdater.accessCheck
     40  |       40  |        4  | io/github/timortel/kmpgrpc/core/io/DataSize.computeTagSize
     40  |       80  |        4  | ...ble/implementations/immutableList/PersistentVectorBuilder.removeAll
     40  |       40  |        4  | ...icReferenceFieldUpdater$AtomicReferenceFieldUpdaterImpl.accessCheck
     40  |      150  |        4  | kotlinx/coroutines/channels/BufferedChannel.receive$suspendImpl
     30  |       40  |        3  | kotlinx/io/Buffer.writableSegment
     30  |       30  |        3  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.exportCurrentBatch

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
     10  |     6110  |        1  | kotlin/collections/AbstractList.indexOf
     20  |     5950  |        2  | ...table/implementations/immutableList/AbstractPersistentList.contains
   5760  |     5770  |      576  | kotlin/jvm/internal/Intrinsics.areEqual
      0  |     5720  |        0  | ...ementations/immutableList/AbstractPersistentList$removeAll$1.invoke
   1820  |     2110  |      182  | io/opentelemetry/proto/trace/v1/Span.<init>
    160  |     2080  |       16  | com/infendro/otlp/SerializationKt.toProto
     10  |     1970  |        1  | io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
     10  |     1930  |        1  | ...lemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
      0  |     1840  |        0  | io/opentelemetry/proto/trace/v1/SpanDSL.build
    100  |     1180  |       10  | io/opentelemetry/proto/trace/v1/Span.serialize
     20  |     1160  |        2  | kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
    100  |     1150  |       10  | ...imortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
    500  |      820  |       50  | ...el/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessageArray
     10  |      790  |        1  | io/opentelemetry/proto/trace/v1/ScopeSpans.serialize
    440  |      740  |       44  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
      0  |      730  |        0  | ...tlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
      0  |      620  |        0  | ...tel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeStringNoTag
      0  |      610  |        0  | ...timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeString
      0  |      610  |        0  | kotlinx/coroutines/DispatchedTaskKt.resume
    200  |      450  |       20  | okio/Utf8.size$default
      0  |      440  |        0  | kotlinx/coroutines/DispatchedTaskKt.resumeUnconfined
      0  |      430  |        0  | ...llections/immutable/implementations/immutableList/TrieIterator.next
      0  |      420  |        0  | ...mutable/implementations/immutableList/PersistentVectorIterator.next
    240  |      420  |       24  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
     10  |      400  |        1  | kotlinx/coroutines/DispatchedTask.run
      0  |      370  |        0  | com/infendro/otlp/OtlpExporter$export$job$1.invokeSuspend
    120  |      360  |       12  | com/infendro/otlp/SerializationKt.toExportRequest
     60  |      360  |        6  | kotlinx/io/Utf8Kt.writeString$default
    340  |      340  |       34  | java/lang/String.charAt
    290  |      300  |       29  | kotlinx/io/Utf8Kt.writeString
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/ matched 7 samples
Aggregate: self=60 ms (on top), total=70 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           10  |             10  | io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeUnknownFields
           10  |             10  | io/grpc/internal/MessageFramer.writeUncompressed
            0  |             10  | (root)
           10  |             10  | io/opentelemetry/proto/trace/v1/ScopeSpans.serialize
           10  |             10  | io/opentelemetry/proto/trace/v1/Span.<init>

=== 5 sample chains (root <- ... <- match) ===
[match: io/github/timortel/kmpgrpc/core/io/CodedOutputStream.writeUnknownFields]
  io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessageArray
    io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
      io/opentelemetry/proto/trace/v1/Span.serialize
        io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeUnknownFields
          io/github/timortel/kmpgrpc/core/io/CodedOutputStream.writeUnknownFields

[match: io/grpc/internal/MessageFramer.writeKnownLengthUncompressed]
  io/grpc/internal/ForwardingClientStream.writeMessage
    io/grpc/internal/AbstractStream.writeMessage
      io/grpc/internal/MessageFramer.writePayload
        io/grpc/internal/MessageFramer.writeUncompressed
          io/grpc/internal/MessageFramer.writeKnownLengthUncompressed

[match: io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeUnknownFields]
  io/opentelemetry/proto/trace/v1/ResourceSpans.serialize
    io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessageArray
      io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
        io/opentelemetry/proto/trace/v1/ScopeSpans.serialize
          io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeUnknownFields

[match: io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize]
  io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
      io/opentelemetry/proto/trace/v1/Span.<init>
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize

[match: java/time/Instant.create]
  kotlinx/datetime/Instant$Companion.now
    java/time/Clock$SystemClock.instant
      java/time/Clock.currentInstant
        java/time/Instant.ofEpochSecond
          java/time/Instant.create
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 627 samples
Aggregate: self=30 ms (on top), total=6270 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           10  |           5930  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
            0  |            280  | (root)
            0  |             40  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.removeSpanDataFromBatch
           20  |             20  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke

=== 5 sample chains (root <- ... <- match) ===
[match: kotlin/collections/AbstractList.indexOf]
  kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.recyclableRemoveAll
    kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
      kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
        kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
          kotlin/collections/AbstractList.indexOf
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
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 315 samples
Aggregate: self=2410 ms (on top), total=3150 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
         1810  |           1810  | io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
          440  |            510  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
           10  |            290  | io/opentelemetry/proto/trace/v1/Span.<init>
          150  |            220  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
            0  |            220  | MainKt._startSpan

=== 5 sample chains (root <- ... <- match) ===
[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  io/opentelemetry/proto/trace/v1/SpanDSL.build
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/opentelemetry/proto/trace/v1/Span.<init>

[match: io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run]
  kotlinx/coroutines/DispatchedTaskKt.resumeUnconfined
    kotlinx/coroutines/DispatchedTaskKt.resume
      kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
        io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
          io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run

[match: io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.exportCurrentBatch]
  kotlinx/coroutines/DispatchedTaskKt.resume
    kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
      io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
        io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
          io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.exportCurrentBatch

[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  com/infendro/otlp/SerializationKt.toProto
    io/opentelemetry/proto/trace/v1/SpanDSL.build
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
        io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
          io/opentelemetry/proto/trace/v1/Span.<init>

[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  com/infendro/otlp/SerializationKt.toProto
    io/opentelemetry/proto/trace/v1/SpanDSL.build
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
        io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
          io/opentelemetry/proto/trace/v1/Span.<init>
```

---

## How to view interactively

Open ``otel-proto-sampler.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

