# Profile -- otel-proto-anchored (Protobuf/gRPC + SDK AnchoredClock) (js)

**Variant:** `otel-proto-anchored`  
**Platform:** js  
**SUMMARY rendered:** 2026-06-13 17:09:07  
**Profile file last captured:** 2026-06-13 17:09:06  
**Profile file:** [otel-proto-anchored.cpuprofile](otel-proto-anchored.cpuprofile)  
**Wall time (capture run):** 43950 ms (incl. profiler overhead)  
**Workload-reported time:** 43384 ms  

---

## Top 30 frames

```
Profile: otel-proto-anchored.cpuprofile
Wall: 43518.9 ms total, 10373 nodes, 25523 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
  5418.6  |    6357.2  |   3281  | protoOf.o8                                         (.../packages/comparison-otel-proto-anchored/kotlin/ktor-ktor-client-core.js:8987)
  5063.7  |    6085.7  |   2834  | recyclableRemoveAll                                (.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787)
  4827.2  |    4827.2  |   2609  | (idle)                                             (:-1)
  3471.3  |    3471.3  |   2109  | (garbage collector)                                (:-1)
  2377.3  |    2377.3  |   1356  | arrayCopy                                          (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:3040)
  1029.2  |    1034.2  |    601  | add                                                (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1777)
  1025.2  |    7178.3  |    580  | removeAll                                          (.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
   959.0  |    1113.7  |    542  | equals                                             (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:2190)
   827.2  |    3120.3  |    472  | divide                                             (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1816)
   766.6  |     766.6  |    469  | toTypedArray                                       (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:2756)
   752.5  |    1535.7  |    430  | subtract                                           (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1682)
   670.9  |    1252.0  |    377  | writeVarUInt64                                     (.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:1208)
   571.2  |     592.3  |    336  | protoOf.d4w                                        (.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-api-all.js:1428)
   564.0  |     567.6  |    358  | readableByteStreamControllerEnqueue                (node:internal/webstreams/readablestream:2872)
   464.9  |    1266.3  |    274  | get_isValid                                        (.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-api-all.js:102)
   424.8  |     424.8  |    270  | captureStack                                       (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:2329)
   416.2  |    1698.2  |    240  | multiply                                           (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1694)
   381.7  |     381.7  |    229  | millis                                             (.../node_modules/@js-joda/core/dist/js-joda.js:12810)
   376.1  |     386.0  |    216  | bitwiseAnd                                         (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1912)
   363.2  |     363.2  |   1628  | (program)                                          (:-1)
   345.1  |     345.1  |    202  | charCodeAt                                         (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1988)
   284.9  |     294.8  |    154  | equalsLong                                         (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1666)
   276.9  |     856.9  |    159  | lessThan                                           (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1808)
   269.6  |     269.6  |    149  | utf8Size                                           (.../packages/comparison-otel-proto-anchored/kotlin/okio-parent-okio.js:16)
   251.0  |    2687.8  |    148  | protoOf.w4y                                        (.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:632)
   234.5  |    6130.0  |    134  | protoOf.z4c                                        (.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:3078)
   218.9  |    1638.2  |    123  | compare                                            (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1606)
   213.8  |    2159.0  |    117  | protoOf.o8                                         (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:6068)
   209.4  |     209.4  |    120  | toNumber                                           (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1623)
   205.9  |     724.5  |    130  | (anonymous)                                        (node:internal/webstreams/readablestream:1824)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
   134.9  |  223321.0  |     79  | fibonacci                                          (.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27)
     0.0  |   43518.6  |      0  | (root)                                             (:-1)
     0.0  |   32189.3  |      0  | (anonymous)                                        (.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:0)
    62.3  |   28855.0  |     34  | protoOf.n8                                         (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4692)
    51.3  |   17296.7  |     26  | processTimers                                      (node:internal/timers:525)
    27.2  |   17247.7  |     14  | protoOf.pq                                         (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9144)
    10.2  |   17246.8  |      5  | protoOf.w1g                                        (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:11052)
    18.4  |   17244.2  |     10  | listOnTimeout                                      (node:internal/timers:545)
     0.0  |   16361.7  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |   16361.7  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     0.0  |   16358.5  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |   16358.5  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |   16345.4  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     0.1  |   16282.4  |      1  | protoOf.o8                                         (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4878)
     0.0  |   16143.3  |      0  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |   16141.9  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     0.0  |   16024.8  |      0  | mainWrapper                                        (.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:156)
     0.0  |   16024.8  |      0  | main                                               (.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:83)
     0.0  |   15999.7  |      0  | workload                                           (.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:68)
     2.3  |   12926.3  |      1  | (anonymous)                                        (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:10875)
     0.0  |   12904.0  |      0  | _endSpan                                           (.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:152)
     0.0  |   12904.0  |      0  | protoOf.r4z                                        (.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:560)
     1.7  |   12670.1  |      1  | endInternal                                        (.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:467)
    18.0  |   12668.4  |     11  | protoOf.h5h                                        (.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1616)
   110.0  |   12658.4  |     64  | protoOf.q12                                        (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:6255)
     0.0  |   12525.0  |      0  | protoOf.m58                                        (.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1682)
    27.9  |   11450.5  |     17  | updateCellSend                                     (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4423)
     0.4  |   11393.3  |      0  | tryResumeReceiver                                  (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4538)
     3.2  |   11392.9  |      2  | dispatch                                           (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9202)
     1.6  |   11388.0  |      1  | resumeUnconfined                                   (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9239)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 42 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=19.7 ms, total=435.4 ms, hits=11

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         11.0  |            11.0   |       5  | protoOf.z4c  (.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:3078)
          4.7  |             4.7   |       3  | insert  (node:internal/timers:386)
          1.7  |           395.0   |       1  | protoOf.b4t  (.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:277)
          1.6  |             6.5   |       1  | protoOf.o8  (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:6068)
          0.7  |             3.4   |       1  | protoOf.o8  (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4082)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 9.3ms]
  protoOf.n8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4692
    protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4878
      l@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:8060
        protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:8022
          l@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:2455
            protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:2267
              serialize@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:257
                protoOf.k4a@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:1303
                  protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:1795
                    protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:1982
                      protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:3078
                        writeUnknownFields@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:180

[self 1.7ms]
  fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
    fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
      fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
        fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
          _startSpan@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:145
            protoOf.w4y@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:632
              protoOf.p59@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501
                protoOf.b4t@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:187
                  protoOf.x56@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-common.js:684
                    protoOf.b4t@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:54
                      protoOf.b4t@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:277
                        instant@.../node_modules/@js-joda/core/dist/js-joda.js:12814

[self 1.7ms]
  tryResumeReceiver@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4538
    dispatch@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9202
      resumeUnconfined@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9239
        protoOf.pq@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9144
          protoOf.n8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4692
            protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4878
              l@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1263
                protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1217
                  delay@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:1699
                    w3cSetTimeout@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:10895
                      insert@node:internal/timers:386
                        getLibuvNow@:-1

[self 1.7ms]
  protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4878
    l@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:8060
      protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:8022
        protoOf.n14@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:7181
          l@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:2455
            protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:2267
              serialize@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:257
                protoOf.k4a@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:1303
                  protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:1795
                    protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:1982
                      protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:3078
                        writeUnknownFields@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:180

[self 1.6ms]
  resumeCancellableWith@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:8861
    protoOf.r8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4747
      protoOf.n8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4692
        protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4878
          l@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1197
            protoOf.p19@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1150
              protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1159
                protoOf.w5f@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1636
                  protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1319
                    protoOf.t12@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:6415
                      protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:6068
                        receiveOnNoWaiterSuspend@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4569
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 197 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=5080.1 ms, total=8507.3 ms, hits=2842

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
       5063.7  |          6085.7   |    2834  | removeAll  (.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
          4.5  |           870.6   |       2  | protoOf.c2  (.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216)
          3.3  |           168.7   |       1  | protoOf.o8  (.../packages/comparison-otel-proto-anchored/kotlin/ktor-ktor-client-core.js:2983)
          3.2  |           887.1   |       2  | (anonymous)  (.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203)
          2.1  |            85.6   |       1  | protoOf.d26  (.../packages/comparison-otel-proto-anchored/kotlin/ktor-ktor-utils.js:1451)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 882.8ms]
  updateCellSend@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1319
                exportCurrentBatch@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1134
                  protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1523
                    protoOf.z4m@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 779.4ms]
  updateCellSend@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1319
                exportCurrentBatch@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1134
                  protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1523
                    protoOf.z4m@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 677.1ms]
  updateCellSend@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1319
                exportCurrentBatch@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1134
                  protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1523
                    protoOf.z4m@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 544.1ms]
  updateCellSend@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1319
                exportCurrentBatch@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1134
                  protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1523
                    protoOf.z4m@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 381.8ms]
  updateCellSend@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1319
                exportCurrentBatch@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1134
                  protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1523
                    protoOf.z4m@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-anchored/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 1082 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=2933.7 ms, total=7892.0 ms, hits=1671

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        738.3  |          1367.8   |     417  | compare  (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1606)
        412.0  |          1647.3   |     234  | modulo  (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1908)
        371.7  |           951.7   |     212  | multiply  (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1694)
        364.0  |          1305.2   |     205  | divide  (.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1816)
        156.9  |           525.5   |      89  | protoOf.y10  (.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:6596)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 50.8ms]
  protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:2267
    serialize@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:257
      protoOf.k4a@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:1303
        protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:1795
          protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:1982
            protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:3078
              protoOf.i4a@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:1286
                protoOf.t4c@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                  writeULongLe@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                    writeLongLe@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                      reverseBytes_0@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-io-kotlinx-io-core.js:2252
                        bitwiseAnd@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1912

[self 40.3ms]
  protoOf.h5h@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1616
    protoOf.q12@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:6255
      updateCellSend@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4423
        tryResumeReceiver@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:4538
          dispatch@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9202
            resumeUnconfined@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9239
              resume@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:9263
                protoOf.n8@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:4692
                  protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1319
                    protoOf.a13@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:6589
                      protoOf.y10@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:6596
                        divide@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1816

[self 39.2ms]
  serialize@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:257
    protoOf.k4a@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:1303
      protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:1795
        protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:1982
          protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:3078
            protoOf.i4a@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:1286
              protoOf.t4c@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                writeULongLe@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                  writeLongLe@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                    protoOf.b1l@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-io-kotlinx-io-core.js:551
                      protoOf.c1l@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-io-kotlinx-io-core.js:1118
                        bitwiseAnd@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1912

[self 29.4ms]
  fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
    fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
      fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
        fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
          _endSpan@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:152
            protoOf.r4z@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:560
              endInternal@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:467
                protoOf.m58@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1682
                  protoOf.h5h@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1616
                    protoOf.q12@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-coroutines-core.js:6255
                      modulo@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1908
                        divide@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1816

[self 28.5ms]
  protoOf.o8@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:2267
    serialize@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:257
      protoOf.k4a@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:1303
        protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:1795
          protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:1982
            protoOf.z4c@.../packages/comparison-otel-proto-anchored/kotlin/otlp-exporter-proto.js:3078
              protoOf.e4a@.../packages/comparison-otel-proto-anchored/kotlin/grpc-kmp-kmp-grpc-core.js:1257
                write$default@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-io-kotlinx-io-core.js:56
                  protoOf.m1k@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-io-kotlinx-io-core.js:453
                    checkBounds@.../packages/comparison-otel-proto-anchored/kotlin/kotlinx-io-kotlinx-io-core.js:89
                      compare@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1606
                        subtract@.../packages/comparison-otel-proto-anchored/kotlin/kotlin-kotlin-stdlib.js:1682
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 13 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=66.9 ms, total=76.3 ms, hits=41

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         65.2  |            66.9   |      40  | protoOf.p59  (.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501)
          1.7  |             1.7   |       1  | (root)  (:-1)
          0.0  |             1.6   |       0  | protoOf.i5h  (.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1663)
          0.0  |             5.1   |       0  | protoOf.j2a  (.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1737)
          0.0  |             0.5   |       0  | protoOf.o8  (.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1284)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 18.0ms]
  fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
    fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
      fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
        fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
          fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
            fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
              fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
                fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
                  _startSpan@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:145
                    protoOf.w4y@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:632
                      protoOf.p59@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:528

[self 15.7ms]
  fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
    fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
      fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
        fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
          fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
            fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
              fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
                fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
                  _startSpan@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:145
                    protoOf.w4y@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:632
                      protoOf.p59@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:528

[self 12.2ms]
  fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
    fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
      fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
        fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
          fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
            fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
              fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
                fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
                  _startSpan@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:145
                    protoOf.w4y@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:632
                      protoOf.p59@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:528

[self 8.0ms]
  fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
    fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
      fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
        fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
          fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
            fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
              fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
                fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
                  _startSpan@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:145
                    protoOf.w4y@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:632
                      protoOf.p59@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:528

[self 6.4ms]
  fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
    fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
      fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
        fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
          fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
            fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
              fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
                fibonacci@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:27
                  _startSpan@.../packages/comparison-otel-proto-anchored/kotlin/comparison-otel-proto-anchored.js:145
                    protoOf.w4y@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:632
                      protoOf.p59@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-anchored/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:528
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``otel-proto-anchored.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

