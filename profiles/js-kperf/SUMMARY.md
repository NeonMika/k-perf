# Profile -- k-perf (js)

**Variant:** `k-perf`  
**Platform:** js  
**SUMMARY rendered:** 2026-05-20 20:40:36  
**Profile file last captured:** 2026-05-20 20:40:36  
**Profile file:** [kperf.cpuprofile](kperf.cpuprofile)  
**Wall time (capture run):** 1351 ms (incl. profiler overhead)  
**Workload-reported time:** unknown (no marker line in stdout)  

---

## Top 30 frames

```
Profile: kperf.cpuprofile
Wall: 1281.6 ms total, 1133 nodes, 795 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
   165.2  |     166.9  |    102  | add                                                (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:605)
   124.8  |     124.8  |     78  | (garbage collector)                                (:-1)
   102.1  |     244.0  |     64  | subtract                                           (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:510)
    61.6  |     309.7  |     37  | writeString                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:543)
    57.3  |     116.6  |     35  | checkBounds                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:81)
    55.6  |      55.6  |     35  | isNegative                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:506)
    54.4  |     274.5  |     33  | toDuration                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2917)
    53.7  |      53.7  |     34  | hrtime                                             (:-1)
    51.5  |     142.7  |     32  | protoOf.z3                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2571)
    43.0  |     150.6  |     27  | divide                                             (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644)
    38.6  |    6754.3  |     24  | fibonacci                                          (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39)
    38.0  |      38.0  |     23  | toNumber                                           (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:455)
    34.8  |      34.8  |     22  | writeBuffer                                        (:-1)
    32.1  |      50.1  |     19  | protoOf.d5                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:970)
    31.4  |      31.4  |     17  | equalsLong                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:498)
    30.2  |      63.4  |     19  | roundToLong                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1718)
    29.5  |      66.8  |     18  | toStringImpl                                       (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:459)
    24.3  |      24.3  |     15  | shiftRight                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:702)
    16.6  |     147.5  |     10  | convertDurationUnit_0                              (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013)
    16.1  |     102.4  |     10  | durationOfNanosNormalized                          (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2941)
    14.6  |      42.0  |      9  | protoOf.o4                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:121)
    14.4  |      14.4  |      9  | fromNumber                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:586)
    14.3  |     121.5  |      9  | Duration__plus_impl_yu9v8f                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2613)
    12.9  |     102.5  |      7  | protoOf.n4                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:315)
    12.7  |      12.7  |      8  | shiftLeft                                          (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:715)
    11.8  |      11.8  |     15  | (program)                                          (:-1)
    10.4  |      64.1  |      7  | hrtime                                             (node:internal/process/per_thread:77)
     9.6  |      43.6  |      6  | multiply                                           (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:522)
     9.4  |       9.4  |      6  | (anonymous)                                        (:-1)
     6.7  |       6.7  |      4  | _init_properties_boxedLong_kt__v24qrw              (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:763)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
    38.6  |    6754.3  |     24  | fibonacci                                          (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39)
     1.7  |    2257.5  |      1  | (anonymous)                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:0)
     0.0  |    1281.3  |      0  | (root)                                             (:-1)
     0.0  |    1135.7  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |    1134.0  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     0.0  |    1133.7  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |    1133.7  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |    1133.7  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     1.3  |    1133.4  |      1  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |    1132.1  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     0.0  |    1122.1  |      0  | (anonymous)                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:14)
     1.7  |    1120.4  |      1  | mainWrapper                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:152)
     3.4  |    1118.8  |      1  | main                                               (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:89)
     1.6  |     923.5  |      1  | workload                                           (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:78)
     1.7  |     798.1  |      1  | _exit_method                                       (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:138)
     1.7  |     435.8  |      1  | protoOf.e3                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123)
     0.0  |     418.0  |      0  | ValueTimeMark__elapsedNow_impl_eonqvs              (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3005)
     0.0  |     416.3  |      0  | protoOf.e3                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2087)
     0.0  |     414.5  |      0  | protoOf.d4                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033)
    61.6  |     309.7  |     37  | writeString                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:543)
    54.4  |     274.5  |     33  | toDuration                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2917)
     0.0  |     273.7  |      0  | compare                                            (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:442)
   102.1  |     244.0  |     64  | subtract                                           (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:510)
   165.2  |     166.9  |    102  | add                                                (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:605)
     1.7  |     161.6  |      1  | _enter_method                                      (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:134)
     2.7  |     159.9  |      2  | Duration__toLong_impl_shr43i                       (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2739)
     0.0  |     151.7  |      0  | durationOfNanos                                    (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2996)
     0.0  |     150.8  |      0  | _Duration___get_inWholeMicroseconds__impl__8oe8vv  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2755)
    43.0  |     150.6  |     27  | divide                                             (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644)
    16.6  |     147.5  |     10  | convertDurationUnit_0                              (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 57 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=64.1 ms, total=535.8 ms, hits=41

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
         53.7  |            53.7   |      34  | hrtime  (node:internal/process/per_thread:77)
          7.3  |            33.3   |       5  | protoOf.e3  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123)
          3.2  |            30.8   |       2  | protoOf.c3  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2117)
          0.0  |           414.5   |       0  | protoOf.d4  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033)
          0.0  |             3.5   |       0  | main  (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:89)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 5.7ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
            _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:138
              protoOf.d4@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3005
                  protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2087
                    protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 5.6ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
            fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
              _enter_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:134
                protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3050
                  protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2081
                    protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2117
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 5.0ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
            _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:138
              protoOf.d4@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3005
                  protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2087
                    protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 4.7ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
            fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
              _enter_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:134
                protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3050
                  protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2081
                    protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2117
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 4.4ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
            fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
              _enter_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:134
                protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3050
                  protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2081
                    protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2117
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 2 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=0.0 ms, total=26.3 ms, hits=0

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
          0.0  |            19.5   |       0  | println  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1473)
          0.0  |             6.7   |       0  | protoOf.e2  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1427)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self NaNms]
  wrapModuleLoad@node:internal/modules/cjs/loader:237
    (anon)@node:internal/modules/cjs/loader:1238
      (anon)@node:internal/modules/cjs/loader:1537
        (anon)@node:internal/modules/cjs/loader:1925
          (anon)@node:internal/modules/cjs/loader:1781
            (anon)@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:0
              (anon)@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:0
                (anon)@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:14
                  mainWrapper@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:152
                    main@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:89
                      println@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1473
                        protoOf.e2@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1427

[self NaNms]
  (anon)@node:internal/modules/cjs/loader:1238
    (anon)@node:internal/modules/cjs/loader:1537
      (anon)@node:internal/modules/cjs/loader:1925
        (anon)@node:internal/modules/cjs/loader:1781
          (anon)@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:0
            (anon)@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:0
              (anon)@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:14
                mainWrapper@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:152
                  main@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:89
                    println@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1473
                      protoOf.e2@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1427
                        protoOf.c2@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1424
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 137 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=191.2 ms, total=490.7 ms, hits=117

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        110.9  |           227.3   |      68  | compare  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:442)
         33.3  |           129.3   |      21  | convertDurationUnit_0  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013)
         14.5  |            51.7   |       9  | divide  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644)
         13.1  |            27.9   |       8  | toStringImpl  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:459)
          9.9  |            25.9   |       5  | multiply  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:522)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 7.9ms]
  workload@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:78
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
            fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
              fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
                _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:138
                  _Duration___get_inWholeMicroseconds__impl__8oe8vv@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2755
                    Duration__toLong_impl_shr43i@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2739
                      convertDurationUnit_0@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013
                        divide@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644

[self 6.6ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
            fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
              fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
                _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:138
                  _Duration___get_inWholeMicroseconds__impl__8oe8vv@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2755
                    Duration__toLong_impl_shr43i@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2739
                      convertDurationUnit_0@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013
                        divide@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644

[self 4.9ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
          _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:138
            protoOf.d4@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033
              ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3005
                protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2087
                  protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123
                    toDuration@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2917
                      compare@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:442
                        subtract@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:510

[self 4.9ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
          _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:138
            protoOf.d4@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033
              ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3005
                protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2087
                  protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123
                    toDuration@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2917
                      compare@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:442
                        subtract@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:510

[self 4.8ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:39
      _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:138
        protoOf.d4@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033
          ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3005
            protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2087
              protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123
                toDuration@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2917
                  durationOfNanos@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2996
                    protoOf.z3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2571
                      compare@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:442
                        subtract@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:510
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

