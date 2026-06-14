# Profile -- otel-proto (Protobuf/gRPC) (jvm)

**Variant:** `otel-proto`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-06-13 15:54:47  
**Profile file last captured:** 2026-06-13 15:54:42  
**Profile file:** [otel-proto.jfr](otel-proto.jfr)  
**Wall time (capture run):** 21510 ms (incl. profiler overhead)  
**Workload-reported time:** 19846 ms  

---

## Top 30 frames

```
Profile: otel-proto.jfr
Wall (samples*10ms): 12.2 s, 1220 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
   5130  |     5140  |      513  | kotlin/jvm/internal/Intrinsics.areEqual
   1400  |     1580  |      140  | io/opentelemetry/proto/trace/v1/SpanDSL.build
    420  |     1120  |       42  | io/opentelemetry/proto/trace/v1/Span.serialize
    310  |      750  |       31  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
    260  |      260  |       26  | java/lang/String.charAt
    250  |      430  |       25  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
    240  |      240  |       24  | kotlinx/io/Utf8Kt.writeString
    220  |      270  |       22  | kotlinx/io/Buffer.write
    220  |      430  |       22  | okio/Utf8.size$default
    220  |      220  |       22  | ...tions/immutable/implementations/immutableList/TrieIterator.fillPath
    210  |      310  |       21  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
    170  |      470  |       17  | io/opentelemetry/proto/trace/v1/Span.<init>
    160  |     1010  |       16  | ...imortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
    160  |      220  |       16  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch
    120  |     1680  |       12  | com/infendro/otlp/SerializationKt.toExportRequest
    120  |      120  |       12  | kotlinx/coroutines/channels/BufferedChannel.expandBuffer
    110  |      230  |       11  | kotlinx/coroutines/channels/BufferedChannel.updateCellReceive
     90  |       90  |        9  | io/opentelemetry/kotlin/sdk/trace/SdkTracer.spanBuilder
     90  |      330  |        9  | kotlinx/io/Utf8Kt.writeString$default
     80  |       90  |        8  | io/opentelemetry/proto/trace/v1/ScopeSpans.<init>
     80  |       80  |        8  | ...icReferenceFieldUpdater$AtomicReferenceFieldUpdaterImpl.accessCheck
     70  |       70  |        7  | kotlinx/io/Buffer.writableSegment
     70  |       70  |        7  | io/opentelemetry/kotlin/api/trace/Span$Companion.fromContext
     50  |       50  |        5  | java/util/Arrays.copyOf
     50  |       50  |        5  | com/google/common/base/Preconditions.checkNotNull
     40  |     1700  |        4  | com/infendro/otlp/SerializationKt.toProto
     40  |       90  |        4  | ...ble/implementations/immutableList/PersistentVectorBuilder.removeAll
     40  |       50  |        4  | kotlin/jvm/internal/Intrinsics.throwParameterIsNullNPE
     40  |       60  |        4  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.runWorker
     40  |       50  |        4  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.exportCurrentBatch

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
     10  |     5420  |        1  | kotlin/collections/AbstractList.indexOf
     30  |     5280  |        3  | ...table/implementations/immutableList/AbstractPersistentList.contains
   5130  |     5140  |      513  | kotlin/jvm/internal/Intrinsics.areEqual
      0  |     5060  |        0  | ...ementations/immutableList/AbstractPersistentList$removeAll$1.invoke
     10  |     2230  |        1  | kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
     40  |     1700  |        4  | com/infendro/otlp/SerializationKt.toProto
    120  |     1680  |       12  | com/infendro/otlp/SerializationKt.toExportRequest
      0  |     1670  |        0  | com/infendro/otlp/OtlpExporter$export$job$1.invokeSuspend
   1400  |     1580  |      140  | io/opentelemetry/proto/trace/v1/SpanDSL.build
    420  |     1120  |       42  | io/opentelemetry/proto/trace/v1/Span.serialize
    160  |     1010  |       16  | ...imortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
    310  |      750  |       31  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
      0  |      640  |        0  | ...el/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessageArray
     10  |      610  |        1  | ...tlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
     10  |      600  |        1  | io/opentelemetry/proto/trace/v1/ScopeSpans.serialize
      0  |      530  |        0  | kotlinx/coroutines/DispatchedTaskKt.resume
      0  |      470  |        0  | ...timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeString
    170  |      470  |       17  | io/opentelemetry/proto/trace/v1/Span.<init>
      0  |      450  |        0  | ...tel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeStringNoTag
    220  |      430  |       22  | okio/Utf8.size$default
    250  |      430  |       25  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
      0  |      430  |        0  | ...llections/immutable/implementations/immutableList/TrieIterator.next
      0  |      430  |        0  | ...mutable/implementations/immutableList/PersistentVectorIterator.next
     20  |      420  |        2  | MainKt.fibonacci
     10  |      410  |        1  | MainKt._startSpan
     90  |      330  |        9  | kotlinx/io/Utf8Kt.writeString$default
      0  |      330  |        0  | kotlinx/coroutines/DispatchedTaskKt.resumeUnconfined
      0  |      330  |        0  | io/github/timortel/kmpgrpc/core/io/DataSize.computeStringSize
    210  |      310  |       21  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
      0  |      310  |        0  | io/github/timortel/kmpgrpc/core/io/DataSize.computeStringSizeNoTag
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/ matched 6 samples
Aggregate: self=40 ms (on top), total=60 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           10  |             30  | io/grpc/internal/MessageFramer.writeUncompressed
           10  |             10  | io/opentelemetry/proto/trace/v1/Span.<init>
           10  |             10  | io/opentelemetry/proto/resource/v1/Resource.serialize
           10  |             10  | java/time/Clock$SystemClock.instant

=== 5 sample chains (root <- ... <- match) ===
[match: io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize]
  io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
      io/opentelemetry/proto/trace/v1/Span.<init>
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize

[match: io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeUnknownFields]
  io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
    io/opentelemetry/proto/trace/v1/ResourceSpans.serialize
      io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
        io/opentelemetry/proto/resource/v1/Resource.serialize
          io/github/timortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeUnknownFields

[match: io/grpc/internal/MessageFramer.writeKnownLengthUncompressed]
  io/grpc/internal/ForwardingClientStream.writeMessage
    io/grpc/internal/AbstractStream.writeMessage
      io/grpc/internal/MessageFramer.writePayload
        io/grpc/internal/MessageFramer.writeUncompressed
          io/grpc/internal/MessageFramer.writeKnownLengthUncompressed

[match: java/time/Clock.currentInstant]
  MainKt._endSpan
    kotlinx/datetime/Clock$System.now
      kotlinx/datetime/Instant$Companion.now
        java/time/Clock$SystemClock.instant
          java/time/Clock.currentInstant
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 555 samples
Aggregate: self=40 ms (on top), total=5550 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           10  |           5250  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
            0  |            210  | (root)
            0  |             50  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.removeSpanDataFromBatch
           30  |             30  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
            0  |             10  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.iterator

=== 5 sample chains (root <- ... <- match) ===
[match: kotlin/collections/AbstractList.indexOf]
  kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.recyclableRemoveAll
    kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
      kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
        kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
          kotlin/collections/AbstractList.indexOf

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
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 167 samples
Aggregate: self=960 ms (on top), total=1670 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
          310  |            330  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
           20  |            310  | io/opentelemetry/proto/trace/v1/Span.<init>
          210  |            290  | MainKt._startSpan
          200  |            270  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
            0  |            220  | (root)

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

[match: io/opentelemetry/proto/trace/v1/Span.<init>]
  com/infendro/otlp/SerializationKt.toProto
    io/opentelemetry/proto/trace/v1/SpanDSL.build
      io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
        io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
          io/opentelemetry/proto/trace/v1/Span.<init>
```

---

## How to view interactively

Open ``otel-proto.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

