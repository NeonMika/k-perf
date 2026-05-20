# Profile -- otel-proto (Protobuf/gRPC) (jvm)

**Variant:** `otel-proto`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-05-20 20:42:22  
**Profile file last captured:** 2026-05-20 20:42:20  
**Profile file:** [otel-proto.jfr](otel-proto.jfr)  
**Wall time (capture run):** 2407 ms (incl. profiler overhead)  
**Workload-reported time:** 734 ms  

---

## Top 30 frames

```
Profile: otel-proto.jfr
Wall (samples*10ms): 0.1 s, 5 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
     10  |       10  |        1  | jdk/internal/loader/BuiltinClassLoader.isSealed
     10  |       10  |        1  | java/lang/String.equals
     10  |       10  |        1  | com/infendro/otlp/SerializationKt.toProto
     10  |       10  |        1  | kotlin/jvm/internal/Intrinsics.areEqual
     10  |       10  |        1  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
      0  |       20  |        0  | kotlin/collections/AbstractList.indexOf
      0  |       20  |        0  | ...table/implementations/immutableList/AbstractPersistentList.contains
     10  |       10  |        1  | jdk/internal/loader/BuiltinClassLoader.isSealed
      0  |       10  |        0  | jdk/internal/loader/BuiltinClassLoader.getAndVerifyPackage
      0  |       10  |        0  | jdk/internal/loader/BuiltinClassLoader.defineOrCheckPackage
      0  |       10  |        0  | jdk/internal/loader/ClassLoaders$AppClassLoader.defineOrCheckPackage
      0  |       10  |        0  | jdk/internal/loader/BuiltinClassLoader.defineClass
     10  |       10  |        1  | java/lang/String.equals
      0  |       10  |        0  | java/net/URL.lowerCaseProtocol
      0  |       10  |        0  | java/net/URL.<init>
     10  |       10  |        1  | com/infendro/otlp/SerializationKt.toProto
      0  |       10  |        0  | com/infendro/otlp/SerializationKt.toExportRequest
      0  |       10  |        0  | com/infendro/otlp/OtlpExporter.export
      0  |       10  |        0  | ...otlin/sdk/trace/export/BatchSpanProcessor$Worker.exportCurrentBatch
      0  |       10  |        0  | ...entelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.flush
     10  |       10  |        1  | kotlin/jvm/internal/Intrinsics.areEqual
      0  |       10  |        0  | ...ementations/immutableList/AbstractPersistentList$removeAll$1.invoke
     10  |       10  |        1  | ...mutable/implementations/immutableList/TrieIterator.fillPathIfNeeded
      0  |       10  |        0  | ...llections/immutable/implementations/immutableList/TrieIterator.next
      0  |       10  |        0  | ...mutable/implementations/immutableList/PersistentVectorIterator.next
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
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 2 samples
Aggregate: self=0 ms (on top), total=20 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
            0  |             20  | kotlinx/collections/immutable/implementations/immutableList/AbstractPersistentList.contains

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
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 1 samples
Aggregate: self=0 ms (on top), total=10 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller
            0  |             10  | io/opentelemetry/kotlin/sdk/trace/export/BatchSpanProcessor$Worker.flush

=== 5 sample chains (root <- ... <- match) ===
```

---

## How to view interactively

Open ``otel-proto.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

