# Profile -- k-perf (native)

**Variant:** `k-perf`  
**Platform:** native  
**SUMMARY rendered:** 2026-05-05 22:26:22  
**Profile file last captured:** 2026-05-05 22:26:21  
**Profile file:** [k-perf.perfView.xml.zip](k-perf.perfView.xml.zip)  
**Wall time (capture run):** 0 ms (incl. profiler overhead)  
**Workload-reported time:** 1172 ms  

---

## Top 30 frames

```
Profile: k-perf.perfView.xml.zip
Frames: 28, Stacks: 1141, Samples: 1179, recorded weight: 1.15 s

=== Top 30 by SELF time ===
  self ms | total ms |   hits | function
    496  |      659  |    473  | c:\windows\system32\ntoskrnl!?
    294  |     1095  |    310  | ...perf\build\bin\mingwx64\releaseexecutable\comparison-k-perf-flushearly-true!?
    111  |      556  |    117  | c:\windows\system32\drivers\fltmgr.sys!?
     92  |      778  |    122  | c:\windows\system32\msvcrt!?
     45  |     1144  |     49  | c:\windows\system32\ntdll!?
     38  |      203  |     37  | c:\windows\system32\drivers\ntfs.sys!?
     30  |       48  |     29  | c:\windows\system32\drivers\wd\wdfilter.sys!?
     18  |     1093  |     18  | c:\windows\system32\kernel32!?
      9  |      687  |      8  | c:\windows\system32\kernelbase!?
      7  |       22  |      7  | c:\windows\system32\drivers\cldflt.sys!?
      6  |       36  |      6  | c:\windows\system32\drivers\fileinfo.sys!?
      1  |       16  |      2  | c:\windows\system32\drivers\bindflt.sys!?
      0  |        0  |      1  | c:\windows\system32\drivers\luafv.sys!?

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |   hits | function
      0  |     1146  |      0  | Process64 comparison-k-perf-flushEarly-true (33652) Args: 
     45  |     1144  |     49  | c:\windows\system32\ntdll!?
      0  |     1103  |      0  | Thread (23564) CPU=1098ms (Startup Thread)
    294  |     1095  |    310  | ...perf\build\bin\mingwx64\releaseexecutable\comparison-k-perf-flushearly-true!?
     18  |     1093  |     18  | c:\windows\system32\kernel32!?
     92  |      778  |    122  | c:\windows\system32\msvcrt!?
      9  |      687  |      8  | c:\windows\system32\kernelbase!?
    496  |      659  |    473  | c:\windows\system32\ntoskrnl!?
    111  |      556  |    117  | c:\windows\system32\drivers\fltmgr.sys!?
     38  |      203  |     37  | c:\windows\system32\drivers\ntfs.sys!?
     30  |       48  |     29  | c:\windows\system32\drivers\wd\wdfilter.sys!?
      0  |       40  |      0  | Thread (32372) CPU=77ms
      6  |       36  |      6  | c:\windows\system32\drivers\fileinfo.sys!?
      0  |       31  |      0  | ?!?
      7  |       22  |      7  | c:\windows\system32\drivers\cldflt.sys!?
      1  |       16  |      2  | c:\windows\system32\drivers\bindflt.sys!?
      0  |        7  |      0  | c:\windows\system32\drivers\condrv.sys!?
      0  |        6  |      0  | c:\windows\system32\drivers\appid.sys!?
      0  |        5  |      0  | c:\windows\system32\drivers\applockerfltr.sys!?
      0  |        3  |      0  | Thread (30416) CPU=5ms
      0  |        2  |      0  | BROKEN
      0  |        1  |      0  | c:\windows\system32\rpcrt4!?
      0  |        1  |      0  | c:\windows\system32\sechost!?
      0  |        0  |      1  | c:\windows\system32\drivers\luafv.sys!?

=== Self-time attributed by MODULE / loaded binary ===
  self ms |    %  |   hits | module
    496  |  43.3%  |    473  | ntoskrnl
    294  |  25.6%  |    310  | comparison-k-perf-flushearly-true
    111  |   9.7%  |    117  | fltmgr
     92  |   8.0%  |    122  | msvcrt
     45  |   4.0%  |     49  | ntdll
     38  |   3.3%  |     37  | ntfs
     30  |   2.6%  |     29  | wdfilter
     18  |   1.5%  |     18  | kernel32
      9  |   0.8%  |      8  | kernelbase
      7  |   0.6%  |      7  | cldflt
      6  |   0.5%  |      6  | fileinfo
      1  |   0.1%  |      2  | bindflt
      0  |   0.0%  |      1  | luafv
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/
Matched 0 of 1179 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/
Matched 0 of 1179 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Pattern /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/
Matched 0 of 1179 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/
Matched 0 of 1179 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

---

## How to view interactively

Drag ``k-perf.perfView.xml.zip`` onto https://profiler.firefox.com (or run ``samply load <file>``). App-code frames appear as ``0x<hex>`` because Kotlin/Native release builds drop ``-g`` on the ``-opt`` conflict; system DLL symbols resolve via Microsoft's public symbol server.

