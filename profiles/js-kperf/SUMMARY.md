# Profile -- k-perf (js)

**Variant:** `k-perf`  
**Platform:** js  
**SUMMARY rendered:** 2026-06-13 16:56:03  
**Profile file last captured:** 2026-06-13 16:56:02  
**Profile file:** [kperf.cpuprofile](kperf.cpuprofile)  
**Wall time (capture run):** 2687 ms (incl. profiler overhead)  
**Workload-reported time:** unknown (no marker line in stdout)  

---

## Top 30 frames

```
Profile: kperf.cpuprofile
Wall: 2592.3 ms total, 1292 nodes, 1582 samples

=== Top 30 by SELF time ===
  self ms |  total ms |   hits |  function   (file)
   323.6  |     328.6  |    194  | add                                                (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:605)
   210.0  |     210.0  |    129  | (garbage collector)                                (:-1)
   188.9  |     457.7  |    117  | subtract                                           (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:510)
   153.8  |     601.2  |     91  | writeString                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:543)
   145.4  |     145.4  |     88  | isNegative                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:506)
   138.6  |     324.4  |     85  | protoOf.z3                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2571)
   122.0  |     617.4  |     75  | toDuration                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2917)
   118.8  |     381.1  |     77  | divide                                             (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644)
   102.5  |     134.0  |     63  | roundToLong                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1718)
   101.4  |     101.4  |     62  | hrtime                                             (:-1)
    85.7  |   31901.6  |     53  | fibonacci                                          (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41)
    80.5  |      80.5  |     51  | equalsLong                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:498)
    75.0  |     202.5  |     45  | checkBounds                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:81)
    63.8  |      65.1  |     39  | shiftRight                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:702)
    52.9  |     221.1  |     32  | durationOfNanosNormalized                          (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2941)
    52.6  |     140.2  |     33  | toStringImpl                                       (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:459)
    45.4  |      47.0  |     28  | toNumber                                           (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:455)
    45.3  |     146.7  |     27  | hrtime                                             (node:internal/process/per_thread:77)
    42.2  |      42.2  |     71  | (program)                                          (:-1)
    35.3  |      35.3  |     23  | shiftLeft                                          (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:715)
    34.2  |     102.4  |     21  | protoOf.o4                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:121)
    32.0  |     185.3  |     21  | protoOf.n4                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:315)
    30.1  |      30.1  |     18  | _init_properties_boxedLong_kt__v24qrw              (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:763)
    28.6  |      28.6  |     18  | fromNumber                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:586)
    28.2  |     274.9  |     17  | Duration__plus_impl_yu9v8f                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2613)
    26.4  |      85.8  |     16  | multiply                                           (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:522)
    24.7  |      24.7  |     15  | writeBuffer                                        (:-1)
    23.5  |     349.7  |     15  | convertDurationUnit_0                              (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013)
    17.1  |     383.0  |     11  | Duration__toLong_impl_shr43i                       (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2739)
    15.0  |      38.7  |     10  | lessThan                                           (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:636)

=== Top 30 by TOTAL (inclusive) time ===
  self ms |  total ms |   hits |  function   (file)
    85.7  |   31901.6  |     53  | fibonacci                                          (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41)
     0.0  |    4633.7  |      0  | (anonymous)                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:0)
     0.0  |    2592.1  |      0  | (root)                                             (:-1)
     0.0  |    2325.9  |      0  | wrapModuleLoad                                     (node:internal/modules/cjs/loader:237)
     0.0  |    2325.9  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1238)
     0.0  |    2322.7  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1537)
     0.0  |    2322.7  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1925)
     0.0  |    2321.5  |      0  | (anonymous)                                        (node:internal/modules/cjs/loader:1781)
     0.0  |    2321.1  |      0  | (anonymous)                                        (node:internal/main/run_main_module:0)
     0.0  |    2319.6  |      0  | executeUserEntryPoint                              (node:internal/modules/run_main:139)
     0.0  |    2309.4  |      0  | (anonymous)                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:14)
     0.0  |    2306.2  |      0  | mainWrapper                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:157)
     0.0  |    2306.2  |      0  | main                                               (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:94)
     0.0  |    2292.0  |      0  | workload                                           (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:80)
     0.0  |    1801.4  |      0  | _exit_method                                       (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:143)
     6.4  |     994.2  |      4  | protoOf.e3                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123)
     0.0  |     973.8  |      0  | ValueTimeMark__elapsedNow_impl_eonqvs              (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3005)
     0.0  |     973.8  |      0  | protoOf.e3                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2087)
     0.0  |     972.2  |      0  | protoOf.d4                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033)
   122.0  |     617.4  |     75  | toDuration                                         (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2917)
   153.8  |     601.2  |     91  | writeString                                        (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:543)
     1.6  |     544.7  |      1  | compare                                            (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:442)
   188.9  |     457.7  |    117  | subtract                                           (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:510)
     0.0  |     387.3  |      0  | _Duration___get_inWholeMicroseconds__impl__8oe8vv  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2755)
    17.1  |     383.0  |     11  | Duration__toLong_impl_shr43i                       (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2739)
   118.8  |     381.1  |     77  | divide                                             (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644)
     0.0  |     364.6  |      0  | durationOfNanos                                    (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2996)
    23.5  |     349.7  |     15  | convertDurationUnit_0                              (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013)
     0.0  |     347.9  |      0  | _enter_method                                      (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:139)
   323.6  |     328.6  |    194  | add                                                (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:605)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Found 49 matching node(s) for /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/

Aggregate match: self=146.7 ms, total=1221.9 ms, hits=89

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        101.4  |           101.4   |      62  | hrtime  (node:internal/process/per_thread:77)
         31.1  |            86.6   |      18  | protoOf.e3  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123)
         14.1  |            60.1   |       9  | protoOf.c3  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2117)
          0.0  |             1.6   |       0  | main  (.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:94)
          0.0  |           972.2   |       0  | protoOf.d4  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 15.7ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
            fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
              _enter_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:139
                protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3050
                  protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2081
                    protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2117
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 11.0ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
            _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:143
              protoOf.d4@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3005
                  protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2087
                    protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 10.6ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
            fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
              _enter_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:139
                protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3050
                  protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2081
                    protoOf.c3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2117
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 9.3ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
            _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:143
              protoOf.d4@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3005
                  protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2087
                    protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1

[self 9.2ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
            _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:143
              protoOf.d4@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033
                ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3005
                  protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2087
                    protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123
                      hrtime@node:internal/process/per_thread:77
                        hrtime@:-1
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Found 1 matching node(s) for /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/

Aggregate match: self=0.0 ms, total=1.6 ms, hits=0

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
          0.0  |             1.6   |       0  | println  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1473)

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
                  mainWrapper@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:157
                    main@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:94
                      println@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1473
                        protoOf.e2@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:1427
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Found 172 matching node(s) for /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/

Aggregate match: self=429.5 ms, total=1043.7 ms, hits=271

=== Top 5 CALLERS of matched frames (by self-in-match) ===
  match-self ms |  match-total ms |   hits  |  caller (file:line)
        197.8  |           415.5   |     122  | compare  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:442)
         99.4  |           324.5   |      64  | convertDurationUnit_0  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013)
         42.0  |           106.2   |      26  | divide  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644)
         35.1  |            79.7   |      23  | toStringImpl  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:459)
         27.6  |            66.6   |      18  | protoOf.o4  (.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlinx-io-kotlinx-io-core.js:121)

=== Heaviest 5 matching call chains (root <- ... <- match) ===
[self 23.5ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
            fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
              fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
                _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:143
                  _Duration___get_inWholeMicroseconds__impl__8oe8vv@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2755
                    Duration__toLong_impl_shr43i@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2739
                      convertDurationUnit_0@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013
                        divide@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644

[self 21.4ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
            fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
              fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
                _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:143
                  _Duration___get_inWholeMicroseconds__impl__8oe8vv@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2755
                    Duration__toLong_impl_shr43i@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2739
                      convertDurationUnit_0@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013
                        divide@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644

[self 11.3ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
          _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:143
            protoOf.d4@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3033
              ValueTimeMark__elapsedNow_impl_eonqvs@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:3005
                protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2087
                  protoOf.e3@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2123
                    toDuration@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2917
                      compare@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:442
                        subtract@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:510

[self 10.6ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
            fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
              fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
                _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:143
                  _Duration___get_inWholeMicroseconds__impl__8oe8vv@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2755
                    Duration__toLong_impl_shr43i@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2739
                      convertDurationUnit_0@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013
                        divide@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644

[self 10.1ms]
  fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
    fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
      fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
        fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
          fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
            fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
              fibonacci@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:41
                _exit_method@.../packages/comparison-k-perf-flushEarly-false/kotlin/comparison-k-perf-flushEarly-false.js:143
                  _Duration___get_inWholeMicroseconds__impl__8oe8vv@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2755
                    Duration__toLong_impl_shr43i@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2739
                      convertDurationUnit_0@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:2013
                        divide@.../packages/comparison-k-perf-flushEarly-false/kotlin/kotlin-kotlin-stdlib.js:644
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

