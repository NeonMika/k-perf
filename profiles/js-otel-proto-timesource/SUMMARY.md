# Profile -- otel-proto-timesource (Protobuf/gRPC + monotonic clock) (js)

**Variant:** `otel-proto-timesource`  
**Platform:** js  
**SUMMARY rendered:** 2026-05-05 22:28:15  
**Profile file last captured:** 2026-05-05 22:28:14  
**Profile file:** [otel-proto-timesource.cpuprofile](otel-proto-timesource.cpuprofile)  
**Wall time (capture run):** 9484 ms (incl. profiler overhead)  
**Workload-reported time:** 9169 ms  

---

## Top 30 frames

```
Profile: otel-proto-timesource.cpuprofile
Wall: 9312.5 ms total, 6454 nodes, 5702 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
   775.0  |    1046.1  |    457  | protoOf.e2                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:7226)
   652.5  |     652.5  |    391  | toTypedArray                                       (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:2797)
   567.9  |     567.9  |    325  | (idle)                                             (:-1)
   446.1  |    1187.5  |    267  | protoOf.o8                                         (.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:8987)
   409.7  |     411.3  |    240  | add                                                (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1818)
   383.2  |     431.7  |    225  | equals                                             (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:2231)
   381.0  |     381.0  |    226  | (garbage collector)                                (:-1)
   343.2  |     343.2  |    200  | arrayCopy                                          (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:3081)
   217.4  |    1290.7  |    127  | protoOf.c2                                         (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216)
   196.7  |     524.3  |    110  | subtract                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1723)
   168.7  |     168.7  |    105  | captureStack                                       (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:2370)
   158.1  |     688.2  |     93  | divide                                             (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1857)
   152.8  |     302.0  |     93  | writeVarUInt64                                     (.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1208)
   124.8  |     124.8  |     74  | bitwiseAnd                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1953)
    95.9  |     100.8  |     59  | internalConnectMultiple                            (node:net:1176)
    95.4  |      95.4  |    165  | (program)                                          (:-1)
    94.4  |     490.6  |     55  | toProto                                            (.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:3810)
    87.4  |     448.7  |     51  | multiply                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1735)
    84.7  |     258.8  |     52  | lessThan                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1849)
    79.0  |      79.0  |     44  | isNegative                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1719)
    77.5  |     522.9  |     44  | protoOf.s4r                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:593)
    76.0  |     728.5  |     46  | (anonymous)                                        (.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:8898)
    73.6  |      73.6  |     44  | protoOf.y4o                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:1431)
    71.8  |     215.6  |     43  | get_isValid                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:102)
    65.9  |      65.9  |     35  | fromNumber                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1799)
    65.4  |      65.4  |     39  | utf8Size                                           (.../packages/comparison-otel-proto-timesource/kotlin/okio-parent-okio.js:16)
    64.4  |      64.4  |     38  | toNumber                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1664)
    63.7  |      63.7  |     36  | charCodeAt                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:2029)
    56.6  |      56.6  |     34  | wrapSafe                                           (node:internal/modules/cjs/loader:1720)
    56.3  |     145.7  |     32  | protoOf.zi                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11007)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
    13.3  |   75933.9  |      7  | fibonacci                                          (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25)
     0.0  |    9312.2  |      0  | (root)                                             (:-1)
     0.0  |    8777.5  |      0  | (anonymous)                                        (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:0)
    12.1  |    6315.7  |      7  | protoOf.n8                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4733)
     0.0  |    4691.8  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |    4691.8  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     0.0  |    4683.4  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |    4681.8  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |    4675.1  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     0.0  |    4439.8  |      0  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |    4438.1  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     0.0  |    4311.6  |      0  | mainWrapper                                        (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:125)
     0.0  |    4311.6  |      0  | main                                               (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:80)
     0.0  |    4296.1  |      0  | workload                                           (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:66)
     6.5  |    4019.9  |      4  | protoOf.r8                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4788)
     9.3  |    3821.4  |      5  | protoOf.m4a                                        (.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1314)
    11.6  |    3818.7  |      7  | protoOf.l4a                                        (.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303)
     2.8  |    3351.0  |      1  | protoOf.qq                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:9144)
     2.1  |    3342.8  |      1  | listOnTimeout                                      (node:internal/timers:545)
     0.0  |    3342.0  |      0  | (anonymous)                                        (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:10875)
     2.0  |    3342.0  |      1  | protoOf.x1g                                        (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:11052)
     1.3  |    3342.0  |      1  | processTimers                                      (node:internal/timers:525)
     0.0  |    3279.7  |      0  | _endSpan                                           (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:121)
    30.4  |    3167.5  |     17  | protoOf.r12                                        (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:6255)
     1.7  |    3166.6  |      1  | protoOf.r5g                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1550)
    16.0  |    3059.5  |     10  | protoOf.o4s                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:514)
     0.0  |    2974.7  |      0  | endInternal                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:421)
     8.4  |    2842.4  |      5  | updateCellSend                                     (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4423)
     3.3  |    2828.8  |      2  | tryResumeReceiver                                  (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:4538)
     0.0  |    2825.5  |      0  | dispatchResume                                     (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:867)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 64 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=64.8 ms, total=531.2 ms, hits=38

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         37.1  |            37.1   |      22  | hrtime  (node:internal/process/per_thread:77)
         15.1  |            52.2   |       9  | protoOf.pc  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6922)
          3.4  |             8.2   |       2  | protoOf.o8  (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:6068)
          3.3  |             3.3   |       2  | protoOf.a4d  (.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:3073)
          1.7  |             1.7   |       1  | insert  (node:internal/timers:386)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 8.5ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
            _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:121
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11538
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11507
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6883
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6922
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 6.8ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
            _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:113
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11538
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11507
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6883
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6922
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 6.5ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
            _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:121
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11538
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11507
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6883
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6922
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 5.2ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
            _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:121
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11538
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11507
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6883
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6922
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 3.4ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
              _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:121
                protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11538
                  ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11507
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6883
                      protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6922
                        hrtime@node:internal/process/per_thread:77
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 114 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=997.5 ms, total=3824.4 ms, hits=587

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        775.0  |          1046.1   |     457  | protoOf.c2  (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216)
        217.4  |          1290.7   |     127  | (anonymous)  (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203)
          3.5  |          1405.3   |       2  | removeAll  (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
          1.7  |             6.7   |       1  | contentType  (.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-http.js:1438)
          0.0  |            68.2   |       0  | protoOf.o8  (.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:2540)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 156.6ms]
  protoOf.r8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4788
    protoOf.n8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4733
      protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1253
        exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1068
          protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1457
            protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.q4u@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:7226

[self 102.7ms]
  protoOf.r8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4788
    protoOf.n8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4733
      protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1253
        exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1068
          protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1457
            protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.q4u@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:7226

[self 99.8ms]
  protoOf.r8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4788
    protoOf.n8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4733
      protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1253
        exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1068
          protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1457
            protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.q4u@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:7226

[self 94.0ms]
  protoOf.r8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4788
    protoOf.n8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4733
      protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1253
        exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1068
          protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1457
            protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.q4u@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:7226

[self 53.9ms]
  protoOf.r8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4788
    protoOf.n8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4733
      protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1253
        exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1068
          protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1457
            protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.q4u@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:7226
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 519 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=704.0 ms, total=2098.9 ms, hits=411

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        181.3  |           471.4   |     103  | compare  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1647)
        119.7  |           492.5   |      71  | modulo  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1949)
         99.9  |           274.1   |      61  | multiply  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1735)
         63.1  |           289.6   |      37  | divide  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1857)
         43.7  |            43.7   |      27  | protoOf.d1l  (.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1118)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 16.7ms]
  protoOf.l4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303
    protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:1977
      protoOf.m4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1314
        protoOf.l4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303
          protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:3073
            protoOf.j4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1286
              protoOf.u4c@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                writeULongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                  writeLongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                    protoOf.c1l@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:551
                      protoOf.d1l@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1118
                        bitwiseAnd@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1953

[self 16.3ms]
  protoOf.l4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303
    protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:1977
      protoOf.m4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1314
        protoOf.l4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303
          protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:3073
            protoOf.j4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1286
              protoOf.u4c@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                writeULongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                  writeLongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                    protoOf.c1l@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:551
                      protoOf.d1l@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1118
                        bitwiseAnd@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1953

[self 13.7ms]
  protoOf.m4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1314
    protoOf.l4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303
      protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:1977
        protoOf.m4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1314
          protoOf.l4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303
            protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:3073
              protoOf.j4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1286
                protoOf.u4c@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                  writeULongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                    writeLongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                      reverseBytes_0@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:2252
                        bitwiseAnd@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1953

[self 12.3ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
            _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:121
              protoOf.o4s@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:514
                endInternal@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:421
                  protoOf.x57@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1616
                    protoOf.r5g@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1550
                      protoOf.r12@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-coroutines-core.js:6255
                        divide@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1857

[self 10.6ms]
  protoOf.l4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303
    protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:1977
      protoOf.m4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1314
        protoOf.l4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1303
          protoOf.a4d@.../packages/comparison-otel-proto-timesource/kotlin/otlp-exporter-proto.js:3073
            protoOf.j4a@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1286
              protoOf.u4c@.../packages/comparison-otel-proto-timesource/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                writeULongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                  writeLongLe@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                    protoOf.c1l@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:551
                      protoOf.d1l@.../packages/comparison-otel-proto-timesource/kotlin/kotlinx-io-kotlinx-io-core.js:1118
                        bitwiseAnd@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1953
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 11 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=11.9 ms, total=28.7 ms, hits=7

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         11.9  |            20.2   |       7  | protoOf.y58  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:455)
          0.0  |             1.7   |       0  | protoOf.t5g  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1597)
          0.0  |             6.7   |       0  | protoOf.k2a  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1659)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 3.4ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:113
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:593
                      protoOf.y58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:455
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:482

[self 1.7ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:113
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:593
                      protoOf.y58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:455
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:482

[self 1.7ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:113
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:593
                      protoOf.y58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:455
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:482

[self 1.7ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:113
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:593
                      protoOf.y58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:455
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:482

[self 1.7ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:25
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:113
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:593
                      protoOf.y58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:455
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:482
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``otel-proto-timesource.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

