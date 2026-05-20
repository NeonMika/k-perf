# Profile -- otel (JSON/HTTP) (jvm)

**Variant:** `otel`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-05-20 20:40:57  
**Profile file last captured:** 2026-05-20 20:40:54  
**Profile file:** [otel.jfr](otel.jfr)  
**Wall time (capture run):** 8697 ms (incl. profiler overhead)  
**Workload-reported time:** 7511 ms  

---

## Top 30 frames

```
Profile: otel.jfr
Wall (samples*10ms): 6.1 s, 611 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
   1270  |     1500  |      127  | sun/nio/cs/UTF_8$Encoder.encodeBufferLoop
   1230  |     1280  |      123  | kotlinx/serialization/json/internal/JsonToStringWriter.writeQuoted
    540  |      540  |       54  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
    370  |      530  |       37  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.trySteal
    160  |      160  |       16  | java/nio/CharBuffer.position
    110  |      230  |       11  | com/infendro/otlp/SerializationKt.serialize
     70  |       70  |        7  | java/lang/StringUTF16.compress
     70  |      100  |        7  | kotlinx/coroutines/scheduling/WorkQueue.pollBuffer
     40  |      320  |        4  | kotlinx/serialization/json/internal/StreamingJsonEncoder.encodeElement
     40  |       40  |        4  | java/lang/String.charAt
     40  |       40  |        4  | java/lang/Long.getChars
     40  |       40  |        4  | .../serialization/json/internal/JsonToStringWriter.ensureTotalCapacity
     40  |      400  |        4  | ...nx/serialization/encoding/AbstractEncoder.encodeSerializableElement
     40  |       40  |        4  | java/lang/Class.cast
     30  |       30  |        3  | java/lang/String.getChars
     30  |      140  |        3  | kotlinx/serialization/internal/CollectionLikeSerializer.serialize
     30  |      800  |        3  | com/infendro/otlp/Span.write$Self$otlp_exporter
     30  |       30  |        3  | kotlin/jvm/internal/Intrinsics.throwParameterIsNullNPE
     30  |       30  |        3  | ...icReferenceFieldUpdater$AtomicReferenceFieldUpdaterImpl.accessCheck
     30  |       40  |        3  | java/nio/StringCharBuffer.get
     30  |       80  |        3  | java/lang/String.valueOf
     30  |       30  |        3  | kotlinx/serialization/SealedClassSerializer.getDescriptor
     30  |       80  |        3  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
     30  |       40  |        3  | sun/util/locale/BaseLocale$Key.<init>
     30  |       30  |        3  | sun/util/locale/InternalLocaleBuilder.setLanguageTag
     30  |      140  |        3  | sun/util/locale/BaseLocale.getInstance
     30  |      120  |        3  | java/util/concurrent/ConcurrentHashMap.get
     30  |     1150  |        3  | kotlinx/serialization/encoding/AbstractEncoder.encodeStringElement
     30  |       30  |        3  | sun/nio/ch/Util$BufferCache.offerLast
     30  |       30  |        3  | ...AtomicIntegerFieldUpdater$AtomicIntegerFieldUpdaterImpl.accessCheck

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
     10  |     1510  |        1  | java/nio/charset/CharsetEncoder.encode
   1270  |     1500  |      127  | sun/nio/cs/UTF_8$Encoder.encodeBufferLoop
      0  |     1500  |        0  | sun/nio/cs/UTF_8$Encoder.encodeLoop
   1230  |     1280  |      123  | kotlinx/serialization/json/internal/JsonToStringWriter.writeQuoted
      0  |     1280  |        0  | kotlinx/serialization/json/internal/Composer.printQuoted
      0  |     1280  |        0  | kotlinx/serialization/json/internal/StreamingJsonEncoder.encodeString
      0  |     1280  |        0  | kotlin/text/StringsKt__StringsJVMKt.encodeToByteArray
     30  |     1150  |        3  | kotlinx/serialization/encoding/AbstractEncoder.encodeStringElement
     30  |      800  |        3  | com/infendro/otlp/Span.write$Self$otlp_exporter
     10  |      550  |        1  | ...mutable/implementations/immutableList/PersistentVectorIterator.next
      0  |      540  |        0  | kotlin/collections/AbstractList.indexOf
      0  |      540  |        0  | ...table/implementations/immutableList/AbstractPersistentList.contains
    540  |      540  |       54  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
      0  |      540  |        0  | ...llections/immutable/implementations/immutableList/TrieIterator.next
    370  |      530  |       37  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.trySteal
      0  |      480  |        0  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.findTask
      0  |      460  |        0  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.findAnyTask
     30  |      420  |        3  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.runWorker
      0  |      410  |        0  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.run
     40  |      400  |        4  | ...nx/serialization/encoding/AbstractEncoder.encodeSerializableElement
     20  |      360  |        2  | ...lization/json/internal/StreamingJsonEncoder.encodeSerializableValue
     40  |      320  |        4  | kotlinx/serialization/json/internal/StreamingJsonEncoder.encodeElement
      0  |      300  |        0  | kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
      0  |      240  |        0  | com/infendro/otlp/OtlpExporter$export$job$1.invokeSuspend
    110  |      230  |       11  | com/infendro/otlp/SerializationKt.serialize
      0  |      200  |        0  | kotlinx/coroutines/DispatchedTask.run
     20  |      200  |        2  | java/util/Locale.forLanguageTag
      0  |      170  |        0  | sun/util/locale/provider/LocaleProviderAdapter.lambda$toLocaleArray$0
    160  |      160  |       16  | java/nio/CharBuffer.position
     30  |      140  |        3  | kotlinx/serialization/internal/CollectionLikeSerializer.serialize
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
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 55 samples
Aggregate: self=0 ms (on top), total=550 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
            0  |            540  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
            0  |             10  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.iterator

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
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 9 samples
Aggregate: self=40 ms (on top), total=90 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
           30  |             50  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
           10  |             30  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
            0  |             10  | MainKt._startSpan

=== 5 sample chains (root <- ... <- match) ===
[match: io/opentelemetry/kotlin/sdk/trace/RecordEventsReadableSpan.toSpanData]
  kotlinx/coroutines/DispatchedTaskKt.resume
    kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
      io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
        io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
          io/opentelemetry/kotlin/sdk/trace/RecordEventsReadableSpan.toSpanData

[match: io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run]
  kotlinx/coroutines/DispatchedTaskKt.resumeUnconfined
    kotlinx/coroutines/DispatchedTaskKt.resume
      kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
        io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
          io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run

[match: io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run]
  kotlinx/coroutines/DispatchedTaskKt.resumeUnconfined
    kotlinx/coroutines/DispatchedTaskKt.resume
      kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
        io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
          io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run

[match: io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run]
  kotlinx/coroutines/DispatchedTaskKt.resumeUnconfined
    kotlinx/coroutines/DispatchedTaskKt.resume
      kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
        io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
          io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
```

---

## How to view interactively

Open ``otel.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

