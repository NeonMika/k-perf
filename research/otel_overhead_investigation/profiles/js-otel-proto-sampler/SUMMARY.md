# Profile -- otel-proto-sampler (Protobuf/gRPC + alwaysOn sampler) (js)

**Variant:** `otel-proto-sampler`  
**Platform:** js  
**SUMMARY rendered:** 2026-06-13 17:05:04  
**Profile file last captured:** 2026-06-13 17:05:03  
**Profile file:** [otel-proto-sampler.cpuprofile](otel-proto-sampler.cpuprofile)  
**Wall time (capture run):** 44343 ms (incl. profiler overhead)  
**Workload-reported time:** 43825 ms  

---

## Top 30 frames

```
Profile: otel-proto-sampler.cpuprofile
Wall: 43984.7 ms total, 10808 nodes, 25735 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
  5393.2  |    6356.4  |   3273  | protoOf.o8                                         (.../packages/comparison-otel-proto-sampler/kotlin/ktor-ktor-client-core.js:8987)
  5304.0  |    6358.1  |   2959  | recyclableRemoveAll                                (.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787)
  4858.9  |    4858.9  |   2651  | (idle)                                             (:-1)
  3711.7  |    3711.7  |   2248  | (garbage collector)                                (:-1)
  2384.9  |    2384.9  |   1380  | arrayCopy                                          (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:3040)
  1072.0  |    1077.1  |    609  | add                                                (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1777)
  1051.8  |    7448.2  |    565  | removeAll                                          (.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
   951.9  |    1118.0  |    550  | equals                                             (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:2190)
   847.7  |    3144.2  |    479  | divide                                             (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1816)
   762.6  |     762.6  |    465  | toTypedArray                                       (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:2756)
   728.3  |    1564.9  |    408  | subtract                                           (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1682)
   662.3  |    1169.9  |    365  | writeVarUInt64                                     (.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1208)
   525.5  |     525.5  |    312  | millis                                             (.../node_modules/@js-joda/core/dist/js-joda.js:12810)
   508.8  |    1894.3  |    293  | multiply                                           (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1694)
   483.4  |     483.4  |   1728  | (program)                                          (:-1)
   416.8  |     424.8  |    235  | bitwiseAnd                                         (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1912)
   383.5  |     398.3  |    244  | readableByteStreamControllerEnqueue                (node:internal/webstreams/readablestream:2872)
   381.7  |     381.7  |    240  | captureStack                                       (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:2329)
   356.2  |     370.2  |    213  | protoOf.d4w                                        (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-api-all.js:1440)
   329.5  |     329.5  |    192  | charCodeAt                                         (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1988)
   306.2  |     960.4  |    173  | lessThan                                           (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1808)
   294.4  |     312.2  |    166  | equalsLong                                         (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1666)
   277.1  |     870.9  |    166  | get_isValid                                        (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-api-all.js:102)
   255.1  |    1772.6  |    150  | protoOf.w4y                                        (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642)
   244.0  |     244.0  |    138  | fromNumber                                         (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1758)
   237.5  |    2139.5  |    134  | protoOf.o8                                         (.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6068)
   227.7  |     229.4  |    134  | utf8Size                                           (.../packages/comparison-otel-proto-sampler/kotlin/okio-parent-okio.js:16)
   222.2  |     457.1  |    131  | get_spanIdBytes                                    (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-api-all.js:96)
   220.6  |    6033.9  |    123  | protoOf.z4c                                        (.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:3078)
   217.8  |    1735.8  |    115  | compare                                            (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1606)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
   128.5  |  227464.2  |     76  | fibonacci                                          (.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28)
     0.0  |   43984.2  |      0  | (root)                                             (:-1)
     0.0  |   32772.8  |      0  | (anonymous)                                        (.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:0)
    39.2  |   29216.1  |     22  | protoOf.n8                                         (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4692)
    52.9  |   17402.5  |     26  | processTimers                                      (node:internal/timers:525)
    30.8  |   17344.7  |     15  | listOnTimeout                                      (node:internal/timers:545)
    16.5  |   17331.5  |      9  | protoOf.w1g                                        (.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:11052)
    34.8  |   17314.7  |     20  | protoOf.pq                                         (.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9144)
     1.7  |   16722.7  |      1  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     1.6  |   16719.4  |      1  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     0.0  |   16714.4  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |   16714.4  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |   16692.8  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     0.0  |   16443.3  |      0  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |   16442.0  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     1.7  |   16401.6  |      1  | protoOf.o8                                         (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4878)
     0.0  |   16303.6  |      0  | mainWrapper                                        (.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:158)
     0.0  |   16303.6  |      0  | main                                               (.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:84)
     0.0  |   16280.2  |      0  | workload                                           (.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:69)
     0.0  |   13494.8  |      0  | _endSpan                                           (.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:154)
    60.6  |   13173.5  |     37  | end                                                (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-api-all.js:149)
     0.0  |   12998.9  |      0  | protoOf.t4z                                        (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:563)
   113.4  |   12985.7  |     66  | protoOf.q12                                        (.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6255)
     1.7  |   12983.7  |      1  | protoOf.l5h                                        (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1630)
     0.0  |   12969.6  |      0  | (anonymous)                                        (.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:10875)
     0.0  |   12793.0  |      0  | protoOf.p58                                        (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1696)
     0.0  |   12644.4  |      0  | endInternal                                        (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:470)
    33.3  |   11821.1  |     18  | updateCellSend                                     (.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4423)
     5.4  |   11755.0  |      3  | tryResumeReceiver                                  (.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4538)
     0.1  |   11748.8  |      0  | dispatch                                           (.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9202)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 42 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=19.3 ms, total=605.3 ms, hits=12

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         12.8  |            12.8   |       8  | protoOf.z4c  (.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:3078)
          1.7  |             7.6   |       1  | protoOf.o8  (.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6068)
          1.6  |             1.6   |       1  | Event  (node:internal/event_target:114)
          1.6  |             1.6   |       1  | insert  (node:internal/timers:386)
          1.6  |             1.6   |       1  | fetching  (node:internal/deps/undici/undici:12870)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 8.1ms]
  protoOf.n8@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4692
    protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4878
      l@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:8060
        protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:8022
          l@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:2455
            protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:2267
              serialize@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:257
                protoOf.k4a@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1303
                  protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:1795
                    protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:1982
                      protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:3078
                        writeUnknownFields@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:180

[self 3.2ms]
  serialize@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:257
    protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:350
      protoOf.l4a@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1314
        protoOf.k4a@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1303
          protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:1795
            protoOf.l4a@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1314
              protoOf.k4a@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1303
                protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:1982
                  protoOf.l4a@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1314
                    protoOf.k4a@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1303
                      protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:3078
                        writeUnknownFields@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:180

[self 1.7ms]
  tryResume0@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6772
    protoOf.fo@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:1241
      dispatchResume@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:867
        dispatch@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9202
          resumeUnconfined@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9239
            resume@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9263
              protoOf.r8@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4747
                protoOf.n8@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4692
                  protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1333
                    protoOf.t12@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6415
                      protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6068
                        receiveOnNoWaiterSuspend@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4569

[self 1.6ms]
  protoOf.d41@.../packages/comparison-otel-proto-sampler/kotlin/ktor-ktor-client-core.js:8112
    protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/ktor-ktor-client-core.js:8048
      tryMakeCompleting@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:2489
        tryMakeCompletingSlowPath@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:2518
          finalizeFinishingState@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:2010
            completeStateFinalization@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:2129
              notifyCompletion@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:2220
                protoOf.ro@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:3458
                  (anon)@.../packages/comparison-otel-proto-sampler/kotlin/ktor-ktor-client-core.js:9315
                    abortSignal@node:internal/abort_controller:432
                      Event@node:internal/event_target:114
                        now@:-1

[self 1.6ms]
  (root)@:-1
    processTimers@node:internal/timers:525
      listOnTimeout@node:internal/timers:545
        (anon)@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:10875
          protoOf.w1g@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:11052
            insert@node:internal/timers:386
              getLibuvNow@:-1
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 199 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=5319.2 ms, total=8915.9 ms, hits=2968

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
       5304.0  |          6358.1   |    2959  | removeAll  (.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660)
          4.8  |           887.4   |       3  | protoOf.c2  (.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:216)
          3.2  |           120.9   |       2  | protoOf.o8  (.../packages/comparison-otel-proto-sampler/kotlin/ktor-ktor-client-core.js:610)
          3.2  |           898.8   |       2  | (anonymous)  (.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:203)
          1.7  |             1.7   |       1  | protoOf.o8  (.../packages/comparison-otel-proto-sampler/kotlin/ktor-ktor-client-core.js:4197)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 932.6ms]
  updateCellSend@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1333
                exportCurrentBatch@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1148
                  protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1537
                    protoOf.z4m@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 881.2ms]
  updateCellSend@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1333
                exportCurrentBatch@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1148
                  protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1537
                    protoOf.z4m@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 680.7ms]
  updateCellSend@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1333
                exportCurrentBatch@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1148
                  protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1537
                    protoOf.z4m@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 596.4ms]
  updateCellSend@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1333
                exportCurrentBatch@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1148
                  protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1537
                    protoOf.z4m@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787

[self 409.7ms]
  updateCellSend@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4423
    tryResumeReceiver@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4538
      dispatch@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9202
        resumeUnconfined@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9239
          resume@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9263
            protoOf.n8@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4692
              protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1333
                exportCurrentBatch@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1148
                  protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1537
                    protoOf.z4m@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:341
                      removeAll@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:660
                        recyclableRemoveAll@.../packages/comparison-otel-proto-sampler/kotlin/Kotlin-Immutable-Collections-kotlinx-collections-immutable.js:787
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 1207 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=3102.1 ms, total=8300.8 ms, hits=1754

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        801.5  |          1511.5   |     448  | compare  (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1606)
        430.8  |          1643.8   |     243  | modulo  (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1908)
        373.3  |          1027.5   |     210  | multiply  (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1694)
        346.1  |          1204.4   |     198  | divide  (.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1816)
        151.1  |           588.4   |      85  | protoOf.y10  (.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6596)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 75.3ms]
  serialize@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:257
    protoOf.k4a@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1303
      protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:1795
        protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:1982
          protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:3078
            protoOf.i4a@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1286
              protoOf.t4c@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                writeULongLe@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                  writeLongLe@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                    protoOf.b1l@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-io-kotlinx-io-core.js:551
                      protoOf.c1l@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-io-kotlinx-io-core.js:1118
                        bitwiseAnd@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1912

[self 45.6ms]
  protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:2267
    serialize@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:257
      protoOf.k4a@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1303
        protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:1795
          protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:1982
            protoOf.z4c@.../packages/comparison-otel-proto-sampler/kotlin/otlp-exporter-proto.js:3078
              protoOf.i4a@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1286
                protoOf.t4c@.../packages/comparison-otel-proto-sampler/kotlin/grpc-kmp-kmp-grpc-core.js:1290
                  writeULongLe@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-io-kotlinx-io-core.js:1422
                    writeLongLe@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-io-kotlinx-io-core.js:1432
                      reverseBytes_0@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-io-kotlinx-io-core.js:2252
                        bitwiseAnd@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1912

[self 33.8ms]
  protoOf.l5h@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1630
    protoOf.q12@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6255
      updateCellSend@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4423
        tryResumeReceiver@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:4538
          dispatch@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9202
            resumeUnconfined@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9239
              resume@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:9263
                protoOf.n8@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:4692
                  protoOf.o8@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1333
                    protoOf.a13@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6589
                      protoOf.y10@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6596
                        divide@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1816

[self 32.8ms]
  fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
    fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
      fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
        fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
          _endSpan@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:154
            end@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-api-all.js:149
              protoOf.t4z@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:563
                endInternal@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:470
                  protoOf.p58@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1696
                    protoOf.l5h@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1630
                      protoOf.q12@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6255
                        divide@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1816

[self 29.1ms]
  fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
    fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
      fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
        fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
          _endSpan@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:154
            end@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-api-all.js:149
              protoOf.t4z@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:563
                endInternal@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:470
                  protoOf.p58@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1696
                    protoOf.l5h@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1630
                      protoOf.q12@.../packages/comparison-otel-proto-sampler/kotlin/kotlinx-coroutines-core.js:6255
                        divide@.../packages/comparison-otel-proto-sampler/kotlin/kotlin-kotlin-stdlib.js:1816
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 13 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=64.0 ms, total=72.5 ms, hits=39

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         62.3  |            62.3   |      38  | protoOf.s59  (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504)
          1.7  |             1.7   |       1  | protoOf.e4z  (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:683)
          0.0  |             1.7   |       0  | protoOf.m5h  (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1677)
          0.0  |             6.8   |       0  | protoOf.j2a  (.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:1751)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 17.1ms]
  fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
    fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
      fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
        fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
          fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
            fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
              fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
                fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
                  _startSpan@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:146
                    protoOf.w4y@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642
                      protoOf.s59@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:531

[self 9.9ms]
  fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
    fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
      fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
        fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
          fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
            fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
              fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
                fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
                  _startSpan@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:146
                    protoOf.w4y@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642
                      protoOf.s59@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:531

[self 9.6ms]
  fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
    fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
      fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
        fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
          fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
            fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
              fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
                fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
                  _startSpan@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:146
                    protoOf.w4y@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642
                      protoOf.s59@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:531

[self 7.9ms]
  fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
    fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
      fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
        fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
          fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
            fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
              fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
                fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
                  _startSpan@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:146
                    protoOf.w4y@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642
                      protoOf.s59@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:531

[self 5.1ms]
  fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
    fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
      fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
        fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
          fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
            fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
              fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
                fibonacci@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:28
                  _startSpan@.../packages/comparison-otel-proto-sampler/kotlin/comparison-otel-proto-sampler.js:146
                    protoOf.w4y@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:642
                      protoOf.s59@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:504
                        RecordEventsReadableSpan@.../packages/comparison-otel-proto-sampler/kotlin/opentelemetry-kotlin-sdk-sdk-trace.js:531
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``otel-proto-sampler.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

