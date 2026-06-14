# Profile -- k-perf (jvm)

**Variant:** `k-perf`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-06-13 15:52:01  
**Profile file last captured:** 2026-06-13 15:51:59  
**Profile file:** [k-perf.jfr](k-perf.jfr)  
**Wall time (capture run):** 1453 ms (incl. profiler overhead)  
**Workload-reported time:** unknown (no marker line in stdout)  

---

## Top 30 frames

```
Profile: k-perf.jfr
Wall (samples*10ms): 0.2 s, 20 samples

=== Top 30 by SELF time ===
  self ms | total ms |  samples | function
     70  |       80  |        7  | kotlinx/io/Utf8Kt.writeString
     60  |       60  |        6  | java/lang/Long.getChars
     40  |      120  |        4  | kotlinx/io/Utf8Kt.writeString$default
     10  |       70  |        1  | java/lang/invoke/DirectMethodHandle$Holder.invokeStatic
     10  |      130  |        1  | MainKt.fibonacci
     10  |       10  |        1  | kotlinx/io/RealSink.hintEmit

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |  samples | function
     10  |      130  |        1  | MainKt.fibonacci
     40  |      120  |        4  | kotlinx/io/Utf8Kt.writeString$default
     70  |       80  |        7  | kotlinx/io/Utf8Kt.writeString
     10  |       70  |        1  | java/lang/invoke/DirectMethodHandle$Holder.invokeStatic
      0  |       70  |        0  | MainKt._exit_method
      0  |       60  |        0  | MainKt._enter_method
     60  |       60  |        6  | java/lang/Long.getChars
      0  |       60  |        0  | java/lang/StringConcatHelper.prepend
      0  |       60  |        0  | java.lang.invoke.LambdaForm$MH/0x000001cdc6018400.invoke
      0  |       10  |        0  | java.lang.invoke.LambdaForm$MH/0x000001cdc6019000.invoke
      0  |       10  |        0  | java.lang.invoke.LambdaForm$MH/0x000001cdc6019400.invoke
      0  |       10  |        0  | java/lang/invoke/Invokers$Holder.linkToTargetMethod
     10  |       10  |        1  | kotlinx/io/RealSink.hintEmit
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

