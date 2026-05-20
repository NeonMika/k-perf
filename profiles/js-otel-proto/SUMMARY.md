# Profile -- otel-proto (Protobuf/gRPC) (js)

**Variant:** `otel-proto`  
**Platform:** js  
**SUMMARY rendered:** 2026-05-20 20:42:27  
**Profile file last captured:** 2026-05-20 20:42:27  
**Profile file:** [otel-proto.cpuprofile](otel-proto.cpuprofile)  
**Wall time (capture run):** 2387 ms (incl. profiler overhead)  
**Workload-reported time:** 2172 ms  

---

## Top 30 frames

```
Profile: otel-proto.cpuprofile
Wall: 2306.9 ms total, 2280 nodes, 1467 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
   156.2  |     156.2  |     96  | millis                                             (.../node_modules/@js-joda/core/dist/js-joda.js:12810)
   138.5  |    1027.2  |     88  | protoOf.r4r                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:597)
   136.0  |     142.3  |     83  | protoOf.x4o                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:1440)
   104.8  |     104.8  |     65  | add                                                (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1777)
    85.2  |     154.4  |     50  | protoOf.n4p                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:741)
    79.9  |     171.5  |     51  | protoOf.c5a                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1763)
    74.6  |      74.6  |     48  | charCodeAt                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1988)
    69.6  |     263.8  |     43  | multiply                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1694)
    67.5  |     331.0  |     40  | get_isValid                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:102)
    60.5  |      60.5  |     38  | (garbage collector)                                (:-1)
    57.4  |      59.0  |     36  | equals                                             (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:2190)
    55.3  |      55.3  |     35  | wrapSafe                                           (node:internal/modules/cjs/loader:1720)
    50.7  |     162.2  |     31  | lessThan                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1808)
    50.1  |   12038.0  |     31  | fibonacci                                          (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25)
    46.8  |     132.4  |     28  | subtract                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1682)
    46.1  |     216.7  |     27  | protoOf.u4l                                        (.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:277)
    41.1  |     166.4  |     25  | protoOf.o4s                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:518)
    38.7  |     150.1  |     24  | getNanoseconds                                     (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:369)
    36.4  |     229.2  |     23  | protoOf.e58                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:402)
    31.9  |      94.7  |     18  | _get_links__eq4bew                                 (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:529)
    29.7  |      29.7  |     19  | shiftRight                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1874)
    28.9  |      28.9  |     18  | bitwiseAnd                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1912)
    25.6  |      25.6  |     16  | equalsLong                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1666)
    24.0  |      24.0  |     15  | writeBuffer                                        (:-1)
    23.9  |      50.9  |     14  | protoOf.z58                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:459)
    22.7  |      39.2  |     14  | protoOf.e2                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:7185)
    21.0  |     141.3  |     11  | protoOf.q12                                        (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6255)
    19.4  |      19.4  |     12  | charArray                                          (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1461)
    19.4  |      19.4  |     12  | protoOf.a5                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:3514)
    18.7  |      44.0  |     12  | getInterfaceMaskFor                                (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:2555)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
    50.1  |   12038.0  |     31  | fibonacci                                          (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25)
     0.0  |    4054.9  |      0  | (anonymous)                                        (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:0)
     0.0  |    2306.6  |      0  | (root)                                             (:-1)
     0.0  |    2289.9  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |    2289.9  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     1.6  |    2283.5  |      1  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |    2282.0  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |    2275.6  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     0.0  |    2075.5  |      0  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |    2074.3  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     0.0  |    1958.4  |      0  | mainWrapper                                        (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:147)
     2.8  |    1958.4  |      1  | main                                               (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:78)
     6.5  |    1625.8  |      4  | workload                                           (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:66)
    16.0  |    1325.6  |     10  | _startSpan                                         (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135)
   138.5  |    1027.2  |     88  | protoOf.r4r                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:597)
     0.0  |     339.5  |      0  | _endSpan                                           (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:143)
    67.5  |     331.0  |     40  | get_isValid                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:102)
    69.6  |     263.8  |     43  | multiply                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1694)
     6.6  |     239.5  |      4  | end                                                (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:149)
    36.4  |     229.2  |     23  | protoOf.e58                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:402)
     0.0  |     217.2  |      0  | require                                            (node:internal/modules/helpers:151)
     0.0  |     217.2  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1567)
    46.1  |     216.7  |     27  | protoOf.u4l                                        (.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:277)
     3.2  |     216.6  |      2  | protoOf.u4l                                        (.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:54)
     0.0  |     214.4  |      0  | protoOf.r8                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4747)
     0.0  |     214.4  |      0  | protoOf.n8                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692)
     4.9  |     182.4  |      3  | setStartTimestamp                                  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:137)
    79.9  |     171.5  |     51  | protoOf.c5a                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1763)
     0.0  |     170.6  |      0  | instant                                            (.../node_modules/@js-joda/core/dist/js-joda.js:12814)
    41.1  |     166.4  |     25  | protoOf.o4s                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:518)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 33 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=1.5 ms, total=199.3 ms, hits=0

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
          1.5  |             1.5   |       0  | protoOf.pc  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:6881)
          0.0  |           170.6   |       0  | protoOf.u4l  (.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:277)
          0.0  |            14.4   |       0  | _create  (.../node_modules/@js-joda/core/dist/js-joda.js:12370)
          0.0  |            12.7   |       0  | main  (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:78)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 1.5ms]
  wrapModuleLoad@node:internal/modules/cjs/loader:237
    (anon)@node:internal/modules/cjs/loader:1238
      (anon)@node:internal/modules/cjs/loader:1537
        (anon)@node:internal/modules/cjs/loader:1925
          (anon)@node:internal/modules/cjs/loader:1781
            (anon)@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:0
              (anon)@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:0
                mainWrapper@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:147
                  main@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:78
                    ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:11469
                      protoOf.pc@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:6881
                        hrtime@node:internal/process/per_thread:77

[self NaNms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                    protoOf.u4l@.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:54
                      protoOf.u4l@.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:277
                        instant@.../node_modules/@js-joda/core/dist/js-joda.js:12814

[self NaNms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                    protoOf.u4l@.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:54
                      protoOf.u4l@.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:277
                        instant@.../node_modules/@js-joda/core/dist/js-joda.js:12814

[self NaNms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                  _endSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:143
                    protoOf.u4l@.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:54
                      protoOf.u4l@.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:277
                        instant@.../node_modules/@js-joda/core/dist/js-joda.js:12814

[self NaNms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                    protoOf.u4l@.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:54
                      protoOf.u4l@.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:277
                        instant@.../node_modules/@js-joda/core/dist/js-joda.js:12814
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 13 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=33.8 ms, total=129.7 ms, hits=21

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         21.1  |            32.2   |      13  | recyclableRemoveAll  (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787)
         11.1  |            58.4   |       7  | removeAll  (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
          1.6  |             7.0   |       1  | protoOf.c2  (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216)
          0.0  |             7.0   |       0  | (anonymous)  (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203)
          0.0  |             0.3   |       0  | protoOf.g37  (.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:7354)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 21.1ms]
  protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1257
    flush@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1063
      protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1380
        exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1072
          protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1461
            removeSpanDataFromBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1050
              protoOf.z4t@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:213
                protoOf.a4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                  protoOf.r4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                    removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                      recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                        protoOf.e2@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:7185

[self 11.1ms]
  protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692
    protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1257
      flush@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1063
        protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1380
          exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1072
            protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1461
              removeSpanDataFromBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1050
                protoOf.z4t@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:213
                  protoOf.a4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                    protoOf.r4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                      removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 1.6ms]
  protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1380
    exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1072
      protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1461
        removeSpanDataFromBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1050
          protoOf.z4t@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:213
            protoOf.a4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.r4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:7185

[self NaNms]
  flush@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1063
    protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1380
      exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1072
        protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1461
          removeSpanDataFromBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1050
            protoOf.z4t@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:213
              protoOf.a4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                protoOf.r4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                  removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                    removeAll_0@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:759
                      (anon)@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                        protoOf.c2@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216

[self NaNms]
  protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1380
    exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1072
      protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1461
        removeSpanDataFromBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1050
          protoOf.z4t@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:213
            protoOf.a4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.r4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  removeAll_0@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:759
                    (anon)@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:7185
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 149 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=225.8 ms, total=625.3 ms, hits=139

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         66.8  |           178.3   |      41  | multiply  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1694)
         50.0  |           130.8   |      30  | compare  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1606)
         36.8  |           155.7   |      22  | normalizeToNanos  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:344)
         31.1  |            98.5   |      20  | getNanoseconds  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:369)
         27.3  |            27.3   |      17  | protoOf.n4p  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:741)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 8.1ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                protoOf.r4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:597
                  protoOf.e58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:402
                    protoOf.s4s@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:1361
                      protoOf.n4p@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:741
                        bitwiseAnd@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1912

[self 6.5ms]
  workload@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:66
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                    setStartTimestamp@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:137
                      getNanoseconds@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:369
                        multiply@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1694

[self 6.4ms]
  workload@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:66
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                  setStartTimestamp@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:137
                    getNanoseconds@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:369
                      multiply@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1694
                        equalsLong@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1666

[self 6.4ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                protoOf.r4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:597
                  protoOf.e58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:402
                    protoOf.s4s@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:1361
                      protoOf.n4p@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:741
                        bitwiseAnd@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1912

[self 6.3ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                  setStartTimestamp@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:137
                    protoOf.w4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:590
                      normalizeToNanos@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:344
                        multiply@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1694
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 10 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=12.6 ms, total=33.3 ms, hits=8

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         12.6  |            25.3   |       8  | protoOf.z58  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:459)
          0.0  |             1.6   |       0  | protoOf.u5g  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1601)
          0.0  |             6.4   |       0  | protoOf.j2a  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1672)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 4.8ms]
  mainWrapper@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:147
    main@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:78
      workload@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:66
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                    protoOf.r4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:597
                      protoOf.z58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:459
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:486

[self 4.6ms]
  main@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:78
    workload@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:66
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                    protoOf.r4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:597
                      protoOf.z58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:459
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:486

[self 1.6ms]
  (anon)@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:0
    (anon)@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:0
      mainWrapper@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:147
        main@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:78
          workload@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:66
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                    protoOf.r4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:597
                      protoOf.z58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:459
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:486

[self 1.6ms]
  (anon)@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:0
    mainWrapper@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:147
      main@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:78
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:25
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:135
                    protoOf.r4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:597
                      protoOf.z58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:459
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:486

[self NaNms]
  (root)@:-1
    (anon)@node:internal/main/run_main_module:0
      executeUserEntryPoint@node:internal/modules/run_main:139
        wrapModuleLoad@node:internal/modules/cjs/loader:237
          (anon)@node:internal/modules/cjs/loader:1238
            (anon)@node:internal/modules/cjs/loader:1537
              (anon)@node:internal/modules/cjs/loader:1925
                (anon)@node:internal/modules/cjs/loader:1781
                  (anon)@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:0
                    (anon)@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:0
                      protoOf.u5g@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1601
                        BatchSpanProcessorBuilder@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1651
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``otel-proto.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

