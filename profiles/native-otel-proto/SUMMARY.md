# Profile -- otel-proto (Protobuf/gRPC) (native)

**Variant:** `otel-proto`  
**Platform:** native  
**SUMMARY rendered:** 2026-06-13 17:36:15  
**Profile file last captured:** 2026-06-13 17:36:14  
**Profile file:** [otel-proto.perfView.xml.zip](otel-proto.perfView.xml.zip)  
**Wall time (capture run):** 0 ms (incl. profiler overhead)  
**Workload-reported time:** 6406 ms  

---

## Top 30 frames

```
Profile: otel-proto.perfView.xml.zip
Frames: 52, Stacks: 2157, Samples: 7111, recorded weight: 6.15 s

=== Top 30 by SELF time ===
  self ms | total ms |   hits | function
   5384  |     6098  |   6153  | ...mp-examples\comparison-otel-proto\build\bin\mingwx64\releaseexecutable\main!?
    331  |     5922  |    379  | c:\windows\system32\kernel32!?
    234  |     5944  |    299  | c:\windows\system32\ntdll!?
    100  |      115  |    142  | c:\windows\system32\ntoskrnl!?
     80  |      434  |    103  | c:\windows\system32\msvcrt!?
     10  |       19  |     21  | c:\windows\system32\drivers\tcpip.sys!?
      2  |        4  |      4  | c:\windows\system32\drivers\netio.sys!?
      1  |        2  |      1  | c:\windows\system32\drivers\ntfs.sys!?
      1  |       20  |      2  | c:\windows\system32\drivers\afd.sys!?
      1  |       47  |      3  | c:\windows\system32\kernelbase!?
      1  |       26  |      1  | c:\windows\system32\ws2_32!?
      0  |        0  |      1  | c:\windows\system32\drivers\wdf01000.sys!?
      0  |       24  |      2  | c:\windows\system32\mswsock!?

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |   hits | function
      0  |     6146  |      0  | Process64 main (1604) Args: 
   5384  |     6098  |   6153  | ...mp-examples\comparison-otel-proto\build\bin\mingwx64\releaseexecutable\main!?
    234  |     5944  |    299  | c:\windows\system32\ntdll!?
    331  |     5922  |    379  | c:\windows\system32\kernel32!?
      0  |     5479  |      0  | Thread (27340) CPU=5963ms (Startup Thread)
     80  |      434  |    103  | c:\windows\system32\msvcrt!?
      0  |      218  |      0  | Thread (18100) CPU=278ms
      0  |      202  |      0  | BROKEN
      0  |      126  |      0  | Thread (14812) CPU=211ms (tokio-rt-worker)
      0  |      124  |      0  | ?!?
    100  |      115  |    142  | c:\windows\system32\ntoskrnl!?
      0  |       99  |      0  | Thread (18520) CPU=189ms (tokio-rt-worker)
      0  |       96  |      0  | Thread (24528) CPU=204ms (tokio-rt-worker)
      1  |       47  |      3  | c:\windows\system32\kernelbase!?
      0  |       45  |      0  | Thread (28784) CPU=80ms
      1  |       26  |      1  | c:\windows\system32\ws2_32!?
      0  |       24  |      2  | c:\windows\system32\mswsock!?
      1  |       20  |      2  | c:\windows\system32\drivers\afd.sys!?
     10  |       19  |     21  | c:\windows\system32\drivers\tcpip.sys!?
      0  |       15  |      0  | Thread (14968) CPU=36ms
      0  |       14  |      0  | Thread (21484) CPU=34ms
      0  |       14  |      0  | Thread (11400) CPU=27ms
      0  |       13  |      0  | Thread (33376) CPU=33ms
      0  |       12  |      0  | Thread (24944) CPU=28ms
      0  |        7  |      0  | Thread (17948) CPU=16ms
      2  |        4  |      4  | c:\windows\system32\drivers\netio.sys!?
      0  |        4  |      0  | Thread (30444) CPU=7ms (tokio-rt-worker)
      0  |        3  |      0  | c:\windows\system32\drivers\condrv.sys!?
      0  |        3  |      0  | c:\windows\system32\drivers\fltmgr.sys!?
      1  |        2  |      1  | c:\windows\system32\drivers\ntfs.sys!?

=== Self-time attributed by MODULE / loaded binary ===
  self ms |    %  |   hits | module
   5384  |  87.6%  |   6153  | main
    331  |   5.4%  |    379  | kernel32
    234  |   3.8%  |    299  | ntdll
    100  |   1.6%  |    142  | ntoskrnl
     80  |   1.3%  |    103  | msvcrt
     10  |   0.2%  |     21  | tcpip
      2  |   0.0%  |      4  | netio
      1  |   0.0%  |      1  | ntfs
      1  |   0.0%  |      2  | afd
      1  |   0.0%  |      3  | kernelbase
      1  |   0.0%  |      1  | ws2_32
      0  |   0.0%  |      1  | wdf01000
      0  |   0.0%  |      2  | mswsock
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/
Matched 0 of 7111 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/
Matched 0 of 7111 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Pattern /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/
Matched 0 of 7111 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/
Matched 0 of 7111 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

---

## How to view interactively

Drag ``otel-proto.perfView.xml.zip`` onto https://profiler.firefox.com (or run ``samply load <file>``). App-code frames appear as ``0x<hex>`` because Kotlin/Native release builds drop ``-g`` on the ``-opt`` conflict; system DLL symbols resolve via Microsoft's public symbol server.

