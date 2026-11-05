# Profile -- otel (JSON/HTTP) (native)

**Variant:** `otel`  
**Platform:** native  
**SUMMARY rendered:** 2026-06-13 17:36:11  
**Profile file last captured:** 2026-06-13 17:36:10  
**Profile file:** [otel.perfView.xml.zip](otel.perfView.xml.zip)  
**Wall time (capture run):** 0 ms (incl. profiler overhead)  
**Workload-reported time:** 7787 ms  

---

## Top 30 frames

```
Profile: otel.perfView.xml.zip
Frames: 54, Stacks: 4715, Samples: 11838, recorded weight: 6.67 s

=== Top 30 by SELF time ===
  self ms | total ms |   hits | function
   5499  |     6604  |   9710  | ...ject\kmp-examples\comparison-otel\build\bin\mingwx64\releaseexecutable\main!?
    346  |      430  |    569  | c:\windows\system32\ntoskrnl!?
    275  |     6557  |    505  | c:\windows\system32\ntdll!?
    258  |     6532  |    476  | c:\windows\system32\kernel32!?
    158  |     2887  |    277  | c:\windows\system32\msvcrt!?
     49  |      121  |    111  | c:\windows\system32\drivers\tcpip.sys!?
     18  |      370  |     47  | c:\windows\system32\winhttp!?
     18  |       28  |     36  | c:\windows\system32\drivers\netio.sys!?
     17  |      202  |     33  | c:\windows\system32\webio!?
     10  |      130  |     25  | c:\windows\system32\drivers\afd.sys!?
      5  |       73  |     13  | c:\windows\system32\kernelbase!?
      5  |        5  |      9  | c:\windows\system32\ucrtbase!?
      2  |      144  |      6  | c:\windows\system32\mswsock!?
      2  |      148  |      4  | c:\windows\system32\ws2_32!?
      2  |        3  |      2  | c:\windows\system32\drivers\winhvr.sys!?
      2  |        2  |      2  | c:\windows\system32\drivers\intelppm.sys!?
      1  |        2  |      2  | c:\windows\system32\drivers\ndu.sys!?
      1  |        2  |      3  | c:\windows\system32\drivers\fltmgr.sys!?
      0  |        0  |      3  | c:\windows\system32\combase!?
      0  |       78  |      2  | ?!?
      0  |        1  |      1  | c:\windows\system32\drivers\fwpkclnt.sys!?
      0  |        2  |      1  | c:\windows\system32\drivers\ndis.sys!?
      0  |        0  |      1  | ...driverstore\filerepository\iigd_dch.inf_amd64_8b3356e9a80c7a42\igdkmd64.sys!?

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |   hits | function
      0  |     6671  |      0  | Process64 main (33196) Args: 
   5499  |     6604  |   9710  | ...ject\kmp-examples\comparison-otel\build\bin\mingwx64\releaseexecutable\main!?
    275  |     6557  |    505  | c:\windows\system32\ntdll!?
    258  |     6532  |    476  | c:\windows\system32\kernel32!?
      0  |     3592  |      0  | Thread (9464) CPU=5907ms (Startup Thread)
    158  |     2887  |    277  | c:\windows\system32\msvcrt!?
      0  |      867  |      0  | Thread (32840) CPU=1111ms
    346  |      430  |    569  | c:\windows\system32\ntoskrnl!?
      0  |      388  |      0  | Thread (4644) CPU=837ms
      0  |      380  |      0  | Thread (1896) CPU=789ms
      0  |      372  |      0  | Thread (27988) CPU=797ms
     18  |      370  |     47  | c:\windows\system32\winhttp!?
      0  |      366  |      0  | Thread (25052) CPU=822ms
      0  |      337  |      0  | Thread (26664) CPU=740ms
     17  |      202  |     33  | c:\windows\system32\webio!?
      2  |      148  |      4  | c:\windows\system32\ws2_32!?
      2  |      144  |      6  | c:\windows\system32\mswsock!?
     10  |      130  |     25  | c:\windows\system32\drivers\afd.sys!?
      0  |      129  |      0  | Thread (31872) CPU=265ms
     49  |      121  |    111  | c:\windows\system32\drivers\tcpip.sys!?
      0  |      114  |      0  | BROKEN
      0  |       86  |      0  | Thread (32772) CPU=210ms
      0  |       79  |      0  | Thread (33380) CPU=183ms
      0  |       78  |      2  | ?!?
      5  |       73  |     13  | c:\windows\system32\kernelbase!?
      0  |       65  |      0  | Thread (12640) CPU=154ms
     18  |       28  |     36  | c:\windows\system32\drivers\netio.sys!?
      0  |       24  |      0  | c:\windows\system32\fwpuclnt!?
      0  |        8  |      0  | Thread (31716) CPU=21ms
      5  |        5  |      9  | c:\windows\system32\ucrtbase!?

=== Self-time attributed by MODULE / loaded binary ===
  self ms |    %  |   hits | module
   5499  |  82.4%  |   9710  | main
    346  |   5.2%  |    569  | ntoskrnl
    275  |   4.1%  |    505  | ntdll
    258  |   3.9%  |    476  | kernel32
    158  |   2.4%  |    277  | msvcrt
     49  |   0.7%  |    111  | tcpip
     18  |   0.3%  |     47  | winhttp
     18  |   0.3%  |     36  | netio
     17  |   0.2%  |     33  | webio
     10  |   0.1%  |     25  | afd
      5  |   0.1%  |     13  | kernelbase
      5  |   0.1%  |      9  | ucrtbase
      2  |   0.0%  |      6  | mswsock
      2  |   0.0%  |      4  | ws2_32
      2  |   0.0%  |      2  | winhvr
      2  |   0.0%  |      2  | intelppm
      1  |   0.0%  |      2  | ndu
      1  |   0.0%  |      3  | fltmgr
      0  |   0.0%  |      3  | combase
      0  |   0.0%  |      2  | (unresolved)
      0  |   0.0%  |      1  | fwpkclnt
      0  |   0.0%  |      1  | ndis
      0  |   0.0%  |      1  | igdkmd64
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/
Matched 0 of 11838 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/
Matched 0 of 11838 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Pattern /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/
Matched 0 of 11838 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/
Matched 0 of 11838 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

---

## How to view interactively

Drag ``otel.perfView.xml.zip`` onto https://profiler.firefox.com (or run ``samply load <file>``). App-code frames appear as ``0x<hex>`` because Kotlin/Native release builds drop ``-g`` on the ``-opt`` conflict; system DLL symbols resolve via Microsoft's public symbol server.

