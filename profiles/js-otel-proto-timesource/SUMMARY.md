# Profile -- otel-proto-timesource (Protobuf/gRPC + monotonic clock) (js)

**Variant:** `otel-proto-timesource`  
**Platform:** js  
**SUMMARY rendered:** 2026-05-20 20:42:40  
**Profile file last captured:** 2026-05-20 20:42:39  
**Profile file:** [otel-proto-timesource.cpuprofile](otel-proto-timesource.cpuprofile)  
**Wall time (capture run):** 2994 ms (incl. profiler overhead)  
**Workload-reported time:** 2761 ms  

---

## Top 30 frames

```
Profile: otel-proto-timesource.cpuprofile
Wall: 2895.8 ms total, 2686 nodes, 1810 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
   192.4  |     192.4  |    117  | add                                                (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1777)
   150.3  |    1003.3  |     95  | protoOf.s4r                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:594)
   142.0  |     305.2  |     89  | subtract                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1682)
   112.0  |     115.3  |     70  | protoOf.y4o                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:1431)
   110.6  |     110.6  |     68  | (garbage collector)                                (:-1)
   102.2  |     539.3  |     62  | toDuration_0                                       (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11340)
    97.8  |     291.5  |     61  | protoOf.zi                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:10966)
    95.3  |      95.3  |     56  | isNegative                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1678)
    80.2  |      80.2  |     48  | shiftRight                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1874)
    71.0  |     306.3  |     43  | get_isValid                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:102)
    70.7  |      72.3  |     44  | equals                                             (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:2190)
    66.8  |      66.8  |     40  | equalsLong                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1666)
    62.7  |      62.7  |     36  | charCodeAt                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1988)
    59.4  |    1684.6  |     35  | _startSpan                                         (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139)
    58.7  |      91.7  |     37  | roundToLong                                        (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:5355)
    57.8  |     145.7  |     35  | protoOf.b5a                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1760)
    57.1  |     235.7  |     32  | protoOf.d58                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:399)
    56.2  |      56.2  |     36  | hrtime                                             (:-1)
    55.9  |     865.9  |     33  | protoOf.pc                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881)
    51.1  |      51.1  |     32  | wrapSafe                                           (node:internal/modules/cjs/loader:1720)
    47.2  |      47.2  |     30  | bitwiseAnd                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1912)
    46.8  |     133.1  |     28  | protoOf.o4p                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:734)
    46.6  |     171.8  |     29  | durationOfNanosNormalized                          (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11364)
    41.6  |      43.2  |     26  | shiftLeft                                          (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1887)
    41.1  |     185.4  |     24  | multiply                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1694)
    35.3  |      35.3  |     22  | writeBuffer                                        (:-1)
    34.7  |      34.7  |     22  | toNumber                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1623)
    34.0  |   15921.3  |     22  | fibonacci                                          (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28)
    32.4  |     120.3  |     20  | lessThan                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1808)
    28.8  |     104.4  |     16  | _get_links__eq4bew                                 (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:526)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
    34.0  |   15921.3  |     22  | fibonacci                                          (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28)
     0.0  |    5116.9  |      0  | (anonymous)                                        (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:0)
     0.0  |    2895.5  |      0  | (root)                                             (:-1)
     1.6  |    2839.0  |      1  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |    2837.4  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     0.0  |    2834.1  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |    2834.1  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |    2823.3  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     0.0  |    2606.4  |      0  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |    2604.9  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     0.0  |    2489.0  |      0  | mainWrapper                                        (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:151)
     5.3  |    2489.0  |      3  | main                                               (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:81)
     1.6  |    1978.7  |      1  | workload                                           (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:69)
    59.4  |    1684.6  |     35  | _startSpan                                         (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139)
   150.3  |    1003.3  |     95  | protoOf.s4r                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:594)
    55.9  |     865.9  |     33  | protoOf.pc                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881)
     0.0  |     858.2  |      0  | ValueTimeMark__elapsedNow_impl_eonqvs              (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11469)
     0.0  |     851.1  |      0  | protoOf.dj                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11500)
    17.6  |     849.4  |     10  | protoOf.pc                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6842)
     0.0  |     570.1  |      0  | _endSpan                                           (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:147)
   102.2  |     539.3  |     62  | toDuration_0                                       (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11340)
    11.4  |     436.8  |      7  | compare                                            (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1606)
     0.0  |     320.2  |      0  | durationOfNanos                                    (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11419)
    71.0  |     306.3  |     43  | get_isValid                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:102)
   142.0  |     305.2  |     89  | subtract                                           (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1682)
    97.8  |     291.5  |     61  | protoOf.zi                                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:10966)
    57.1  |     235.7  |     32  | protoOf.d58                                        (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:399)
     0.0  |     235.7  |      0  | require                                            (node:internal/modules/helpers:151)
     0.0  |     235.7  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1567)
    11.2  |     207.0  |      6  | Duration__plus_impl_yu9v8f                         (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11008)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 62 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=61.0 ms, total=976.5 ms, hits=39

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         56.2  |            56.2   |      36  | hrtime  (node:internal/process/per_thread:77)
          4.9  |            62.1   |       3  | protoOf.pc  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881)
          0.0  |           849.4   |       0  | protoOf.dj  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11500)
          0.0  |             8.7   |       0  | main  (.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:81)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 12.7ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11500
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11469
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6842
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 8.0ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:147
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11500
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11469
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6842
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 5.9ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11500
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11469
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6842
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 5.8ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:147
              protoOf.dj@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11500
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:11469
                  protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6842
                    protoOf.pc@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:6881
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 4.9ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139
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
Found 5 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=19.8 ms, total=90.9 ms, hits=13

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         10.0  |            25.6   |       7  | recyclableRemoveAll  (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787)
          8.1  |            49.5   |       5  | removeAll  (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
          1.7  |             6.6   |       1  | protoOf.c2  (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216)
          0.0  |             6.6   |       0  | (anonymous)  (.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203)
          0.0  |             2.5   |       0  | protoOf.o8  (.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:2540)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 10.0ms]
  protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1254
    flush@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1060
      protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1377
        exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1069
          protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1458
            removeSpanDataFromBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1047
              protoOf.y4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:213
                protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                  protoOf.q4u@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                    removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                      recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                        protoOf.e2@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:7185

[self 8.1ms]
  protoOf.n8@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:4692
    protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1254
      flush@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1060
        protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1377
          exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1069
            protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1458
              removeSpanDataFromBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1047
                protoOf.y4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:213
                  protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                    protoOf.q4u@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                      removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 1.7ms]
  protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1377
    exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1069
      protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1458
        removeSpanDataFromBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1047
          protoOf.y4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:213
            protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.q4u@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:7185

[self NaNms]
  flush@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1060
    protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1377
      exportCurrentBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1069
        protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1458
          removeSpanDataFromBatch@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1047
            protoOf.y4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:213
              protoOf.z4t@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                protoOf.q4u@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                  removeAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                    recyclableRemoveAll@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                      (anon)@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                        protoOf.c2@.../packages/comparison-otel-proto-timesource/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216

[self NaNms]
  proceedLoop@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-utils.js:1365
    protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-utils.js:1375
      l@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:3052
        protoOf.e30@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:2973
          protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:2983
            protoOf.f26@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-utils.js:1455
              proceedLoop@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-utils.js:1365
                protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-utils.js:1375
                  l@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:2615
                    protoOf.e30@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:2530
                      protoOf.o8@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-client-core.js:2540
                        protoOf.e26@.../packages/comparison-otel-proto-timesource/kotlin/ktor-ktor-utils.js:1451
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 204 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=329.4 ms, total=733.9 ms, hits=203

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        178.1  |           339.8   |     112  | compare  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1606)
         46.9  |           134.8   |      27  | multiply  (.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1694)
         45.5  |            45.5   |      29  | protoOf.o4p  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:734)
         37.1  |           166.3   |      22  | normalizeToNanos  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:337)
          4.8  |             4.8   |       3  | (root)  (:-1)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 19.4ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139
                protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:594
                  protoOf.d58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:399
                    protoOf.r4s@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:1352
                      protoOf.o4p@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:734
                        bitwiseAnd@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1912

[self 12.8ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139
                protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:594
                  protoOf.d58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:399
                    protoOf.r4s@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:1352
                      protoOf.o4p@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:734
                        bitwiseAnd@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1912

[self 8.3ms]
  workload@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:69
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139
                  protoOf.x4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:587
                    normalizeToNanos@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:337
                      multiply@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1694
                        lessThan@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1808

[self 8.1ms]
  workload@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:69
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139
                    protoOf.x4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:587
                      normalizeToNanos@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:337
                        multiply@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1694

[self 7.9ms]
  workload@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:69
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                  _endSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:147
                    protoOf.o4s@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:515
                      normalizeToNanos@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-api-all.js:337
                        multiply@.../packages/comparison-otel-proto-timesource/kotlin/kotlin-kotlin-stdlib.js:1694
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 7 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=6.4 ms, total=21.0 ms, hits=4

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
          6.4  |            12.9   |       4  | protoOf.y58  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:456)
          0.0  |             1.5   |       0  | protoOf.t5g  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1598)
          0.0  |             6.5   |       0  | protoOf.k2a  (.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1669)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 3.2ms]
  mainWrapper@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:151
    main@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:81
      workload@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:69
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:594
                      protoOf.y58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:456
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:483

[self 3.2ms]
  fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:594
                      protoOf.y58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:456
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:483

[self NaNms]
  (root)@:-1
    (anon)@node:internal/main/run_main_module:0
      executeUserEntryPoint@node:internal/modules/run_main:139
        wrapModuleLoad@node:internal/modules/cjs/loader:237
          (anon)@node:internal/modules/cjs/loader:1238
            (anon)@node:internal/modules/cjs/loader:1537
              (anon)@node:internal/modules/cjs/loader:1925
                (anon)@node:internal/modules/cjs/loader:1781
                  (anon)@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:0
                    (anon)@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:0
                      protoOf.t5g@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1598
                        BatchSpanProcessorBuilder@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1648

[self NaNms]
  (root)@:-1
    (anon)@node:internal/main/run_main_module:0
      executeUserEntryPoint@node:internal/modules/run_main:139
        wrapModuleLoad@node:internal/modules/cjs/loader:237
          (anon)@node:internal/modules/cjs/loader:1238
            (anon)@node:internal/modules/cjs/loader:1537
              (anon)@node:internal/modules/cjs/loader:1925
                (anon)@node:internal/modules/cjs/loader:1781
                  (anon)@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:0
                    (anon)@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:0
                      protoOf.k2a@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1669
                        BatchSpanProcessor@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1607

[self NaNms]
  workload@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:69
    fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
      fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
        fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
          fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
            fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
              fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                fibonacci@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:28
                  _startSpan@.../packages/comparison-otel-proto-timesource/kotlin/comparison-otel-proto-timesource.js:139
                    protoOf.s4r@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:594
                      protoOf.y58@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:456
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-timesource/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:483
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``otel-proto-timesource.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

