# Profile -- k-perf (jvm)

**Variant:** `k-perf`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-05-20 20:40:33  
**Profile file last captured:** 2026-05-20 20:40:32  
**Profile file:** [k-perf.jfr](k-perf.jfr)  
**Wall time (capture run):** 856 ms (incl. profiler overhead)  
**Workload-reported time:** unknown (no marker line in stdout)  

---

## Top 30 frames

```
Profile: k-perf.jfr
Wall (samples*10ms): 0.0 s, 4 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
     20  |       30  |        2  | kotlinx/io/Utf8Kt.writeString
     10  |       10  |        1  | kotlinx/io/RealSink.hintEmit
     10  |       10  |        1  | MainKt.main

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
     20  |       30  |        2  | kotlinx/io/Utf8Kt.writeString
      0  |       30  |        0  | kotlinx/io/Utf8Kt.writeString$default
      0  |       30  |        0  | MainKt._enter_method
      0  |       30  |        0  | MainKt.fibonacci
     10  |       10  |        1  | kotlinx/io/RealSink.hintEmit
     10  |       10  |        1  | MainKt.main
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
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 0 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

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
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 0 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

---

## How to view interactively

Open ``k-perf.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

