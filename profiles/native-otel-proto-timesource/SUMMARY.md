# Profile -- otel-proto-timesource (Protobuf/gRPC + monotonic clock) (native)

**Variant:** `otel-proto-timesource`  
**Platform:** native  
**SUMMARY rendered:** 2026-05-05 22:28:19  
**Profile file last captured:** 2026-05-05 22:28:18  
**Profile file:** [otel-proto-timesource.perfView.xml.zip](otel-proto-timesource.perfView.xml.zip)  
**Wall time (capture run):** 0 ms (incl. profiler overhead)  
**Workload-reported time:** 6364 ms  

---

## Top 30 frames

```
Profile: otel-proto-timesource.perfView.xml.zip
Frames: 45, Stacks: 2104, Samples: 7121, recorded weight: 6.13 s

=== Top 30 by SELF time ===
  self ms | total ms |   hits | function
   5362  |     6101  |   6190  | ...\comparison-otel-proto-timesource\build\bin\mingwx64\releaseexecutable\main!?
    308  |     5946  |    365  | c:\windows\system32\kernel32!?
    203  |     5968  |    265  | c:\windows\system32\ntdll!?
    139  |      156  |    144  | c:\windows\system32\ntoskrnl!?
     95  |      471  |    116  | c:\windows\system32\msvcrt!?
      9  |       22  |     20  | c:\windows\system32\drivers\tcpip.sys!?
      4  |       84  |      6  | c:\windows\system32\kernelbase!?
      3  |       26  |      7  | c:\windows\system32\drivers\afd.sys!?
      2  |        6  |      3  | c:\windows\system32\drivers\netio.sys!?
      2  |        2  |      3  | c:\windows\system32\drivers\ndis.sys!?
      0  |        0  |      1  | c:\windows\system32\drivers\ndu.sys!?
      0  |        1  |      1  | c:\windows\system32\drivers\fltmgr.sys!?

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |   hits | function
      0  |     6127  |      0  | Process64 main (4844) Args: 
   5362  |     6101  |   6190  | ...\comparison-otel-proto-timesource\build\bin\mingwx64\releaseexecutable\main!?
    203  |     5968  |    265  | c:\windows\system32\ntdll!?
    308  |     5946  |    365  | c:\windows\system32\kernel32!?
      0  |     5379  |      0  | Thread (29552) CPU=5884ms (Startup Thread)
     95  |      471  |    116  | c:\windows\system32\msvcrt!?
      0  |      281  |      0  | Thread (30632) CPU=353ms
      0  |      159  |      0  | BROKEN
    139  |      156  |    144  | c:\windows\system32\ntoskrnl!?
      0  |      140  |      0  | Thread (30368) CPU=221ms (tokio-rt-worker)
      0  |      107  |      0  | Thread (27720) CPU=210ms (tokio-rt-worker)
      0  |       89  |      0  | ?!?
      4  |       84  |      6  | c:\windows\system32\kernelbase!?
      0  |       83  |      0  | Thread (9824) CPU=173ms (tokio-rt-worker)
      0  |       47  |      0  | Thread (13588) CPU=90ms
      0  |       32  |      0  | c:\windows\system32\ws2_32!?
      0  |       29  |      0  | c:\windows\system32\mswsock!?
      3  |       26  |      7  | c:\windows\system32\drivers\afd.sys!?
      9  |       22  |     20  | c:\windows\system32\drivers\tcpip.sys!?
      0  |       17  |      0  | Thread (19560) CPU=35ms
      0  |       14  |      0  | Thread (28212) CPU=33ms
      0  |       14  |      0  | Thread (29852) CPU=31ms
      0  |       13  |      0  | Thread (18644) CPU=27ms
      0  |       12  |      0  | Thread (25752) CPU=22ms
      0  |       11  |      0  | Thread (19588) CPU=33ms
      2  |        6  |      3  | c:\windows\system32\drivers\netio.sys!?
      0  |        5  |      0  | Thread (21780) CPU=7ms (tokio-rt-worker)
      0  |        3  |      0  | c:\windows\system32\drivers\condrv.sys!?
      2  |        2  |      3  | c:\windows\system32\drivers\ndis.sys!?
      0  |        2  |      0  | Thread (20216) CPU=2ms

=== Self-time attributed by MODULE / loaded binary ===
  self ms |    %  |   hits | module
   5362  |  87.5%  |   6190  | main
    308  |   5.0%  |    365  | kernel32
    203  |   3.3%  |    265  | ntdll
    139  |   2.3%  |    144  | ntoskrnl
     95  |   1.5%  |    116  | msvcrt
      9  |   0.1%  |     20  | tcpip
      4  |   0.1%  |      6  | kernelbase
      3  |   0.1%  |      7  | afd
      2  |   0.0%  |      3  | netio
      2  |   0.0%  |      3  | ndis
      0  |   0.0%  |      1  | ndu
      0  |   0.0%  |      1  | fltmgr
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/
Matched 0 of 7121 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/
Matched 0 of 7121 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Pattern /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/
Matched 0 of 7121 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/
Matched 0 of 7121 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

---

## How to view interactively

Drag ``otel-proto-timesource.perfView.xml.zip`` onto https://profiler.firefox.com (or run ``samply load <file>``). App-code frames appear as ``0x<hex>`` because Kotlin/Native release builds drop ``-g`` on the ``-opt`` conflict; system DLL symbols resolve via Microsoft's public symbol server.

