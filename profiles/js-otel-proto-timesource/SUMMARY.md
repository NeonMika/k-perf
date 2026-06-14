# Profile -- otel-proto-timesource (Protobuf/gRPC + monotonic clock) (js)

**Variant:** `otel-proto-timesource`  
**Platform:** js  
**SUMMARY rendered:** 2026-06-13 17:07:11  
**Profile file last captured:** 2026-06-13 17:07:10  
**Profile file:** [otel-proto-timesource.cpuprofile](otel-proto-timesource.cpuprofile)  
**Wall time (capture run):** 52542 ms (incl. profiler overhead)  
**Workload-reported time:** 51985 ms  

---

## Top 30 frames

```
Profile: otel-proto-timesource.cpuprofile
Wall: 52125.1 ms total, 12402 nodes, 31322 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
  6446.7  |    7548.1  |   3949  | protoOf.o8                                         (.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:8987)
  5814.4  |    7037.3  |   3381  | recyclableRemoveAll                                (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787)
  4715.7  |    4715.7  |   2936  | (idle)                                             (:-1)
  3872.7  |    3872.7  |   2369  | (garbage collector)                                (:-1)
  2508.0  |    2508.0  |   1472  | arrayCopy                                          (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:3040)
  1755.8  |    1775.3  |   1018  | add                                                (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1777)
  1350.9  |    8444.5  |    778  | removeAll                                          (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
  1267.3  |    2692.1  |    724  | subtract                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1682)
  1234.3  |    1386.3  |    719  | equals                                             (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:2190)
  1096.0  |    4336.7  |    610  | divide                                             (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1816)
   886.4  |     886.4  |    541  | toTypedArray                                       (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:2756)
   750.8  |    1410.4  |    426  | writeVarUInt64                                     (.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1208)
   736.7  |    2572.8  |    418  | multiply                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1694)
   587.8  |     620.5  |    348  | protoOf.y4o                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:1431)
   531.5  |     531.5  |    310  | isNegative                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1678)
   494.5  |     496.2  |    288  | bitwiseAnd                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1912)
   483.0  |     483.0  |    303  | captureStack                                       (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:2329)
   471.8  |    1426.7  |    276  | get_isValid                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:102)
   402.9  |     402.9  |   1582  | (program)                                          (:-1)
   394.3  |     394.3  |    226  | toNumber                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1623)
   387.4  |     387.4  |    232  | charCodeAt                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1988)
   371.7  |     381.8  |    234  | readableByteStreamControllerEnqueue                (node:internal/webstreams/readablestream:2872)
   370.6  |    2966.5  |    214  | protoOf.s4r                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:639)
   370.4  |     380.4  |    220  | equalsLong                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1666)
   320.4  |    3276.8  |    186  | compare                                            (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1606)
   309.6  |     770.1  |    179  | protoOf.zi                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:10966)
   298.2  |    1389.1  |    177  | toDuration_0                                       (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11340)
   297.6  |    6754.5  |    175  | protoOf.a4d                                        (.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:3078)
   265.6  |     265.6  |    155  | fromNumber                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1758)
   261.5  |     263.1  |    154  | utf8Size                                           (.../packages/comparison-otel-proto-timesource/kotlin/okio-parent-okio.js:16)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
   119.3  |  305141.8  |     68  | fibonacci                                          (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28)
     0.0  |   52124.9  |      0  | (root)                                             (:-1)
     0.0  |   43872.0  |      0  | (anonymous)                                        (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:0)
    71.5  |   34200.5  |     43  | protoOf.n8                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4692)
     0.0  |   22209.5  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |   22209.5  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     1.7  |   22199.6  |      1  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |   22197.9  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |   22191.4  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     0.0  |   21985.1  |      0  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |   21983.9  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     0.0  |   21863.6  |      0  | mainWrapper                                        (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:158)
     0.0  |   21863.6  |      0  | main                                               (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:84)
     0.0  |   21838.1  |      0  | workload                                           (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:69)
    42.4  |   20047.7  |     24  | processTimers                                      (node:internal/timers:525)
    31.3  |   20036.3  |     17  | protoOf.qq                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9144)
    14.3  |   20017.7  |      8  | protoOf.x1g                                        (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:11052)
    11.6  |   19993.6  |      6  | listOnTimeout                                      (node:internal/timers:545)
     0.1  |   18883.3  |      0  | protoOf.o8                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4878)
     0.0  |   16860.9  |      0  | _endSpan                                           (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:154)
     0.0  |   15777.6  |      0  | protoOf.o4s                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:560)
    34.2  |   15732.1  |     21  | protoOf.j5h                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1623)
     7.9  |   15712.3  |      4  | endInternal                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:467)
   101.3  |   15699.2  |     59  | protoOf.r12                                        (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:6255)
     0.0  |   15541.7  |      0  | protoOf.o58                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1689)
     0.0  |   15168.8  |      0  | (anonymous)                                        (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:10875)
    44.0  |   14052.1  |     26  | updateCellSend                                     (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4423)
     0.4  |   13944.5  |      0  | tryResumeReceiver                                  (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4538)
     1.8  |   13941.0  |      1  | dispatch                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9202)
     1.6  |   13937.6  |      1  | resumeUnconfined                                   (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9239)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 94 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=207.1 ms, total=2613.2 ms, hits=125

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        147.8  |           147.8   |      88  | hrtime  (node:internal/process/per_thread:77)
         33.0  |           180.9   |      20  | protoOf.pc  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881)
          9.7  |             9.7   |       6  | insert  (node:internal/timers:386)
          4.7  |             4.7   |       3  | protoOf.a4d  (.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:3078)
          3.4  |            11.1   |       2  | protoOf.o8  (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4082)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 22.7ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:154
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11500
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11469
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6842
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 19.8ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:154
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11500
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11469
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6842
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 17.5ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:146
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11500
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11469
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6842
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 13.4ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:154
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11500
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11469
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6842
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 13.3ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:154
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11500
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11469
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6842
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 167 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=5827.0 ms, total=9275.8 ms, hits=3389

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
       5814.4  |          7037.3   |    3381  | removeAll  (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
          4.9  |          1065.5   |       3  | (anonymous)  (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203)
          4.1  |          1045.9   |       2  | protoOf.c2  (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216)
          3.3  |           106.0   |       2  | protoOf.o8  (.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:2540)
          0.2  |             2.6   |       1  | header  (.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:7549)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 1066.3ms]
  updateCellSend@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1326
                exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1141
                  protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1530
                    protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 923.3ms]
  updateCellSend@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1326
                exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1141
                  protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1530
                    protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 763.4ms]
  updateCellSend@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1326
                exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1141
                  protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1530
                    protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 633.9ms]
  updateCellSend@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1326
                exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1141
                  protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1530
                    protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 442.2ms]
  updateCellSend@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1326
                exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1141
                  protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1530
                    protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 1332 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=4174.8 ms, total=11731.8 ms, hits=2388

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
       1285.0  |          2523.6   |     741  | compare  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1606)
        630.0  |          2417.6   |     356  | modulo  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1908)
        605.3  |          1843.0   |     345  | divide  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1816)
        302.0  |          1345.6   |     181  | multiply  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1694)
        185.8  |           185.8   |     108  | writeVarUInt64  (.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1208)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 88.0ms]
  serialize@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:257
    protoOf.l4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303
      protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:1795
        protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:1982
          protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:3078
            protoOf.j4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1286
              protoOf.u4c@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                writeULongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                  writeLongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                    protoOf.c1l@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:551
                      protoOf.d1l@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1118
                        bitwiseAnd@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1912

[self 55.6ms]
  protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:2267
    serialize@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:257
      protoOf.l4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303
        protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:1795
          protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:1982
            protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:3078
              protoOf.j4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1286
                protoOf.u4c@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                  writeULongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                    writeLongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                      reverseBytes_0@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:2252
                        bitwiseAnd@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1912

[self 42.9ms]
  protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:2267
    serialize@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:257
      protoOf.l4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303
        protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:1795
          protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:1982
            protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:3078
              protoOf.f4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1257
                write$default@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:56
                  protoOf.n1k@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:453
                    checkBounds@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:89
                      compare@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1606
                        subtract@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1682

[self 38.8ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:154
              protoOf.o4s@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:560
                endInternal@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:467
                  protoOf.o58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1689
                    protoOf.j5h@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1623
                      protoOf.r12@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:6255
                        divide@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1816

[self 36.4ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:154
            protoOf.o4s@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:560
              endInternal@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:467
                protoOf.o58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1689
                  protoOf.j5h@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1623
                    protoOf.r12@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:6255
                      modulo@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1908
                        divide@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1816
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 13 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=62.2 ms, total=75.3 ms, hits=35

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         62.2  |            65.4   |      35  | protoOf.r59  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501)
          0.0  |             1.6   |       0  | protoOf.k5h  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1670)
          0.0  |             6.6   |       0  | protoOf.k2a  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1744)
          0.0  |             1.7   |       0  | l  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1270)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 18.2ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:146
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:639
                      protoOf.r59@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:528

[self 14.9ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:146
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:639
                      protoOf.r59@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:528

[self 8.2ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:146
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:639
                      protoOf.r59@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:528

[self 8.2ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:146
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:639
                      protoOf.r59@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:528

[self 5.0ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:146
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:639
                      protoOf.r59@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:501
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:528
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``otel-proto-timesource.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

