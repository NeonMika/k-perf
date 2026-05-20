# Profile -- k-perf (native)

> WARNING: capture failed in this run; analyzing the previously captured profile instead.
> Profile file was last written at **2026-05-20 20:33:24** -- not from the most recent `run-all-profiles.ps1` invocation.
> Common cause on Windows 11: Smart App Control blocks unsigned `samply.exe`.

**Variant:** `k-perf`  
**Platform:** native  
**SUMMARY rendered:** 2026-05-20 20:40:37  
**Profile file last captured:** 2026-05-20 20:33:24  
**Profile file:** [k-perf.perfView.xml.zip](k-perf.perfView.xml.zip)  
**Wall time (capture run):** n/a (capture skipped or failed)  
**Workload-reported time:** unknown (no marker line in stdout)  

---

## Top 30 frames

```
Profile: k-perf.perfView.xml.zip
Frames: 22, Stacks: 211, Samples: 13, recorded weight: 0.03 s

=== Top 30 by SELF time ===
  self ms | total ms |   hits | function
     22  |       24  |      8  | c:\windows\system32\ntoskrnl!?
      2  |       26  |      3  | c:\windows\system32\ntdll!?
      1  |        1  |      1  | c:\windows\system32\drivers\stornvme.sys!?
      1  |        3  |      1  | c:\windows\system32\drivers\ntfs.sys!?

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |   hits | function
      2  |       26  |      3  | c:\windows\system32\ntdll!?
      0  |       26  |      0  | Process64 comparison-k-perf-flushEarly-false (17748) Args: 
      0  |       26  |      0  | Thread (25068) CPU=14ms (Startup Thread)
     22  |       24  |      8  | c:\windows\system32\ntoskrnl!?
      0  |       22  |      0  | ?!?
      0  |        5  |      0  | c:\windows\system32\kernel32!?
      0  |        5  |      0  | ...erf\build\bin\mingwx64\releaseexecutable\comparison-k-perf-flushearly-false!?
      0  |        4  |      0  | c:\windows\system32\kernelbase!?
      0  |        4  |      0  | c:\windows\system32\msvcrt!?
      0  |        3  |      0  | c:\windows\system32\drivers\fltmgr.sys!?
      1  |        3  |      1  | c:\windows\system32\drivers\ntfs.sys!?
      0  |        3  |      0  | c:\windows\system32\drivers\wd\wdfilter.sys!?
      1  |        1  |      1  | c:\windows\system32\drivers\stornvme.sys!?
      0  |        1  |      0  | c:\windows\system32\drivers\storport.sys!?
      0  |        1  |      0  | c:\windows\system32\drivers\bfs.sys!?
      0  |        1  |      0  | c:\windows\system32\combase!?
      0  |        0  |      0  | c:\windows\system32\drivers\fileinfo.sys!?
      0  |        0  |      0  | Thread (20268) CPU=1ms

=== Self-time attributed by MODULE / loaded binary ===
  self ms |    %  |   hits | module
     22  |  83.7%  |      8  | ntoskrnl
      2  |   7.3%  |      3  | ntdll
      1  |   4.6%  |      1  | stornvme
      1  |   4.4%  |      1  | ntfs
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/
Matched 0 of 13 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/
Matched 0 of 13 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Pattern /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/
Matched 0 of 13 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/
Matched 0 of 13 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

---

## How to view interactively

Drag ``k-perf.perfView.xml.zip`` onto https://profiler.firefox.com (or run ``samply load <file>``). App-code frames appear as ``0x<hex>`` because Kotlin/Native release builds drop ``-g`` on the ``-opt`` conflict; system DLL symbols resolve via Microsoft's public symbol server.

