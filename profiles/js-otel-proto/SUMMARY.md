# Profile -- otel-proto (Protobuf/gRPC) (js)

**Variant:** `otel-proto`  
**Platform:** js  
**SUMMARY rendered:** 2026-05-05 22:27:41  
**Profile file last captured:** 2026-05-05 22:27:41  
**Profile file:** [otel-proto.cpuprofile](otel-proto.cpuprofile)  
**Wall time (capture run):** 9188 ms (incl. profiler overhead)  
**Workload-reported time:** 8879 ms  

---

## Top 30 frames

```
Profile: otel-proto.cpuprofile
Wall: 9021.0 ms total, 6680 nodes, 5526 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
   806.5  |    1041.4  |    480  | protoOf.e2                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:7197)
   659.0  |     659.0  |    387  | toTypedArray                                       (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:2797)
   543.7  |     543.7  |    292  | (idle)                                             (:-1)
   462.2  |    1205.3  |    269  | protoOf.o8                                         (.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:8987)
   374.8  |     374.8  |    220  | (garbage collector)                                (:-1)
   338.9  |     372.5  |    201  | equals                                             (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:2231)
   338.4  |     338.4  |    201  | arrayCopy                                          (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:3081)
   301.0  |     307.9  |    177  | add                                                (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1818)
   203.1  |    1265.1  |    121  | protoOf.c2                                         (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216)
   185.4  |     425.9  |    107  | subtract                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1723)
   184.7  |     184.7  |    113  | captureStack                                       (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:2370)
   146.2  |     661.6  |     88  | divide                                             (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1857)
   132.4  |     274.2  |     76  | writeVarUInt64                                     (.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1208)
   120.2  |     121.9  |     71  | bitwiseAnd                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1953)
   104.5  |     486.4  |     62  | multiply                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1735)
    96.8  |      96.8  |    151  | (program)                                          (:-1)
    94.5  |     467.3  |     55  | toProto                                            (.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3810)
    91.1  |      91.1  |     54  | millis                                             (.../node_modules/@js-joda/core/dist/js-joda.js:12810)
    88.6  |      91.8  |     54  | internalConnectMultiple                            (node:net:1176)
    78.6  |     271.4  |     46  | lessThan                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1849)
    73.9  |     475.6  |     45  | protoOf.q4r                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:596)
    67.7  |     726.7  |     40  | (anonymous)                                        (.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:8898)
    67.6  |     184.0  |     41  | get_isValid                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:102)
    66.4  |      66.4  |     39  | charCodeAt                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:2029)
    66.2  |    1192.5  |     35  | protoOf.y4c                                        (.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3073)
    63.2  |      65.0  |     37  | protoOf.w4o                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:1440)
    62.4  |      62.4  |     37  | wrapSafe                                           (node:internal/modules/cjs/loader:1720)
    59.2  |      59.2  |     36  | utf8Size                                           (.../packages/comparison-otel-proto/kotlin/okio-parent-okio.js:16)
    57.3  |      59.0  |     35  | shiftRightUnsigned                                 (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1965)
    51.9  |      51.9  |     32  | toNumber                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1664)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
    29.0  |   71188.8  |     13  | fibonacci                                          (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21)
     0.0  |    9020.8  |      0  | (root)                                             (:-1)
     0.0  |    8249.3  |      0  | (anonymous)                                        (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:0)
    12.1  |    6287.2  |      7  | protoOf.n8                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4733)
     0.0  |    4410.6  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |    4410.6  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     0.0  |    4405.5  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |    4405.5  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |    4394.3  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     0.0  |    4176.5  |      0  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |    4175.1  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     0.0  |    4050.0  |      0  | mainWrapper                                        (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:120)
     3.5  |    4050.0  |      2  | main                                               (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:76)
     0.0  |    4033.4  |      0  | workload                                           (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:62)
    10.6  |    3977.6  |      7  | protoOf.r8                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4788)
    19.8  |    3843.2  |     12  | protoOf.k4a                                        (.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314)
    21.7  |    3831.5  |     12  | protoOf.j4a                                        (.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303)
     0.0  |    3380.1  |      0  | processTimers                                      (node:internal/timers:525)
     5.0  |    3378.0  |      3  | protoOf.oq                                         (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9144)
     3.6  |    3377.5  |      3  | (anonymous)                                        (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:10875)
     1.6  |    3375.2  |      1  | listOnTimeout                                      (node:internal/timers:545)
     0.7  |    3374.0  |      0  | protoOf.v1g                                        (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:11052)
     3.3  |    3151.6  |      2  | protoOf.r5g                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1553)
    18.2  |    3148.6  |     11  | protoOf.p12                                        (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6255)
    10.0  |    2981.6  |      5  | _endSpan                                           (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:116)
     0.0  |    2905.3  |      0  | protoOf.n4s                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:517)
     0.0  |    2833.4  |      0  | protoOf.o8                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4919)
    10.3  |    2804.0  |      6  | updateCellSend                                     (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4423)
     0.0  |    2789.9  |      0  | protoOf.x57                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1619)
    17.0  |    2786.5  |     10  | end                                                (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:149)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 30 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=8.4 ms, total=123.1 ms, hits=5

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
          3.3  |             3.3   |       2  | coarsenedSharedCurrentTime  (node:internal/deps/undici/undici:5543)
          1.7  |             1.7   |       1  | protoOf.y4c  (.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3073)
          1.7  |             1.7   |       1  | _create  (.../node_modules/@js-joda/core/dist/js-joda.js:12370)
          1.6  |             1.6   |       1  | insert  (node:internal/timers:386)
          0.0  |             3.5   |       0  | protoOf.o8  (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6068)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 1.7ms]
  serialize@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:257
    protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:345
      protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314
        protoOf.j4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
          protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1790
            protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314
              protoOf.j4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
                protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1977
                  protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314
                    protoOf.j4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
                      protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3073
                        writeUnknownFields@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:180

[self 1.7ms]
  l@.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:1583
    protoOf.r37@.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:1532
      protoOf.o8@.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:1541
        protoOf.s37@.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:8783
          protoOf.o8@.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:8527
            commonFetch@.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:9288
              fetch@node:internal/bootstrap/web/exposed-window-or-worker:77
                fetch@node:internal/deps/undici/undici:17405
                  fetch2@node:internal/deps/undici/undici:12751
                    fetching@node:internal/deps/undici/undici:12870
                      coarsenedSharedCurrentTime@node:internal/deps/undici/undici:5543
                        now@:-1

[self 1.7ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
            _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:108
              protoOf.t4l@.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:54
                protoOf.t4l@.../packages/comparison-otel-proto/kotlin/Kotlin-DateTime-library-kotlinx-datetime.js:277
                  instant@.../node_modules/@js-joda/core/dist/js-joda.js:12814
                    ofEpochMilli@.../node_modules/@js-joda/core/dist/js-joda.js:12350
                      _create@.../node_modules/@js-joda/core/dist/js-joda.js:12370
                        Instant@.../node_modules/@js-joda/core/dist/js-joda.js:12388

[self 1.6ms]
  (root)@:-1
    onlookupall@node:dns:119
      emitLookup@node:net:1504
        defaultTriggerAsyncIdScope@node:internal/async_hooks:462
          internalConnectMultiple@node:net:1176
            setTimeout@node:timers:114
              insert@node:internal/timers:386
                getLibuvNow@:-1

[self 1.6ms]
  emit@node:events:455
    onceWrapper@node:events:624
      (anon)@node:internal/deps/undici/undici:3261
        (anon)@node:internal/deps/undici/undici:9305
          Client.<computed>@node:internal/deps/undici/undici:9178
            resume@node:internal/deps/undici/undici:9387
              _resume@node:internal/deps/undici/undici:9401
                write@node:internal/deps/undici/undici:7630
                  writeH1@node:internal/deps/undici/undici:7767
                    onConnect@node:internal/deps/undici/undici:13628
                      coarsenedSharedCurrentTime@node:internal/deps/undici/undici:5543
                        now@:-1
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 162 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=1016.5 ms, total=3864.0 ms, hits=605

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        806.5  |          1041.4   |     480  | protoOf.c2  (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216)
        203.1  |          1265.1   |     121  | (anonymous)  (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203)
          5.1  |          1393.1   |       3  | removeAll  (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
          1.8  |             6.8   |       1  | contentType  (.../packages/comparison-otel-proto/kotlin/ktor-ktor-http.js:1438)
          0.0  |            94.7   |       0  | protoOf.w26  (.../packages/comparison-otel-proto/kotlin/ktor-ktor-utils.js:1781)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 145.2ms]
  protoOf.r8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4788
    protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4733
      protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1256
        exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1071
          protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1460
            protoOf.z4t@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.q4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:7197

[self 104.0ms]
  protoOf.r8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4788
    protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4733
      protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1256
        exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1071
          protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1460
            protoOf.z4t@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.q4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:7197

[self 89.5ms]
  protoOf.r8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4788
    protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4733
      protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1256
        exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1071
          protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1460
            protoOf.z4t@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.q4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:7197

[self 87.7ms]
  protoOf.r8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4788
    protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4733
      protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1256
        exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1071
          protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1460
            protoOf.z4t@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.q4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:7197

[self 54.1ms]
  protoOf.r8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4788
    protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4733
      protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1256
        exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1071
          protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1460
            protoOf.z4t@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
              protoOf.q4u@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:989
                removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                  recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
                    (anon)@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203
                      protoOf.c2@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216
                        protoOf.e2@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:7197
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 586 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=686.1 ms, total=2018.4 ms, hits=405

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        178.7  |           370.5   |     104  | compare  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1647)
         90.2  |           460.0   |      55  | modulo  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1949)
         90.2  |           283.0   |      53  | multiply  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1735)
         68.2  |           271.0   |      39  | divide  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1857)
         38.4  |            38.4   |      23  | writeVarUInt64  (.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1208)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 18.2ms]
  protoOf.j4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
    protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1977
      protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314
        protoOf.j4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
          protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3073
            protoOf.h4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1286
              protoOf.s4c@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                writeULongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                  writeLongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                    protoOf.a1l@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:551
                      protoOf.b1l@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1118
                        bitwiseAnd@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1953

[self 11.4ms]
  protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314
    protoOf.j4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
      protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1977
        protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314
          protoOf.j4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
            protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3073
              protoOf.h4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1286
                protoOf.s4c@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                  writeULongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                    writeLongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                      reverseBytes_0@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:2252
                        bitwiseAnd@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1953

[self 10.1ms]
  tryResume0@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6772
    protoOf.eo@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:1241
      dispatchResume@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:867
        dispatch@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9202
          resumeUnconfined@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9239
            resume@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9263
              protoOf.r8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4788
                protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4733
                  protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1256
                    protoOf.z12@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6589
                      protoOf.x10@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6596
                        divide@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1857

[self 8.6ms]
  protoOf.j4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
    protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1977
      protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314
        protoOf.j4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
          protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3073
            protoOf.h4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1286
              protoOf.s4c@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                writeULongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                  writeLongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                    protoOf.a1l@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:551
                      protoOf.b1l@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1118
                        bitwiseAnd@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1953

[self 8.5ms]
  protoOf.j4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
    protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1977
      protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314
        protoOf.j4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
          protoOf.y4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3073
            protoOf.h4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1286
              protoOf.s4c@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                writeULongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                  writeLongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                    protoOf.a1l@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:551
                      protoOf.b1l@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1118
                        bitwiseAnd@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1953
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 8 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=11.7 ms, total=20.4 ms, hits=7

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         11.7  |            13.5   |       7  | protoOf.y58  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:458)
          0.0  |             1.7   |       0  | protoOf.t5g  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1600)
          0.0  |             5.2   |       0  | protoOf.i2a  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1662)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 3.5ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:108
                    protoOf.q4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:596
                      protoOf.y58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:458
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:485

[self 3.3ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:108
                    protoOf.q4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:596
                      protoOf.y58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:458
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:485

[self 1.7ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:108
                    protoOf.q4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:596
                      protoOf.y58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:458
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:485

[self 1.6ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:108
                    protoOf.q4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:596
                      protoOf.y58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:458
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:485

[self 1.6ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:21
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:108
                    protoOf.q4r@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:596
                      protoOf.y58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:458
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:485
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``otel-proto.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

