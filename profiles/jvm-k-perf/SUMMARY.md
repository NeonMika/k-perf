# Profile -- k-perf (jvm)

**Variant:** `k-perf`  
**Platform:** jvm  
**SUMMARY rendered:** 2026-05-05 22:26:12  
**Profile file last captured:** 2026-05-05 22:26:11  
**Profile file:** [k-perf.jfr](k-perf.jfr)  
**Wall time (capture run):** 1914 ms (incl. profiler overhead)  
**Workload-reported time:** 1068 ms  

---

## Top 30 frames

```
Profile: k-perf.jfr
No jdk.ExecutionSample events in recording.
Likely causes: workload too short for JFR sampling, or recording started without settings=profile.
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/ matched 0 samples (recording has no jdk.ExecutionSample events).
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/ matched 0 samples (recording has no jdk.ExecutionSample events).
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Pattern /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/ matched 0 samples (recording has no jdk.ExecutionSample events).
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/ matched 0 samples (recording has no jdk.ExecutionSample events).
```

---

## How to view interactively

Open ``k-perf.jfr`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.

