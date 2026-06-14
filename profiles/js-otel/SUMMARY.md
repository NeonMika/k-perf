# Profile -- otel (JSON/HTTP) (js)

**Variant:** `otel`  
**Platform:** js  
**SUMMARY rendered:** 2026-06-13 17:01:07  
**Profile file last captured:** 2026-06-13 17:01:06  
**Profile file:** [otel.cpuprofile](otel.cpuprofile)  
**Wall time (capture run):** 151412 ms (incl. profiler overhead)  
**Workload-reported time:** 150515 ms  

---

## Top 30 frames

```
Profile: otel.cpuprofile
Wall: 150670.3 ms total, 12517 nodes, 93065 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
 40717.1  |   49011.6  |  25762  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7432)
 27849.4  |   27849.4  |  17647  | (garbage collector)                                (:-1)
  7709.3  |    7730.1  |   4905  | readableByteStreamControllerEnqueue                (node:internal/webstreams/readablestream:2872)
  6977.8  |    6977.8  |   4287  | toTypedArray                                       (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2883)
  6407.6  |   13560.2  |   3965  | encodeUtf8                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:6568)
  5664.4  |    5664.4  |   3427  | charCodeAt                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2008)
  5573.6  |    6694.7  |   3240  | recyclableRemoveAll                                (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774)
  2481.1  |    2481.1  |   3968  | (program)                                          (:-1)
  2427.4  |    2452.6  |   1493  | copyOf_0                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2914)
  2381.4  |    4564.6  |   1509  | (anonymous)                                        (node:internal/deps/undici/undici:6851)
  2378.9  |    2380.5  |   1510  | webidl.util.getCopyOfBytesHeldByBufferSource       (node:internal/deps/undici/undici:5110)
  2116.1  |    5361.6  |   1138  | protoOf.x19                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:800)
  1717.3  |    3088.6  |    953  | printQuoted                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:845)
  1662.6  |    5728.6  |    933  | divide                                             (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1836)
  1609.0  |    1631.0  |    918  | add                                                (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1797)
  1537.5  |    1537.5  |    903  | (idle)                                             (:-1)
  1413.1  |    6692.7  |    895  | (anonymous)                                        (node:internal/webstreams/readablestream:1824)
  1299.1  |    4751.7  |    832  | enqueue                                            (node:internal/webstreams/readablestream:1193)
  1239.6  |    2684.4  |    715  | subtract                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1702)
  1220.5  |    8198.3  |    763  | (anonymous)                                        (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7343)
  1104.1  |    7855.2  |    639  | removeAll                                          (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647)
  1022.8  |    1062.2  |    607  | equals                                             (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2171)
   990.9  |     990.9  |    634  | cloneAsUint8Array                                  (node:internal/webstreams/util:102)
   873.6  |     873.6  |    543  | captureStack                                       (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2349)
   820.8  |    3209.3  |    476  | multiply                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1714)
   792.1  |     792.1  |    499  | Segment_init_$Init$                                (.../packages/comparison-otel/kotlin/kotlinx-io-kotlinx-io-core.js:760)
   662.8  |     662.8  |    362  | _init_properties_StringOps_kt__fcy1db              (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:872)
   628.9  |   65973.2  |    341  | protoOf.l1a                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:561)
   620.6  |    1782.6  |    360  | lessThan                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1828)
   580.8  |    2726.7  |    304  | protoOf.m19                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:650)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
   137.6  |  257883.6  |     82  | fibonacci                                          (.../packages/comparison-otel/kotlin/comparison-otel.js:27)
     0.0  |  150670.1  |      0  | (root)                                             (:-1)
    89.8  |  102305.9  |     50  | protoOf.n8                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820)
    15.6  |   90577.5  |      9  | processTimers                                      (node:internal/timers:525)
     1.7  |   89600.5  |      2  | (anonymous)                                        (.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485)
    27.9  |   89598.8  |     17  | protoOf.t14                                        (.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662)
    48.8  |   89556.5  |     30  | protoOf.qq                                         (.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400)
     6.7  |   89396.2  |      4  | listOnTimeout                                      (node:internal/timers:545)
     1.7  |   72346.1  |      1  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006)
   628.9  |   65973.2  |    341  | protoOf.l1a                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:561)
    35.7  |   56824.4  |     22  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:6987)
     1.6  |   56719.9  |      1  | l                                                  (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1474)
     1.6  |   56715.0  |      1  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1432)
     0.0  |   56695.8  |      0  | protoOf.s3s                                        (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7232)
     1.6  |   49013.1  |      1  | toRaw                                              (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7317)
 40717.1  |   49011.6  |  25762  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7432)
   205.2  |   48734.8  |    107  | protoOf.k1a                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-core.js:1528)
    21.2  |   44951.5  |     12  | protoOf.r8                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4875)
    10.4  |   43023.8  |      6  | proceedLoop                                        (.../packages/comparison-otel/kotlin/ktor-ktor-utils.js:1360)
    12.3  |   43013.2  |      7  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-utils.js:1370)
     0.0  |   37123.2  |      0  | (anonymous)                                        (.../packages/comparison-otel/kotlin/comparison-otel.js:0)
     1.6  |   29612.8  |      1  | protoOf.q30                                        (.../packages/comparison-otel/kotlin/ktor-ktor-utils.js:1450)
    22.3  |   28495.4  |     13  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/otlp-exporter.js:138)
 27849.4  |   27849.4  |  17647  | (garbage collector)                                (:-1)
   150.7  |   24735.9  |     83  | protoOf.y16                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-core.js:1912)
   288.5  |   24583.5  |    153  | protoOf.t1b                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-core.js:1896)
     0.0  |   18821.9  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |   18821.9  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     0.0  |   18815.3  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |   18813.5  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 41 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=22.4 ms, total=569.5 ms, hits=14

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         13.1  |            13.1   |       8  | insert  (node:internal/timers:386)
          3.1  |             3.1   |       2  | Event  (node:internal/event_target:114)
          1.7  |             1.7   |       1  | fetching  (node:internal/deps/undici/undici:12870)
          1.6  |           535.6   |       1  | protoOf.f1j  (.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:559)
          1.6  |             1.6   |       1  | onResponseStarted  (node:internal/deps/undici/undici:13639)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 4.9ms]
  (root)@:-1
    processTimers@node:internal/timers:525
      listOnTimeout@node:internal/timers:545
        (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
          protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
            insert@node:internal/timers:386
              getLibuvNow@:-1

[self 3.3ms]
  listOnTimeout@node:internal/timers:545
    (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
      protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
        protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
            protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
              l@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:5108
                protoOf.o8@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:5067
                  delay@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:1548
                    w3cSetTimeout@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8505
                      insert@node:internal/timers:386
                        getLibuvNow@:-1

[self 1.7ms]
  protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
    protoOf.r8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4875
      protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
        protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
          l@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1474
            protoOf.o8@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1432
              protoOf.s3s@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7232
                protoOf.o8@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:6987
                  commonFetch@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7731
                    fetch2@node:internal/deps/undici/undici:12751
                      fetching@node:internal/deps/undici/undici:12870
                        now@:-1

[self 1.7ms]
  tryResumeReceiver@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4218
    dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
      resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
        protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
            protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
              l@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1285
                protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1239
                  delay@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:1548
                    w3cSetTimeout@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8505
                      insert@node:internal/timers:386
                        getLibuvNow@:-1

[self 1.7ms]
  protoOf.p4g@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:6613
    protoOf.o8@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:6546
      tryMakeCompleting@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:2314
        tryMakeCompletingSlowPath@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:2343
          finalizeFinishingState@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:1854
            completeStateFinalization@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:1973
              notifyCompletion@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:2064
                protoOf.ro@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:3202
                  (anon)@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7756
                    abortSignal@node:internal/abort_controller:432
                      Event@node:internal/event_target:114
                        now@:-1
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 121 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=5581.2 ms, total=7773.2 ms, hits=3245

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
       5573.6  |          6694.7   |    3240  | removeAll  (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647)
          4.7  |           987.8   |       3  | (anonymous)  (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:197)
          1.7  |            16.5   |       1  | updateNextExportTime  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1153)
          1.3  |             1.3   |       1  | (anonymous)  (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1786)
          0.0  |             3.1   |       0  | protoOf.o8  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1181)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 1026.5ms]
  updateCellSend@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4103
    tryResumeReceiver@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4218
      dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
        resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
          resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
            protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
              protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1341
                exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1156
                  protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1545
                    protoOf.v1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 919.6ms]
  updateCellSend@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4103
    tryResumeReceiver@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4218
      dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
        resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
          resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
            protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
              protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1341
                exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1156
                  protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1545
                    protoOf.v1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 734.9ms]
  updateCellSend@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4103
    tryResumeReceiver@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4218
      dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
        resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
          resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
            protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
              protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1341
                exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1156
                  protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1545
                    protoOf.v1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 603.8ms]
  updateCellSend@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4103
    tryResumeReceiver@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4218
      dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
        resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
          resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
            protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
              protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1341
                exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1156
                  protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1545
                    protoOf.v1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 446.0ms]
  updateCellSend@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4103
    tryResumeReceiver@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4218
      dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
        resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
          resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
            protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
              protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1341
                exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1156
                  protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1545
                    protoOf.v1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 1348 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=5011.3 ms, total=14080.8 ms, hits=2868

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
       1268.8  |          2412.6   |     732  | compare  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1626)
        852.3  |          2599.4   |     479  | toStringImpl  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647)
        783.5  |          1945.5   |     453  | multiply  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1714)
        641.7  |          2103.0   |     362  | divide  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1836)
        512.6  |          1915.9   |     291  | modulo  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1928)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 442.6ms]
  listOnTimeout@node:internal/timers:545
    (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
      protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
        protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
            protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
              l@.../packages/comparison-otel/kotlin/otlp-exporter.js:276
                protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:138
                  serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:314
                    protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1366
                      toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647
                        divide@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1836

[self 133.9ms]
  listOnTimeout@node:internal/timers:545
    (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
      protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
        protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
            protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
              l@.../packages/comparison-otel/kotlin/otlp-exporter.js:276
                protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:138
                  serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:314
                    protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1366
                      toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647
                        multiply@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1714

[self 133.7ms]
  (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
    protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
      protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
        protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
          protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
            l@.../packages/comparison-otel/kotlin/otlp-exporter.js:276
              protoOf.s22@.../packages/comparison-otel/kotlin/otlp-exporter.js:129
                protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:138
                  serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:314
                    protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1366
                      toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647
                        divide@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1836

[self 111.0ms]
  (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
    protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
      protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
        protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
          protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
            l@.../packages/comparison-otel/kotlin/otlp-exporter.js:276
              protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:138
                serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:314
                  protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1366
                    toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647
                      divide@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1836
                        multiply@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1714

[self 90.0ms]
  protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
    protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
      protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
        l@.../packages/comparison-otel/kotlin/otlp-exporter.js:276
          protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:138
            serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:314
              protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1366
                toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647
                  divide@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1836
                    greaterThanOrEqual@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1924
                      compare@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1626
                        subtract@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1702
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 15 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=68.2 ms, total=79.5 ms, hits=40

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         63.5  |            63.5   |      37  | protoOf.v27  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:514)
          3.0  |             3.0   |       2  | BatchSpanProcessor$Worker$run$slambda_0  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1283)
          1.8  |             8.4   |       1  | protoOf.y1k  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1759)
          0.0  |             1.7   |       0  | protoOf.q2f  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1685)
          0.0  |             3.0   |       0  | protoOf.o8  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1341)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 15.7ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:145
                    protoOf.m1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:652
                      protoOf.v27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:514
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:541

[self 14.3ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:145
                    protoOf.m1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:652
                      protoOf.v27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:514
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:541

[self 12.9ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:145
                    protoOf.m1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:652
                      protoOf.v27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:514
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:541

[self 4.9ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:145
                    protoOf.m1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:652
                      protoOf.v27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:514
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:541

[self 4.8ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:27
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:145
                    protoOf.m1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:652
                      protoOf.v27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:514
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:541
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``otel.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

