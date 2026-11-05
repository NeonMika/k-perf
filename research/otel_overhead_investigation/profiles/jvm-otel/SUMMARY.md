# Profile -- otel (JSON/HTTP) (jvm)

**Variant:** `otel`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-06-13 15:53:59  
**Profile file last captured:** 2026-06-13 15:53:52  
**Profile file:** [otel.jfr](otel.jfr)  
**Wall time (capture run):** 58402 ms (incl. profiler overhead)  
**Workload-reported time:** 56205 ms  

---

## Top 30 frames

```
Profile: otel.jfr
Wall (samples*10ms): 35.5 s, 3550 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
   8980  |    10240  |      898  | sun/nio/cs/UTF_8$Encoder.encodeBufferLoop
   5570  |     5570  |      557  | kotlin/jvm/internal/Intrinsics.areEqual
   5060  |     5200  |      506  | kotlinx/serialization/json/internal/JsonToStringWriter.writeQuoted
   1260  |     1260  |      126  | java/nio/CharBuffer.position
   1130  |     3850  |      113  | kotlinx/serialization/json/internal/StreamingJsonEncoder.encodeElement
   1020  |     4980  |      102  | kotlinx/serialization/encoding/AbstractEncoder.encodeStringElement
    710  |      710  |       71  | kotlinx/coroutines/scheduling/WorkQueue.tryStealLastScheduled
    700  |      700  |       70  | java/lang/StringUTF16.compress
    580  |     1140  |       58  | java/lang/String.valueOf
    560  |      560  |       56  | java/lang/Long.getChars
    520  |     1240  |       52  | kotlinx/coroutines/scheduling/WorkQueue.trySteal
    510  |      830  |       51  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
    480  |     3750  |       48  | ...nx/serialization/encoding/AbstractEncoder.encodeSerializableElement
    460  |      460  |       46  | kotlinx/serialization/internal/CollectionSerializer.collectionSize
    380  |      440  |       38  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan
    380  |      380  |       38  | .../serialization/json/internal/JsonToStringWriter.ensureTotalCapacity
    300  |      460  |       30  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
    280  |     3530  |       28  | ...lization/json/internal/StreamingJsonEncoder.encodeSerializableValue
    270  |      290  |       27  | kotlin/jvm/internal/Intrinsics.throwParameterIsNullNPE
    260  |      960  |       26  | kotlinx/serialization/json/internal/JsonToStringWriter.toString
    230  |      230  |       23  | ...tions/immutable/implementations/immutableList/TrieIterator.fillPath
    220  |      320  |       22  | kotlinx/serialization/internal/CollectionSerializer.collectionIterator
    190  |      260  |       19  | java/util/HashMap.getNode
    180  |      420  |       18  | com/infendro/otlp/ResourceSpans$$serializer.serialize
    160  |      160  |       16  | kotlinx/coroutines/channels/BufferedChannel.expandBuffer
    150  |      150  |       15  | java/util/ArrayList.grow
    140  |      160  |       14  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch
    130  |     1550  |       13  | kotlinx/serialization/internal/CollectionLikeSerializer.serialize
    130  |      330  |       13  | kotlinx/serialization/encoding/AbstractEncoder.encodeIntElement
    120  |      830  |       12  | ...inx/serialization/json/internal/StreamingJsonEncoder.beginStructure

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
     20  |    10260  |        2  | sun/nio/cs/UTF_8$Encoder.encodeLoop
      0  |    10260  |        0  | java/nio/charset/CharsetEncoder.encode
   8980  |    10240  |      898  | sun/nio/cs/UTF_8$Encoder.encodeBufferLoop
      0  |     9000  |        0  | kotlin/text/StringsKt__StringsJVMKt.encodeToByteArray
     10  |     5870  |        1  | kotlin/collections/AbstractList.indexOf
     10  |     5740  |        1  | ...table/implementations/immutableList/AbstractPersistentList.contains
      0  |     5590  |        0  | ...ementations/immutableList/AbstractPersistentList$removeAll$1.invoke
   5570  |     5570  |      557  | kotlin/jvm/internal/Intrinsics.areEqual
     10  |     5220  |        1  | kotlinx/serialization/json/internal/Composer.printQuoted
      0  |     5220  |        0  | kotlinx/serialization/json/internal/StreamingJsonEncoder.encodeString
   5060  |     5200  |      506  | kotlinx/serialization/json/internal/JsonToStringWriter.writeQuoted
   1020  |     4980  |      102  | kotlinx/serialization/encoding/AbstractEncoder.encodeStringElement
   1130  |     3850  |      113  | kotlinx/serialization/json/internal/StreamingJsonEncoder.encodeElement
    480  |     3750  |       48  | ...nx/serialization/encoding/AbstractEncoder.encodeSerializableElement
    280  |     3530  |       28  | ...lization/json/internal/StreamingJsonEncoder.encodeSerializableValue
     10  |     3080  |        1  | com/infendro/otlp/Span.write$Self$otlp_exporter
     30  |     2010  |        3  | kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
    110  |     1780  |       11  | com/infendro/otlp/Span$$serializer.serialize
      0  |     1680  |        0  | com/infendro/otlp/OtlpExporter$export$job$1.invokeSuspend
    110  |     1610  |       11  | com/infendro/otlp/SerializationKt.serialize
    130  |     1550  |       13  | kotlinx/serialization/internal/CollectionLikeSerializer.serialize
     30  |     1460  |        3  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.findTask
     50  |     1420  |        5  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.findAnyTask
     90  |     1330  |        9  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.trySteal
   1260  |     1260  |      126  | java/nio/CharBuffer.position
    520  |     1240  |       52  | kotlinx/coroutines/scheduling/WorkQueue.trySteal
    580  |     1140  |       58  | java/lang/String.valueOf
    120  |      980  |       12  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.runWorker
      0  |      980  |        0  | kotlinx/serialization/json/Json.encodeToString
    260  |      960  |       26  | kotlinx/serialization/json/internal/JsonToStringWriter.toString
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/ matched 3 samples
Aggregate: self=20 ms (on top), total=30 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           10  |             10  | java/time/Instant.now
           10  |             10  | java/time/Clock$SystemClock.instant
            0  |             10  | (root)

=== 5 sample chains (root <- ... <- match) ===
[match: java/time/Clock.currentInstant]
  jdk/internal/net/http/HttpClientImpl$SelectorManager.run
    jdk/internal/net/http/HttpClientImpl.purgeTimeoutsAndReturnNextDeadline
      java/time/Instant.now
        java/time/Clock.currentInstant

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
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 599 samples
Aggregate: self=30 ms (on top), total=5990 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           10  |           5730  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
            0  |            210  | (root)
            0  |             30  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.removeSpanDataFromBatch
           10  |             10  | kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.removeAll
           10  |             10  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke

=== 5 sample chains (root <- ... <- match) ===
[match: kotlin/collections/AbstractList.indexOf]
  kotlinx/collections/immutable/implementations/immutableList/PersistentVectorBuilder.recyclableRemoveAll
    kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
      kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList$removeAll$1.invoke
        kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
          kotlin/collections/AbstractList.indexOf

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
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 138 samples
Aggregate: self=1150 ms (on top), total=1380 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
          510  |            550  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
          380  |            380  | MainKt._startSpan
          170  |            190  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
            0  |            110  | (root)
           60  |             60  | io/opentelemetry/kotlin/sdk/trace/SdkSpanBuilder.startSpan

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
```

---

## How to view interactively

Open ``otel.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

