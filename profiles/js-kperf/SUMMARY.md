# Profile -- k-perf (js)

**Variant:** `k-perf`  
**Platform:** js  
**SUMMARY rendered:** 2026-05-05 22:26:18  
**Profile file last captured:** 2026-05-05 22:26:18  
**Profile file:** [kperf.cpuprofile](kperf.cpuprofile)  
**Wall time (capture run):** 2758 ms (incl. profiler overhead)  
**Workload-reported time:** 2630 ms  

---

## Top 30 frames

```
Profile: kperf.cpuprofile
Wall: 2670.5 ms total, 1124 nodes, 1600 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
  1379.8  |    1379.8  |    832  | writeBuffer                                        (:-1)
   254.0  |     259.1  |    152  | Segment_init_$Init$                                (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:342)
   121.7  |     121.7  |     73  | (garbage collector)                                (:-1)
    75.4  |     458.4  |     46  | writeString                                        (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:543)
    71.2  |      71.2  |     42  | add                                                (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:593)
    54.6  |     117.4  |     33  | subtract                                           (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:498)
    47.2  |     138.4  |     27  | toDuration                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2575)
    47.0  |   44325.9  |     29  | fibonacci                                          (.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36)
    46.4  |    1601.0  |     28  | protoOf.j4                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:970)
    29.8  |      61.7  |     18  | checkBounds                                        (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:81)
    29.2  |      94.9  |     18  | divide                                             (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:632)
    27.7  |      27.7  |     16  | isNegative                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:494)
    25.9  |      25.9  |     14  | bitMaskWith                                        (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:353)
    25.1  |     287.5  |     15  | protoOf.e4                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:163)
    24.6  |      35.8  |     15  | toStringImpl                                       (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:447)
    23.3  |      23.3  |     14  | hrtime                                             (:-1)
    23.3  |      55.8  |     13  | implement                                          (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:319)
    18.5  |      18.5  |     11  | equalsLong                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:486)
    18.5  |     140.1  |     11  | compare                                            (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:430)
    18.4  |      18.4  |     11  | _init_properties_boxedLong_kt__v24qrw              (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:751)
    17.6  |      24.3  |     10  | roundToLong                                        (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1518)
    17.0  |      77.7  |     10  | getPropertyCallableRef                             (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1073)
    16.6  |      39.9  |      9  | hrtime                                             (node:internal/process/per_thread:77)
    14.7  |      47.5  |      9  | protoOf.e3                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2250)
    13.9  |      13.9  |     17  | (program)                                          (:-1)
    13.5  |      21.8  |      8  | protoOf.c4                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:131)
    13.4  |      13.4  |      8  | toNumber                                           (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:443)
    11.6  |    1619.2  |      7  | protoOf.m1                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:327)
    10.7  |      17.3  |      7  | fromInt                                            (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:490)
    10.2  |      10.2  |      7  | FastBuffer                                         (node:internal/buffer:959)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
    47.0  |   44325.9  |     29  | fibonacci                                          (.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36)
     0.0  |    5056.1  |      0  | (anonymous)                                        (.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:0)
     0.0  |    2670.4  |      0  | (root)                                             (:-1)
     0.0  |    2536.7  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |    2534.9  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     0.0  |    2533.1  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |    2533.1  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |    2533.1  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     1.5  |    2533.0  |      1  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |    2531.5  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     0.0  |    2521.3  |      0  | (anonymous)                                        (.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:14)
     0.0  |    2517.9  |      0  | mainWrapper                                        (.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:127)
     0.0  |    2517.9  |      0  | main                                               (.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:88)
     0.0  |    2506.2  |      0  | workload                                           (.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:75)
     3.3  |    1772.1  |      2  | _enter_method                                      (.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:108)
    11.6  |    1619.2  |      7  | protoOf.m1                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:327)
    46.4  |    1601.0  |     28  | protoOf.j4                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:970)
     0.0  |    1393.4  |      0  | withCaughtException                                (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:687)
     1.7  |    1392.9  |      1  | writeFileSync                                      (node:fs:2393)
     8.4  |    1391.8  |      5  | (anonymous)                                        (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:961)
     6.4  |    1391.2  |      4  | writeSync                                          (node:fs:882)
  1379.8  |    1379.8  |    832  | writeBuffer                                        (:-1)
     0.0  |     535.5  |      0  | _exit_method                                       (.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:113)
    75.4  |     458.4  |     46  | writeString                                        (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:543)
    25.1  |     287.5  |     15  | protoOf.e4                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:163)
     0.0  |     262.4  |      0  | protoOf.h4                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:760)
   254.0  |     259.1  |    152  | Segment_init_$Init$                                (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:342)
     1.7  |     254.2  |      1  | protoOf.x4                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:368)
     0.0  |     252.6  |      0  | Segment_init_$Create$                              (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:349)
     3.1  |     199.9  |      1  | protoOf.j2                                         (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1912)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 39 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=39.9 ms, total=251.7 ms, hits=23

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         23.3  |            23.3   |      14  | hrtime  (node:internal/process/per_thread:77)
         10.0  |            15.0   |       5  | protoOf.j2  (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1912)
          6.6  |            24.9   |       4  | protoOf.h2  (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1906)
          0.0  |           188.5   |       0  | protoOf.i3  (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2691)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 3.4ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
    fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
      fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
        fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
          fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
            fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
              _enter_method@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:108
                protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2708
                  protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1870
                    protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1906
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 3.3ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
    fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
      fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
        fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
          fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
            fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
              _enter_method@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:108
                protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2708
                  protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1870
                    protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1906
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 3.3ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
    fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
      fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
        fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
          fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
            fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
              _exit_method@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:113
                protoOf.i3@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2691
                  ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2663
                    protoOf.j2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1876
                      protoOf.j2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1912
                        hrtime@node:internal/process/per_thread:77

[self 3.3ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
    fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
      fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
        fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
          fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
            fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
              _enter_method@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:108
                protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2708
                  protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1870
                    protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1906
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 1.7ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
    fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
      fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
        fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
          fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
            fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
              _enter_method@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:108
                protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2708
                  protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1870
                    protoOf.h2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1906
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 0 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=0.0 ms, total=0.0 ms, hits=0

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 108 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=112.1 ms, total=269.2 ms, hits=68

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         59.6  |           102.3   |      36  | compare  (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:430)
         26.3  |            87.0   |      16  | convertDurationUnit_0  (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1802)
          8.5  |            31.0   |       5  | divide  (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:632)
          4.8  |            10.9   |       3  | multiply  (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:510)
          4.5  |             9.5   |       3  | toStringImpl  (.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:447)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 6.7ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
    fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
      fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
        fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
          fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
            fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
              fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
                _exit_method@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:113
                  _Duration___get_inWholeMicroseconds__impl__8oe8vv@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2437
                    Duration__toLong_impl_shr43i@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2418
                      convertDurationUnit_0@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1802
                        divide@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:632

[self 5.1ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
    fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
      fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
        fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
          fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
            fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
              fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
                _exit_method@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:113
                  writeString@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:543
                    checkBounds@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlinx-io-kotlinx-io-core.js:81
                      compare@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:430
                        subtract@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:498

[self 5.1ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
    fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
      fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
        fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
          fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
            fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
              fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
                _exit_method@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:113
                  _Duration___get_inWholeMicroseconds__impl__8oe8vv@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2437
                    Duration__toLong_impl_shr43i@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2418
                      convertDurationUnit_0@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1802
                        divide@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:632

[self 3.5ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
    fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
      fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
        fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
          _exit_method@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:113
            protoOf.i3@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2691
              ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2663
                protoOf.j2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1876
                  protoOf.j2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1912
                    toDuration@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2575
                      compare@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:430
                        subtract@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:498

[self 3.4ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
    fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
      fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
        fibonacci@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:36
          _exit_method@.../packages/comparison-k-perf-flushEarly-true/kotlin/comparison-k-perf-flushEarly-true.js:113
            protoOf.i3@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2691
              ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2663
                protoOf.j2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1876
                  protoOf.j2@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:1912
                    toDuration@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:2575
                      compare@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:430
                        subtract@.../packages/comparison-k-perf-flushEarly-true/kotlin/kotlin-kotlin-stdlib.js:498
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Found 0 matching node(s) for /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/

Aggregate match: self=0.0 ms, total=0.0 ms, hits=0

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
```

---

## How to view interactively

In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``kperf.cpuprofile``. Or drag the file onto https://profiler.firefox.com.

