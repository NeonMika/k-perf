# Profile -- otel (JSON/HTTP) (jvm)

**Variant:** `otel`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-05-05 22:26:31  
**Profile file last captured:** 2026-05-05 22:26:28  
**Profile file:** [otel.jfr](otel.jfr)  
**Wall time (capture run):** 3802 ms (incl. profiler overhead)  
**Workload-reported time:** 2483 ms  

---

## Top 30 frames

```
Profile: otel.jfr
Wall (samples*10ms): 3.4 s, 342 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
   1090  |     1100  |      109  | kotlinx/serialization/json/internal/JsonToStringWriter.writeQuoted
    760  |      870  |       76  | sun/nio/cs/UTF_8$Encoder.encodeBufferLoop
    240  |      310  |       24  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.trySteal
    130  |      130  |       13  | kotlin/jvm/internal/Intrinsics.areEqual
     70  |       70  |        7  | java/nio/CharBuffer.position
     70  |      160  |        7  | sun/util/locale/BaseLocale.getInstance
     60  |      190  |        6  | kotlinx/serialization/json/internal/StreamingJsonEncoder.encodeElement
     50  |       90  |        5  | java/util/concurrent/ConcurrentHashMap.get
     50  |       50  |        5  | java/lang/StringUTF16.compress
     50  |       50  |        5  | ...AtomicIntegerFieldUpdater$AtomicIntegerFieldUpdaterImpl.accessCheck
     40  |       40  |        4  | kotlin/jvm/internal/Intrinsics.throwParameterIsNullNPE
     40  |       40  |        4  | sun/util/locale/LocaleUtils.toLowerString
     30  |       30  |        3  | java/lang/String.charAt
     20  |       30  |        2  | kotlinx/serialization/json/internal/PolymorphicKt.classDiscriminator
     20  |       30  |        2  | java/nio/HeapByteBuffer.put
     20  |       20  |        2  | .../serialization/json/internal/JsonToStringWriter.ensureTotalCapacity
     20  |       20  |        2  | java/lang/Long.getChars
     20  |       40  |        2  | java/lang/String.valueOf
     20  |       20  |        2  | java/util/ImmutableCollections$SetN.probe
     20  |       20  |        2  | sun/util/locale/StringTokenIterator.nextDelimiter
     20  |       30  |        2  | sun/util/locale/BaseLocale$Key.equals
     20  |       20  |        2  | java/util/HashMap.clear
     10  |       10  |        1  | ...ntelemetry/kotlin/api/internal/OtelEncodingUtils.buildValidHexArray
     10  |       10  |        1  | com/infendro/otlp/Value.<init>
     10  |       10  |        1  | kotlin/collections/ArraysKt___ArraysKt.toList
     10  |       10  |        1  | io/opentelemetry/kotlin/sdk/trace/SpanWrapper.getParentSpanContext
     10  |       10  |        1  | kotlin/collections/EmptyMap.size
     10  |       10  |        1  | java/util/Arrays$ArrayList.iterator
     10  |       10  |        1  | java/nio/Buffer.nextPutIndex
     10  |       10  |        1  | ...til/concurrent/atomic/AtomicLongFieldUpdater$CASUpdater.accessCheck

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
   1090  |     1100  |      109  | kotlinx/serialization/json/internal/JsonToStringWriter.writeQuoted
      0  |     1100  |        0  | kotlinx/serialization/json/internal/Composer.printQuoted
      0  |     1100  |        0  | kotlinx/serialization/json/internal/StreamingJsonEncoder.encodeString
      0  |     1040  |        0  | kotlinx/serialization/encoding/AbstractEncoder.encodeStringElement
    760  |      870  |       76  | sun/nio/cs/UTF_8$Encoder.encodeBufferLoop
      0  |      870  |        0  | sun/nio/cs/UTF_8$Encoder.encodeLoop
      0  |      870  |        0  | java/nio/charset/CharsetEncoder.encode
      0  |      760  |        0  | kotlin/text/StringsKt__StringsJVMKt.encodeToByteArray
      0  |      700  |        0  | com/infendro/otlp/Span.write$Self$otlp_exporter
    240  |      310  |       24  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.trySteal
      0  |      280  |        0  | java/util/Locale.forLanguageTag
      0  |      280  |        0  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.runWorker
      0  |      270  |        0  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.findTask
      0  |      260  |        0  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.run
     10  |      230  |        1  | sun/util/locale/provider/LocaleProviderAdapter.lambda$toLocaleArray$0
     60  |      190  |        6  | kotlinx/serialization/json/internal/StreamingJsonEncoder.encodeElement
      0  |      180  |        0  | ...til.locale.provider.LocaleProviderAdapter$$Lambda/0x800000068.apply
      0  |      160  |        0  | ...nx/serialization/encoding/AbstractEncoder.encodeSerializableElement
      0  |      160  |        0  | com/infendro/otlp/Value$StringValue.write$Self$otlp_exporter
     70  |      160  |        7  | sun/util/locale/BaseLocale.getInstance
      0  |      150  |        0  | ...nx/coroutines/scheduling/CoroutineScheduler$Worker.findBlockingTask
      0  |      140  |        0  | sun/util/locale/InternalLocaleBuilder.getBaseLocale
    130  |      130  |       13  | kotlin/jvm/internal/Intrinsics.areEqual
      0  |      130  |        0  | kotlin/collections/AbstractList.indexOf
      0  |      130  |        0  | ...table/implementations/immutableList/AbstractPersistentList.contains
      0  |      130  |        0  | ...ementations/immutableList/AbstractPersistentList$removeAll$1.invoke
      0  |      120  |        0  | kotlinx/coroutines/scheduling/CoroutineScheduler$Worker.findAnyTask
      0  |      100  |        0  | com/infendro/otlp/Scope.write$Self$otlp_exporter
     10  |      100  |        1  | sun/util/locale/LocaleObjectCache.get
     50  |       90  |        5  | java/util/concurrent/ConcurrentHashMap.get
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
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 14 samples
Aggregate: self=0 ms (on top), total=140 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
            0  |            130  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains
            0  |             10  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.removeSpanDataFromBatch

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
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 3 samples
Aggregate: self=10 ms (on top), total=30 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
            0  |             20  | (root)
           10  |             10  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run

=== 5 sample chains (root <- ... <- match) ===
[match: io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch]
  kotlinx/coroutines/DispatchedTaskKt.resume
    kotlin/coroutines/jvm/internal/BaseContinuationImpl.resumeWith
      io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker$run$1.invokeSuspend
        io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.run
          io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.addSpanDataToBatch
```

---

## How to view interactively

Open ``otel.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

