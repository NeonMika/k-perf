# Profile -- otel-proto (Protobuf/gRPC) (js)

**Variant:** `otel-proto`  
**Platform:** js  
**SUMMARY rendered:** 2026-06-13 17:03:07  
**Profile file last captured:** 2026-06-13 17:03:06  
**Profile file:** [otel-proto.cpuprofile](otel-proto.cpuprofile)  
**Wall time (capture run):** 45376 ms (incl. profiler overhead)  
**Workload-reported time:** 44826 ms  

---

## Top 30 frames

```
Profile: otel-proto.cpuprofile
Wall: 44964.0 ms total, 11186 nodes, 26539 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
  5490.9  |    6419.2  |   3343  | protoOf.o8                                         (.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:8987)
  5326.1  |    6344.9  |   2986  | recyclableRemoveAll                                (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787)
  4837.7  |    4837.7  |   2705  | (idle)                                             (:-1)
  3633.5  |    3633.5  |   2229  | (garbage collector)                                (:-1)
  2552.8  |    2552.8  |   1437  | arrayCopy                                          (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:3040)
  1182.5  |    1197.0  |    682  | add                                                (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1777)
  1166.3  |    7564.1  |    657  | removeAll                                          (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
   950.5  |    1083.0  |    551  | equals                                             (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:2190)
   809.8  |    3175.3  |    457  | divide                                             (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1816)
   801.6  |    1712.7  |    465  | subtract                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1682)
   768.2  |     768.2  |    469  | toTypedArray                                       (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:2756)
   654.8  |    1147.6  |    358  | writeVarUInt64                                     (.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1208)
   552.1  |     574.8  |    319  | protoOf.d4w                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:1440)
   521.6  |     521.6  |    312  | millis                                             (.../node_modules/@js-joda/core/dist/js-joda.js:12810)
   460.3  |    1240.0  |    267  | get_isValid                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:102)
   453.9  |    1828.9  |    275  | multiply                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1694)
   452.0  |    1872.9  |    265  | toProto                                            (.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3827)
   408.0  |     408.0  |    255  | captureStack                                       (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:2329)
   404.8  |     408.0  |    232  | bitwiseAnd                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1912)
   387.6  |     396.3  |    244  | readableByteStreamControllerEnqueue                (node:internal/webstreams/readablestream:2872)
   380.1  |     380.1  |   1703  | (program)                                          (:-1)
   327.3  |     343.7  |    194  | equalsLong                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1666)
   310.2  |     986.9  |    181  | lessThan                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1808)
   304.3  |     304.3  |    182  | charCodeAt                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1988)
   261.8  |    6182.6  |    149  | protoOf.z4c                                        (.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3078)
   259.9  |    2233.1  |    151  | protoOf.w4y                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642)
   232.2  |     236.7  |    139  | utf8Size                                           (.../packages/comparison-otel-proto/kotlin/okio-parent-okio.js:16)
   226.1  |    1849.7  |    129  | compare                                            (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1606)
   216.0  |     216.0  |    130  | toNumber                                           (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1623)
   194.3  |    2119.8  |    113  | protoOf.o8                                         (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6068)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
   141.3  |  236581.3  |     83  | fibonacci                                          (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27)
     0.0  |   44963.6  |      0  | (root)                                             (:-1)
     0.0  |   34052.2  |      0  | (anonymous)                                        (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:0)
    60.5  |   29857.0  |     34  | protoOf.n8                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692)
    76.1  |   17963.0  |     36  | processTimers                                      (node:internal/timers:525)
    19.4  |   17871.1  |     10  | listOnTimeout                                      (node:internal/timers:545)
    29.4  |   17866.5  |     15  | protoOf.w1g                                        (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:11052)
    26.6  |   17837.3  |     13  | protoOf.pq                                         (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9144)
     0.0  |   17304.5  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     1.6  |   17303.0  |      1  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     0.0  |   17296.4  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     1.7  |   17296.4  |      1  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |   17284.9  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     0.0  |   17076.0  |      0  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |   17074.7  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     1.7  |   16954.3  |      1  | mainWrapper                                        (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:157)
     1.6  |   16952.6  |      1  | main                                               (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:83)
     0.0  |   16927.7  |      0  | workload                                           (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:68)
     7.1  |   16848.3  |      4  | protoOf.o8                                         (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4878)
     1.3  |   13619.2  |      1  | _endSpan                                           (.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:153)
     6.4  |   13408.1  |      3  | (anonymous)                                        (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:10875)
    67.3  |   13316.2  |     41  | end                                                (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:149)
    93.1  |   13098.7  |     53  | protoOf.q12                                        (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6255)
     0.0  |   13090.1  |      0  | protoOf.k5h                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1626)
     1.6  |   13087.8  |      1  | protoOf.t4z                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:563)
     1.5  |   12909.1  |      1  | protoOf.p58                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1692)
     1.7  |   12753.2  |      1  | endInternal                                        (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:470)
    30.6  |   11896.5  |     16  | updateCellSend                                     (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4423)
     1.9  |   11831.1  |      1  | tryResumeReceiver                                  (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4538)
     3.3  |   11823.0  |      2  | dispatch                                           (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9202)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 39 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=35.8 ms, total=618.0 ms, hits=21

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         19.1  |            19.1   |      12  | protoOf.z4c  (.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3078)
          6.9  |             6.9   |       3  | fetching  (node:internal/deps/undici/undici:12870)
          6.6  |            13.5   |       4  | protoOf.o8  (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6068)
          1.7  |             1.7   |       1  | onResponseStarted  (node:internal/deps/undici/undici:13639)
          1.5  |            11.3   |       1  | protoOf.o8  (.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4082)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 9.5ms]
  protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692
    protoOf.o8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4878
      l@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:8060
        protoOf.o8@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:8022
          l@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:2455
            protoOf.o8@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:2267
              serialize@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:257
                protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
                  protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1795
                    protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1982
                      protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3078
                        writeUnknownFields@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:180

[self 7.9ms]
  serialize@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:257
    protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:350
      protoOf.l4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314
        protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
          protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1795
            protoOf.l4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314
              protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
                protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1982
                  protoOf.l4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1314
                    protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
                      protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3078
                        writeUnknownFields@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:180

[self 5.5ms]
  (anon)@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:10875
    protoOf.w1g@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:11052
      protoOf.pq@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9144
        protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692
          protoOf.o8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4878
            l@.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:1583
              protoOf.o8@.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:1541
                protoOf.o8@.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:8527
                  commonFetch@.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:9288
                    fetch2@node:internal/deps/undici/undici:12751
                      fetching@node:internal/deps/undici/undici:12870
                        now@:-1

[self 1.7ms]
  (root)@:-1
    processTicksAndRejections@node:internal/process/task_queues:71
      emitReadable_@node:internal/streams/readable:831
        emit@node:events:455
          onHttpSocketReadable@node:internal/deps/undici/undici:7689
            execute@node:internal/deps/undici/undici:7260
              js-to-wasm:iii:i@:-1
                wasm-function[20]@wasm://wasm/00034eea:0
                  wasm-to-js@:0
                    (anon)@node:internal/deps/undici/undici:7107
                      onResponseStarted@node:internal/deps/undici/undici:13639
                        now@:-1

[self 1.7ms]
  protoOf.k5h@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1626
    protoOf.q12@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6255
      updateCellSend@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4423
        tryResumeReceiver@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4538
          dispatch@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9202
            resumeUnconfined@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9239
              resume@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9263
                protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692
                  protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1329
                    protoOf.t12@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6415
                      protoOf.o8@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6068
                        receiveOnNoWaiterSuspend@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4569
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 198 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=5340.6 ms, total=8827.3 ms, hits=2996

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
       5326.1  |          6344.9   |    2986  | removeAll  (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
          3.4  |           882.2   |       2  | (anonymous)  (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203)
          3.2  |           112.8   |       2  | protoOf.o8  (.../packages/comparison-otel-proto/kotlin/ktor-ktor-client-core.js:610)
          2.9  |           867.5   |       3  | protoOf.c2  (.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216)
          1.6  |             1.6   |       1  | protoOf.c2  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:3198)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 922.0ms]
  updateCellSend@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1329
                exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1144
                  protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1533
                    protoOf.z4m@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 912.2ms]
  updateCellSend@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1329
                exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1144
                  protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1533
                    protoOf.z4m@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 709.9ms]
  updateCellSend@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1329
                exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1144
                  protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1533
                    protoOf.z4m@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 579.9ms]
  updateCellSend@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1329
                exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1144
                  protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1533
                    protoOf.z4m@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 414.1ms]
  updateCellSend@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1329
                exportCurrentBatch@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1144
                  protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1533
                    protoOf.z4m@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 1253 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=3107.6 ms, total=8455.5 ms, hits=1804

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        838.7  |          1609.2   |     488  | compare  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1606)
        403.3  |          1080.0   |     238  | multiply  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1694)
        401.3  |          1682.1   |     226  | modulo  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1908)
        340.8  |          1177.6   |     203  | divide  (.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1816)
        149.5  |           149.5   |      86  | writeVarUInt64  (.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1208)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 42.0ms]
  serialize@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:257
    protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
      protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1795
        protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1982
          protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3078
            protoOf.i4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1286
              protoOf.t4c@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                writeULongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                  writeLongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                    protoOf.b1l@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:551
                      protoOf.c1l@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1118
                        bitwiseAnd@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1912

[self 39.1ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
          _endSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:153
            end@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-api-all.js:149
              protoOf.t4z@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:563
                endInternal@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:470
                  protoOf.p58@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1692
                    protoOf.k5h@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1626
                      protoOf.q12@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6255
                        divide@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1816

[self 35.3ms]
  protoOf.o8@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:2267
    serialize@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:257
      protoOf.k4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1303
        protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1795
          protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:1982
            protoOf.z4c@.../packages/comparison-otel-proto/kotlin/otlp-exporter-proto.js:3078
              protoOf.i4a@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1286
                protoOf.t4c@.../packages/comparison-otel-proto/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                  writeULongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                    writeLongLe@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                      reverseBytes_0@.../packages/comparison-otel-proto/kotlin/kotlinx-io-kotlinx-io-core.js:2252
                        bitwiseAnd@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1912

[self 30.9ms]
  protoOf.k5h@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1626
    protoOf.q12@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6255
      updateCellSend@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4423
        tryResumeReceiver@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4538
          dispatch@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9202
            resumeUnconfined@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9239
              resume@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9263
                protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692
                  protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1329
                    protoOf.a13@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6589
                      protoOf.y10@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6596
                        divide@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1816

[self 29.0ms]
  updateCellSend@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1329
                protoOf.t12@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6415
                  protoOf.o8@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:6068
                    updateCellReceive@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4662
                      expandBuffer@.../packages/comparison-otel-proto/kotlin/kotlinx-coroutines-core.js:4773
                        divide@.../packages/comparison-otel-proto/kotlin/kotlin-kotlin-stdlib.js:1816
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 15 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=41.9 ms, total=53.6 ms, hits=25

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         40.2  |            40.2   |      24  | protoOf.s59  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504)
          1.7  |             1.7   |       1  | BatchSpanProcessor$Worker$run$slambda_0  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1271)
          0.0  |             1.7   |       0  | protoOf.l5h  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1673)
          0.0  |             6.8   |       0  | protoOf.j2a  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1747)
          0.0  |             1.6   |       0  | l  (.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1273)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 14.8ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:145
                    protoOf.w4y@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642
                      protoOf.s59@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:531

[self 6.3ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:145
                    protoOf.w4y@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642
                      protoOf.s59@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:531

[self 4.5ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:145
                    protoOf.w4y@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642
                      protoOf.s59@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:531

[self 3.3ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:145
                    protoOf.w4y@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642
                      protoOf.s59@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:531

[self 3.2ms]
  fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
    fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
      fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
        fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
          fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
            fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
              fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
                fibonacci@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:27
                  _startSpan@.../packages/comparison-otel-proto/kotlin/comparison-otel-proto.js:145
                    protoOf.w4y@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642
                      protoOf.s59@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:531
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``otel-proto.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

