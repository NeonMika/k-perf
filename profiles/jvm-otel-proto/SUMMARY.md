# Profile -- otel-proto (Protobuf/gRPC) (jvm)

**Variant:** `otel-proto`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-05-05 22:27:20  
**Profile file last captured:** 2026-05-05 22:27:17  
**Profile file:** [otel-proto.jfr](otel-proto.jfr)  
**Wall time (capture run):** 3418 ms (incl. profiler overhead)  
**Workload-reported time:** 1729 ms  

---

## Top 30 frames

```
Profile: otel-proto.jfr
Wall (samples*10ms): 1.0 s, 101 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
    250  |      260  |       25  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
     70  |      100  |        7  | io/opentelemetry/proto/trace/v1/Span.<init>
     50  |       70  |        5  | kotlin/jvm/internal/Intrinsics.areEqual
     40  |       40  |        4  | io/opentelemetry/kotlin/api/internal/OtelEncodingUtils.bytesFromBase16
     40  |       40  |        4  | java/lang/String.charAt
     20  |       50  |        2  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch
     20  |      120  |        2  | com/infendro/otlp/SerializationKt.toProto
     20  |       50  |        2  | ...pgrpc/core/io/internal/CodedOutputStreamImpl.writeVarUInt32-WZ4Q5Ns
     20  |      140  |        2  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
     20  |       40  |        2  | ...el/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessageArray
     10  |       10  |        1  | ...ons/immutable/implementations/immutableList/PersistentVector.<init>
     10  |       10  |        1  | ...routines/channels/ChannelSegment.getElement$kotlinx_coroutines_core
     10  |       10  |        1  | ...s/immutable/implementations/immutableList/PersistentVector.rootSize
     10  |       10  |        1  | java/util/ArrayList.iterator
     10  |       20  |        1  | kotlinx/io/SinksKt.writeUByte-EK-6454
     10  |       30  |        1  | ...pgrpc/core/io/internal/CodedOutputStreamImpl.writeVarUInt64-VKZWuLQ
     10  |       10  |        1  | kotlinx/io/Segment.writeLong$kotlinx_io_core
     10  |       10  |        1  | ...entations/immutableList/PersistentVectorBuilder.recyclableRemoveAll
     10  |       10  |        1  | io/opentelemetry/proto/trace/v1/ScopeSpans.<init>
     10  |       30  |        1  | ...ctions/immutable/implementations/immutableList/PersistentVector.add
     10  |       10  |        1  | java/util/zip/ZipFile$Source.getEntryPos
     10  |       10  |        1  | jdk/internal/util/ArraysSupport.hashCode
     10  |       10  |        1  | java/util/Collections.emptyIterator
     10  |       20  |        1  | kotlinx/io/Buffer.recycleHead$kotlinx_io_core
     10  |       10  |        1  | java/lang/String.equals
     10  |       10  |        1  | okio/OutputStreamSink.write
     10  |       10  |        1  | io/github/timortel/kmpgrpc/core/io/DataSize.computeTagSize
     10  |       30  |        1  | kotlinx/io/InputStreamSource.readAtMostTo
     10  |       80  |        1  | com/infendro/otlp/SerializationKt.toExportRequest
     10  |       10  |        1  | java/lang/invoke/VarHandleGuards.guard_LIL_L

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
      0  |      300  |        0  | kotlin/collections/AbstractList.indexOf
      0  |      290  |        0  | ...table/implementations/immutableList/AbstractPersistentList.contains
    250  |      260  |       25  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
      0  |      260  |        0  | ...llections/immutable/implementations/immutableList/TrieIterator.next
      0  |      260  |        0  | ...mutable/implementations/immutableList/PersistentVectorIterator.next
     20  |      140  |        2  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
     20  |      120  |        2  | com/infendro/otlp/SerializationKt.toProto
      0  |      100  |        0  | ...tlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
     70  |      100  |        7  | io/opentelemetry/proto/trace/v1/Span.<init>
      0  |      100  |        0  | io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
      0  |       90  |        0  | kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
     10  |       80  |        1  | com/infendro/otlp/SerializationKt.toExportRequest
      0  |       80  |        0  | ...lemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
     50  |       70  |        5  | kotlin/jvm/internal/Intrinsics.areEqual
      0  |       70  |        0  | io/opentelemetry/proto/trace/v1/Span.serialize
      0  |       70  |        0  | io/opentelemetry/proto/trace/v1/SpanDSL.build
     20  |       50  |        2  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch
      0  |       50  |        0  | kotlinx/coroutines/DispatchedTaskKt.resume
      0  |       50  |        0  | ...imortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessage
     20  |       50  |        2  | ...pgrpc/core/io/internal/CodedOutputStreamImpl.writeVarUInt32-WZ4Q5Ns
      0  |       40  |        0  | ...ementations/immutableList/AbstractPersistentList$removeAll$1.invoke
      0  |       40  |        0  | com/infendro/otlp/OtlpExporter.export
     20  |       40  |        2  | ...el/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeMessageArray
      0  |       40  |        0  | ...mortel/kmpgrpc/core/io/internal/CodedOutputStreamImpl.writeVarInt32
     40  |       40  |        4  | io/opentelemetry/kotlin/api/internal/OtelEncodingUtils.bytesFromBase16
     40  |       40  |        4  | java/lang/String.charAt
     10  |       30  |        1  | ...ctions/immutable/implementations/immutableList/PersistentVector.add
      0  |       30  |        0  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.exportCurrentBatch
      0  |       30  |        0  | kotlinx/coroutines/channels/BufferedChannel.receive$suspendImpl
     10  |       30  |        1  | ...pgrpc/core/io/internal/CodedOutputStreamImpl.writeVarUInt64-VKZWuLQ
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/ matched 2 samples
Aggregate: self=10 ms (on top), total=20 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
            0  |             10  | kotlinx/coroutines/channels/BufferedChannel.receive$suspendImpl
           10  |             10  | io/opentelemetry/proto/trace/v1/Span.<init>

=== 5 sample chains (root <- ... <- match) ===
[match: io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize]
  io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM$default
    io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
      io/opentelemetry/proto/trace/v1/Span.<init>
        io/opentelemetry/proto/trace/v1/Span.<init>
          io/github/timortel/kmpgrpc/core/io/DataSize.computeUnknownFieldsRequiredSize
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 32 samples
Aggregate: self=10 ms (on top), total=320 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
            0  |            290  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
           10  |             10  | kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.removeAll
            0  |             10  | (root)
            0  |             10  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.removeSpanDataFromBatch

=== 5 sample chains (root <- ... <- match) ===
[match: kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.recyclableRemoveAll]
  kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.removeAll
    kotlinx/collections/immutable/implementations/immutableList/PersistentVector.removeAll
      kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.removeAllWithPredicate
        kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.removeAll
          kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.recyclableRemoveAll
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
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 26 samples
Aggregate: self=110 ms (on top), total=260 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           20  |             80  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
           60  |             60  | io/opentelemetry/proto/trace/v1/Span$Companion.createPartial-7GxNOqM
           10  |             40  | io/opentelemetry/proto/trace/v1/Span.<init>
           20  |             40  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
            0  |             30  | (root)

=== 5 sample chains (root <- ... <- match) ===
[match: io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch]
  kotlinx/coroutines/DispatchedTaskKt.resume
    kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
      io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
        io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
          io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch

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
```

---

## How to view interactively

Open ``otel-proto.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

