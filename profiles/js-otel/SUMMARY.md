# Profile -- otel (JSON/HTTP) (js)

**Variant:** `otel`  
**Platform:** js  
**SUMMARY rendered:** 2026-05-20 20:42:16  
**Profile file last captured:** 2026-05-20 20:42:16  
**Profile file:** [otel.cpuprofile](otel.cpuprofile)  
**Wall time (capture run):** 37902 ms (incl. profiler overhead)  
**Workload-reported time:** 37521 ms  

---

## Top 30 frames

```
Profile: otel.cpuprofile
Wall: 37647.6 ms total, 9543 nodes, 23597 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
 12132.7  |   16845.0  |   7575  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7432)
  4279.0  |    4279.0  |   2677  | toTypedArray                                       (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2883)
  1992.9  |    2724.5  |   1228  | recyclableRemoveAll                                (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774)
  1385.3  |    2102.0  |    840  | encodeUtf8                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:6568)
  1289.2  |    1289.2  |    799  | (garbage collector)                                (:-1)
  1092.2  |    1092.2  |    668  | charCodeAt                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2008)
  1054.9  |    1060.8  |    650  | add                                                (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1797)
   884.5  |    1590.8  |    528  | printQuoted                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:845)
   873.3  |    2529.2  |    531  | protoOf.x19                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:800)
   658.3  |    1607.2  |    407  | subtract                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1702)
   649.8  |     669.6  |    399  | equals                                             (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2171)
   578.2  |     578.2  |    329  | (idle)                                             (:-1)
   575.5  |    2573.3  |    356  | divide                                             (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1836)
   501.6  |    3238.9  |    316  | removeAll                                          (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647)
   407.2  |    4686.2  |    256  | (anonymous)                                        (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7343)
   399.3  |     399.3  |    242  | _init_properties_StringOps_kt__fcy1db              (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:872)
   335.4  |    1595.2  |    208  | multiply                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1714)
   299.0  |   30968.8  |    182  | protoOf.l1a                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:561)
   282.8  |    1388.8  |    171  | protoOf.m19                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:650)
   274.0  |     964.3  |    169  | lessThan                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1828)
   264.1  |     264.1  |    163  | equalsLong                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1686)
   196.5  |     196.5  |    458  | (program)                                          (:-1)
   172.0  |     172.0  |    108  | toNumber                                           (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1643)
   164.6  |     234.6  |     98  | encodeTypeInfo                                     (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:530)
   157.7  |     157.7  |     98  | millis                                             (.../node_modules/@js-joda/core/dist/js-joda.js:12810)
   154.7  |     156.3  |     95  | fromNumber                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1778)
   151.2  |    1078.5  |     96  | protoOf.m1p                                        (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606)
   150.3  |     150.3  |     92  | captureStack                                       (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:2349)
   142.1  |     146.8  |     86  | protoOf.u1m                                        (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-api-all.js:1391)
   141.6  |   11723.3  |     89  | protoOf.t1b                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-core.js:1896)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
    61.9  |   51856.7  |     39  | fibonacci                                          (.../packages/comparison-otel/kotlin/comparison-otel.js:25)
     0.0  |   37647.4  |      0  | (root)                                             (:-1)
    30.6  |   32120.9  |     19  | protoOf.n8                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820)
   299.0  |   30968.8  |    182  | protoOf.l1a                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-json.js:561)
     5.3  |   26839.1  |      3  | processTimers                                      (node:internal/timers:525)
    12.1  |   26833.6  |      6  | protoOf.qq                                         (.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400)
     1.7  |   26832.5  |      1  | listOnTimeout                                      (node:internal/timers:545)
     1.6  |   26828.0  |      1  | (anonymous)                                        (.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485)
     1.7  |   26826.4  |      1  | protoOf.t14                                        (.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662)
     0.0  |   26503.9  |      0  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006)
    85.7  |   23609.1  |     53  | protoOf.k1a                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-core.js:1528)
     0.0  |   17284.9  |      0  | l                                                  (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1474)
    21.1  |   17284.8  |     13  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:6987)
     1.7  |   17283.5  |      1  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1432)
 12132.7  |   16845.0  |   7575  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7432)
     0.0  |   15681.2  |      0  | (anonymous)                                        (.../packages/comparison-otel/kotlin/comparison-otel.js:0)
     3.6  |   13022.8  |      2  | protoOf.a3s                                        (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7232)
    83.6  |   11806.9  |     52  | protoOf.y16                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-core.js:1912)
   141.6  |   11723.3  |     89  | protoOf.t1b                                        (.../packages/comparison-otel/kotlin/kotlinx-serialization-kotlinx-serialization-core.js:1896)
     0.0  |    9174.4  |      0  | l                                                  (.../packages/comparison-otel/kotlin/otlp-exporter.js:238)
     5.1  |    9169.1  |      3  | protoOf.o8                                         (.../packages/comparison-otel/kotlin/otlp-exporter.js:132)
     0.0  |    8493.2  |      0  | protoOf.z3r                                        (.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1423)
     0.0  |    8061.4  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |    8061.4  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     0.0  |    8056.6  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |    8056.6  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |    8045.3  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     0.0  |    7885.3  |      0  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |    7884.1  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     0.0  |    7774.6  |      0  | mainWrapper                                        (.../packages/comparison-otel/kotlin/comparison-otel.js:147)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 42 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=15.4 ms, total=225.3 ms, hits=10

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
          5.8  |             5.8   |       4  | insert  (node:internal/timers:386)
          3.2  |             3.2   |       2  | fetching  (node:internal/deps/undici/undici:12870)
          3.2  |            18.8   |       2  | _create  (.../node_modules/@js-joda/core/dist/js-joda.js:12370)
          1.6  |             3.2   |       1  | protoOf.qc  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:7055)
          1.6  |             9.7   |       1  | protoOf.o8  (.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:5748)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 3.2ms]
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

[self 1.6ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
            _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:135
              protoOf.f1j@.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:77
                protoOf.f1j@.../packages/comparison-otel/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:559
                  instant@.../node_modules/@js-joda/core/dist/js-joda.js:12814
                    ofEpochMilli@.../node_modules/@js-joda/core/dist/js-joda.js:12350
                      _create@.../node_modules/@js-joda/core/dist/js-joda.js:12370
                        Instant@.../node_modules/@js-joda/core/dist/js-joda.js:12388

[self 1.6ms]
  protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
    protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
      protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
        protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
          l@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1474
            protoOf.o8@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1432
              protoOf.a3s@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7232
                protoOf.o8@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:6987
                  commonFetch@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7731
                    fetch2@node:internal/deps/undici/undici:12751
                      fetching@node:internal/deps/undici/undici:12870
                        now@:-1

[self 1.6ms]
  (anon)@node:internal/modules/cjs/loader:1238
    (anon)@node:internal/modules/cjs/loader:1537
      (anon)@node:internal/modules/cjs/loader:1925
        (anon)@node:internal/modules/cjs/loader:1781
          (anon)@.../packages/comparison-otel/kotlin/comparison-otel.js:0
            (anon)@.../packages/comparison-otel/kotlin/comparison-otel.js:0
              mainWrapper@.../packages/comparison-otel/kotlin/comparison-otel.js:147
                main@.../packages/comparison-otel/kotlin/comparison-otel.js:78
                  ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:11007
                    protoOf.qc@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:7016
                      protoOf.qc@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:7055
                        hrtime@node:internal/process/per_thread:77

[self 1.6ms]
  (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
    protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
      protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
        protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
          protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
            l@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1474
              protoOf.o8@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:1432
                protoOf.o8@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:6987
                  commonFetch@.../packages/comparison-otel/kotlin/ktor-ktor-client-ktor-client-core.js:7731
                    fetch2@node:internal/deps/undici/undici:12751
                      fetching@node:internal/deps/undici/undici:12870
                        now@:-1
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 149 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=2102.4 ms, total=3542.7 ms, hits=1296

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
       1992.9  |          2724.5   |    1228  | removeAll  (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647)
         93.2  |           161.1   |      58  | protoOf.o8  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268)
          8.2  |            26.7   |       5  | protoOf.m1p  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606)
          6.4  |           599.0   |       4  | (anonymous)  (.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:197)
          1.5  |             2.1   |       1  | awaitContent$default  (.../packages/comparison-otel/kotlin/ktor-ktor-io.js:117)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 226.4ms]
  dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
    resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
      resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
        protoOf.r8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4875
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
            protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268
              exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1083
                protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1472
                  protoOf.v1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                    protoOf.m1s@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:976
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 219.7ms]
  updateCellSend@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4103
    tryResumeReceiver@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4218
      dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
        resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
          resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
            protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
              protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268
                exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1083
                  protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1472
                    protoOf.v1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 181.5ms]
  updateCellSend@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4103
    tryResumeReceiver@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:4218
      dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
        resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
          resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
            protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
              protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268
                exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1083
                  protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1472
                    protoOf.v1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 162.3ms]
  dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
    resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
      resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
        protoOf.r8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4875
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
            protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268
              exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1083
                protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1472
                  protoOf.v1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                    protoOf.m1s@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:976
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774

[self 142.0ms]
  dispatch@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7458
    resumeUnconfined@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7495
      resume@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7519
        protoOf.r8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4875
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
            protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268
              exportCurrentBatch@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1083
                protoOf.o8@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1472
                  protoOf.v1r@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:328
                    protoOf.m1s@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:976
                      removeAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:647
                        recyclableRemoveAll@.../packages/comparison-otel/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:774
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 1061 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=2145.1 ms, total=7041.8 ms, hits=1327

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        647.3  |          1431.9   |     400  | compare  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1626)
        355.7  |          1433.3   |     218  | toStringImpl  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647)
        344.2  |          1034.6   |     213  | multiply  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1714)
        257.5  |           943.8   |     159  | divide  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1836)
        227.5  |          1041.7   |     143  | modulo  (.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1928)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 102.9ms]
  (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
    protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
      protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
        protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
          protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
            l@.../packages/comparison-otel/kotlin/otlp-exporter.js:238
              protoOf.s21@.../packages/comparison-otel/kotlin/otlp-exporter.js:123
                protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:132
                  serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:268
                    protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1366
                      toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647
                        divide@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1836

[self 101.8ms]
  listOnTimeout@node:internal/timers:545
    (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
      protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
        protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
            protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
              l@.../packages/comparison-otel/kotlin/otlp-exporter.js:238
                protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:132
                  serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:268
                    protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1366
                      toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647
                        divide@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1836

[self 41.0ms]
  protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
    protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
      protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
        l@.../packages/comparison-otel/kotlin/otlp-exporter.js:238
          protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:132
            serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:268
              protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1366
                toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647
                  divide@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1836
                    greaterThanOrEqual@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1924
                      compare@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1626
                        subtract@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1702

[self 40.6ms]
  listOnTimeout@node:internal/timers:545
    (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
      protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
        protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
          protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
            protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
              l@.../packages/comparison-otel/kotlin/otlp-exporter.js:238
                protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:132
                  serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:268
                    protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1366
                      toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647
                        multiply@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1714

[self 38.1ms]
  (anon)@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8485
    protoOf.t14@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:8662
      protoOf.qq@.../packages/comparison-otel/kotlin/kotlinx-coroutines-core.js:7400
        protoOf.n8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:4820
          protoOf.o8@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:5006
            l@.../packages/comparison-otel/kotlin/otlp-exporter.js:238
              protoOf.s21@.../packages/comparison-otel/kotlin/otlp-exporter.js:123
                protoOf.o8@.../packages/comparison-otel/kotlin/otlp-exporter.js:132
                  serialize@.../packages/comparison-otel/kotlin/otlp-exporter.js:268
                    protoOf.toString@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1366
                      toStringImpl@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1647
                        multiply@.../packages/comparison-otel/kotlin/kotlin-kotlin-stdlib.js:1714
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 12 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=18.0 ms, total=29.0 ms, hits=12

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         16.9  |            18.5   |      11  | protoOf.c27  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468)
          1.1  |             1.1   |       1  | BatchSpanProcessor$Worker$run$slambda_0  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1210)
          0.0  |             1.6   |       0  | protoOf.z2e  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1612)
          0.0  |             6.6   |       0  | protoOf.y1k  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1674)
          0.0  |             1.1   |       0  | protoOf.o8  (.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1268)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 7.5ms]
  fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:135
                    protoOf.m1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606
                      protoOf.c27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:495

[self 1.6ms]
  workload@.../packages/comparison-otel/kotlin/comparison-otel.js:66
    fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:135
                    protoOf.m1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606
                      protoOf.c27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:495

[self 1.6ms]
  mainWrapper@.../packages/comparison-otel/kotlin/comparison-otel.js:147
    main@.../packages/comparison-otel/kotlin/comparison-otel.js:78
      fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
        fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
          fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
            fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:135
                    protoOf.m1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606
                      protoOf.c27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:495

[self 1.6ms]
  (anon)@node:internal/modules/cjs/loader:1925
    (anon)@node:internal/modules/cjs/loader:1781
      (anon)@.../packages/comparison-otel/kotlin/comparison-otel.js:0
        (anon)@.../packages/comparison-otel/kotlin/comparison-otel.js:0
          mainWrapper@.../packages/comparison-otel/kotlin/comparison-otel.js:147
            main@.../packages/comparison-otel/kotlin/comparison-otel.js:78
              workload@.../packages/comparison-otel/kotlin/comparison-otel.js:66
                bubbleSort@.../packages/comparison-otel/kotlin/comparison-otel.js:36
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:135
                    protoOf.m1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606
                      protoOf.c27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:495

[self 1.6ms]
  (anon)@node:internal/modules/cjs/loader:1781
    (anon)@.../packages/comparison-otel/kotlin/comparison-otel.js:0
      (anon)@.../packages/comparison-otel/kotlin/comparison-otel.js:0
        mainWrapper@.../packages/comparison-otel/kotlin/comparison-otel.js:147
          main@.../packages/comparison-otel/kotlin/comparison-otel.js:78
            workload@.../packages/comparison-otel/kotlin/comparison-otel.js:66
              fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
                fibonacci@.../packages/comparison-otel/kotlin/comparison-otel.js:25
                  _startSpan@.../packages/comparison-otel/kotlin/comparison-otel.js:135
                    protoOf.m1p@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:606
                      protoOf.c27@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:468
                        RecordEventsReadableSpan@.../packages/comparison-otel/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:495
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``otel.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

