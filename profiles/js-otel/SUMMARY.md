# Profile -- otel (JSON/HTTP) (js)

**Variant:** `otel`  
**Platform:** js  
**SUMMARY rendered:** 2026-05-05 22:27:08  
**Profile file last captured:** 2026-05-05 22:27:07  
**Profile file:** [otel.cpuprofile](otel.cpuprofile)  
**Wall time (capture run):** 17504 ms (incl. profiler overhead)  
**Workload-reported time:** 17178 ms  

---

## Top 30 frames

```
Profile: otel.cpuprofile
Wall: 17314.0 ms total, 6218 nodes, 10681 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
  4219.1  |    4219.1  |   2544  | toTypedArray                                       (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2924)
  2799.1  |    7438.9  |   1674  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7432)
   854.8  |    1241.6  |    383  | encodeUtf8                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:6609)
   831.0  |    1290.1  |    482  | recyclableRemoveAll                                (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774)
   553.2  |     553.2  |    321  | charCodeAt                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2049)
   499.4  |     499.4  |    303  | (garbage collector)                                (:-1)
   424.8  |     426.5  |    262  | add                                                (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1838)
   403.7  |    4622.8  |    245  | (anonymous)                                        (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7343)
   401.9  |     401.9  |    208  | (idle)                                             (:-1)
   370.0  |     382.3  |    220  | equals                                             (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2212)
   346.0  |     666.6  |    202  | printQuoted                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:845)
   340.3  |    1043.4  |    195  | protoOf.w19                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:800)
   252.4  |     641.7  |    154  | subtract                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1743)
   233.5  |    1087.3  |    145  | divide                                             (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1877)
   192.0  |    1496.4  |    113  | removeAll                                          (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647)
   184.6  |     184.6  |    104  | _init_properties_StringOps_kt__fcy1db              (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:872)
   174.8  |     708.9  |    108  | multiply                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1755)
   171.5  |     646.6  |     98  | protoOf.l19                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:650)
   159.3  |   13598.8  |     88  | protoOf.k1a                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:561)
   122.7  |     122.7  |    230  | (program)                                          (:-1)
   107.6  |     107.6  |     61  | equalsLong                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1727)
   105.4  |     400.6  |     63  | lessThan                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1869)
    90.6  |     101.9  |     53  | switchMode                                         (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:997)
    89.1  |     295.4  |     50  | protoOf.j19                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:623)
    83.9  |     102.8  |     48  | encodeTypeInfo                                     (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:530)
    83.2  |      83.2  |     49  | millis                                             (.../node_modules/@js-joda/core/dist/js-joda.js:12810)
    82.2  |      83.9  |     51  | fromNumber                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1819)
    81.4  |      81.4  |     51  | toNumber                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1684)
    71.3  |      71.3  |     43  | captureStack                                       (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2390)
    64.2  |      99.6  |     37  | protoOf.v                                          (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:1018)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
    24.5  |   63163.0  |     15  | fibonacci                                          (.../packages/comparison-otel/kotlin/comparison-otel.js:21)
     0.0  |   17313.8  |      0  | (root)                                             (:-1)
    19.0  |   14613.4  |     11  | protoOf.n8                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4861)
   159.3  |   13598.8  |     88  | protoOf.k1a                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:561)
     4.2  |   12198.3  |      2  | protoOf.pq                                         (.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400)
     1.7  |   12198.0  |      1  | (anonymous)                                        (.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485)
     3.8  |   12196.3  |      2  | protoOf.s14                                        (.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662)
     1.6  |   12078.9  |      1  | processTimers                                      (node:internal/timers:525)
     1.6  |   12077.3  |      1  | listOnTimeout                                      (node:internal/timers:545)
     1.7  |   12031.5  |      1  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5047)
    28.5  |   11470.2  |     16  | protoOf.j1a                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-core.js:1528)
     3.6  |   10698.9  |      2  | protoOf.r8                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4916)
     1.6  |    7680.2  |      1  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1432)
     0.0  |    7678.6  |      0  | l                                                  (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1474)
     0.0  |    7678.6  |      0  | protoOf.y3r                                        (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1423)
     3.3  |    7676.8  |      2  | protoOf.z3r                                        (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7232)
     4.2  |    7673.6  |      3  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:6987)
  2799.1  |    7438.9  |   1674  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7432)
     0.0  |    7330.8  |      0  | (anonymous)                                        (.../packages/comparison-otel/kotlin/comparison-otel.js:0)
    38.7  |    5159.4  |     22  | protoOf.x16                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-core.js:1912)
    57.8  |    5120.7  |     35  | protoOf.s1b                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-core.js:1896)
     1.6  |    4624.4  |      1  | buildObject                                        (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7329)
   403.7  |    4622.8  |    245  | (anonymous)                                        (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7343)
     0.0  |    4272.1  |      0  | l                                                  (.../packages/comparison-otel/kotlin/otlp-exporter.js:238)
     3.3  |    4272.1  |      2  | protoOf.r21                                        (.../packages/comparison-otel/kotlin/otlp-exporter.js:123)
     2.9  |    4267.1  |      3  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/otlp-exporter.js:132)
  4219.1  |    4219.1  |   2544  | toTypedArray                                       (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2924)
     1.7  |    4043.1  |      1  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-utils.js:1370)
     0.0  |    3898.5  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |    3898.5  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 22 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=1.6 ms, total=101.4 ms, hits=1

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
          1.6  |             1.6   |       1  | coarsenedSharedCurrentTime  (node:internal/deps/undici/undici:5543)
          0.0  |            93.4   |       0  | protoOf.e1j  (.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:559)
          0.0  |             6.4   |       0  | protoOf.o8  (.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:5748)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 1.6ms]
  l@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1474
    protoOf.y3r@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1423
      protoOf.o8@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1432
        protoOf.z3r@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7232
          protoOf.o8@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:6987
            commonFetch@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7731
              fetch@node:internal/bootstrap/web/exposed-window-or-worker:77
                fetch@node:internal/deps/undici/undici:17405
                  fetch2@node:internal/deps/undici/undici:12751
                    fetching@node:internal/deps/undici/undici:12870
                      coarsenedSharedCurrentTime@node:internal/deps/undici/undici:5543
                        now@:-1

[self NaNms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:108
                    protoOf.e1j@.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:77
                      protoOf.e1j@.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:559
                        instant@.../node_modules/@js-joda/core/dist/js-joda.js:12814

[self NaNms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:108
                    protoOf.e1j@.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:77
                      protoOf.e1j@.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:559
                        instant@.../node_modules/@js-joda/core/dist/js-joda.js:12814

[self NaNms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                  _endSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:116
                    protoOf.e1j@.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:77
                      protoOf.e1j@.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:559
                        instant@.../node_modules/@js-joda/core/dist/js-joda.js:12814

[self NaNms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:108
                    protoOf.e1j@.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:77
                      protoOf.e1j@.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:559
                        instant@.../node_modules/@js-joda/core/dist/js-joda.js:12814
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 83 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=859.9 ms, total=1690.5 ms, hits=499

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        831.0  |          1290.1   |     482  | removeAll  (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647)
         25.3  |            82.0   |      15  | protoOf.l1p  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606)
          3.4  |           312.6   |       2  | (anonymous)  (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:197)
          0.2  |             0.2   |       0  | protoOf.o8  (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:5649)
          0.0  |             1.0   |       0  | protoOf.d24  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-common.js:527)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 156.6ms]
  dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
    resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
      resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
        protoOf.r8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4916
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4861
            protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268
              exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1083
                protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1472
                  protoOf.u1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                    protoOf.l1s@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:976
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 103.2ms]
  dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
    resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
      resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
        protoOf.r8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4916
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4861
            protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268
              exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1083
                protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1472
                  protoOf.u1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                    protoOf.l1s@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:976
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 102.6ms]
  dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
    resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
      resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
        protoOf.r8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4916
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4861
            protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268
              exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1083
                protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1472
                  protoOf.u1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                    protoOf.l1s@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:976
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 100.2ms]
  dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
    resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
      resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
        protoOf.r8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4916
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4861
            protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268
              exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1083
                protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1472
                  protoOf.u1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                    protoOf.l1s@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:976
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 59.8ms]
  resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
    protoOf.r8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4916
      protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4861
        protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268
          exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1083
            protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1472
              removeSpanDataFromBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1061
                protoOf.t1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:207
                  protoOf.u1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                    protoOf.l1s@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:976
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 548 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=895.4 ms, total=2967.8 ms, hits=543

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        270.2  |           565.9   |     160  | compare  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1667)
        133.1  |           526.1   |      92  | toStringImpl  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1688)
        126.5  |           421.7   |      74  | multiply  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1755)
        109.6  |           429.4   |      67  | divide  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1877)
         97.1  |           448.3   |      57  | modulo  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1969)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 79.2ms]
  (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
    protoOf.s14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
      protoOf.pq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
        protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4861
          protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5047
            l@.../packages/comparison-otel/kotlin/otlp-exporter.js:238
              protoOf.r21@.../packages/comparison-otel/kotlin/otlp-exporter.js:123
                protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:132
                  serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:268
                    protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1407
                      toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1688
                        divide@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1877

[self 34.1ms]
  (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
    protoOf.s14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
      protoOf.pq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
        protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4861
          protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5047
            l@.../packages/comparison-otel/kotlin/otlp-exporter.js:238
              protoOf.r21@.../packages/comparison-otel/kotlin/otlp-exporter.js:123
                protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:132
                  serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:268
                    protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1407
                      toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1688
                        multiply@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1755

[self 33.3ms]
  protoOf.s14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
    protoOf.pq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
      protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4861
        protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5047
          l@.../packages/comparison-otel/kotlin/otlp-exporter.js:238
            protoOf.r21@.../packages/comparison-otel/kotlin/otlp-exporter.js:123
              protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:132
                serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:268
                  protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1407
                    toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1688
                      divide@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1877
                        multiply@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1755

[self 22.8ms]
  protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4861
    protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5047
      l@.../packages/comparison-otel/kotlin/otlp-exporter.js:238
        protoOf.r21@.../packages/comparison-otel/kotlin/otlp-exporter.js:123
          protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:132
            serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:268
              protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1407
                toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1688
                  divide@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1877
                    greaterThanOrEqual@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1965
                      compare@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1667
                        subtract@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1743

[self 17.9ms]
  protoOf.pq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
    protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4861
      protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5047
        l@.../packages/comparison-otel/kotlin/otlp-exporter.js:238
          protoOf.r21@.../packages/comparison-otel/kotlin/otlp-exporter.js:123
            protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:132
              serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:268
                protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1407
                  toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1688
                    divide@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1877
                      multiply@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1755
                        lessThan@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1869
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 8 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=18.7 ms, total=28.6 ms, hits=10

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         18.7  |            20.3   |      10  | protoOf.b27  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468)
          0.0  |             1.6   |       0  | protoOf.y2e  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1612)
          0.0  |             6.7   |       0  | protoOf.x1k  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1674)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 8.6ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:108
                    protoOf.l1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606
                      protoOf.b27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:495

[self 3.4ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:108
                    protoOf.l1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606
                      protoOf.b27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:495

[self 3.4ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:108
                    protoOf.l1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606
                      protoOf.b27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:495

[self 1.7ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:108
                    protoOf.l1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606
                      protoOf.b27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:495

[self 1.6ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:21
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:108
                    protoOf.l1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606
                      protoOf.b27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:495
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``otel.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

