# Profile -- otel-proto-timesource (Protobuf/gRPC + monotonic clock) (native)

> WARNING: capture failed in this run; analyzing the previously captured profile instead.
> Profile file was last written at **2026-05-20 20:34:07** -- not from the most recent `run-all-profiles.ps1` invocation.
> Common cause on Windows 11: Smart App Control blocks unsigned `samply.exe`.

**Variant:** `otel-proto-timesource`  
**Platform:** native  
**SUMMARY rendered:** 2026-05-20 20:42:40  
**Profile file last captured:** 2026-05-20 20:34:07  
**Profile file:** [otel-proto-timesource.perfView.xml.zip](otel-proto-timesource.perfView.xml.zip)  
**Wall time (capture run):** n/a (capture skipped or failed)  
**Workload-reported time:** unknown (no marker line in stdout)  

---

## Top 30 frames

```
Profile: otel-proto-timesource.perfView.xml.zip
Frames: 40, Stacks: 544, Samples: 29, recorded weight: 0.04 s

=== Top 30 by SELF time ===
  self ms | total ms |   hits | function
     29  |       31  |     17  | c:\windows\system32\ntoskrnl!?
      5  |       40  |      5  | c:\windows\system32\ntdll!?
      2  |       19  |      2  | ...\comparison-otel-proto-timesource\build\bin\mingwx64\releaseexecutable\main!?
      1  |        1  |      1  | c:\windows\system32\drivers\ndis.sys!?
      1  |        2  |      1  | c:\windows\system32\drivers\fltmgr.sys!?
      1  |        1  |      1  | c:\windows\system32\drivers\wd\wdfilter.sys!?
      1  |       21  |      1  | c:\windows\system32\kernel32!?
      1  |        5  |      1  | c:\windows\system32\kernelbase!?

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |   hits | function
      0  |       41  |      0  | Process64 main (9756) Args: 
      5  |       40  |      5  | c:\windows\system32\ntdll!?
      0  |       33  |      0  | ?!?
     29  |       31  |     17  | c:\windows\system32\ntoskrnl!?
      0  |       27  |      0  | Thread (2580) CPU=16ms (Startup Thread)
      1  |       21  |      1  | c:\windows\system32\kernel32!?
      2  |       19  |      2  | ...\comparison-otel-proto-timesource\build\bin\mingwx64\releaseexecutable\main!?
      0  |        6  |      0  | Thread (5728) CPU=6ms (tokio-rt-worker)
      1  |        5  |      1  | c:\windows\system32\kernelbase!?
      0  |        5  |      0  | c:\windows\system32\ws2_32!?
      0  |        3  |      0  | c:\windows\system32\drivers\condrv.sys!?
      0  |        3  |      0  | c:\windows\system32\drivers\tcpip.sys!?
      0  |        3  |      0  | c:\windows\system32\drivers\afd.sys!?
      0  |        2  |      0  | c:\windows\system32\drivers\appid.sys!?
      0  |        2  |      0  | Thread (11456) CPU=1ms (tokio-rt-worker)
      1  |        2  |      1  | c:\windows\system32\drivers\fltmgr.sys!?
      0  |        2  |      0  | Thread (22932) CPU=2ms
      0  |        2  |      0  | c:\windows\system32\rpcrt4!?
      0  |        2  |      0  | c:\windows\system32\mswsock!?
      0  |        2  |      0  | c:\windows\system32\msvcrt!?
      0  |        1  |      0  | c:\windows\system32\drivers\netio.sys!?
      0  |        1  |      0  | Thread (20828) CPU=1ms (tokio-rt-worker)
      1  |        1  |      1  | c:\windows\system32\drivers\ndis.sys!?
      0  |        1  |      0  | c:\windows\system32\drivers\storqosflt.sys!?
      0  |        1  |      0  | c:\windows\system32\drivers\bfs.sys!?
      0  |        1  |      0  | c:\windows\system32\drivers\bindflt.sys!?
      0  |        1  |      0  | c:\windows\system32\fwpuclnt!?
      1  |        1  |      1  | c:\windows\system32\drivers\wd\wdfilter.sys!?
      0  |        1  |      0  | c:\windows\system32\dnsapi!?
      0  |        1  |      0  | BROKEN

=== Self-time attributed by MODULE / loaded binary ===
  self ms |    %  |   hits | module
     29  |  71.1%  |     17  | ntoskrnl
      5  |  12.7%  |      5  | ntdll
      2  |   4.7%  |      2  | main
      1  |   2.9%  |      1  | ndis
      1  |   2.5%  |      1  | fltmgr
      1  |   2.2%  |      1  | wdfilter
      1  |   2.1%  |      1  | kernel32
      1  |   1.7%  |      1  | kernelbase
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/
Matched 0 of 29 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/
Matched 0 of 29 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Pattern /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/
Matched 0 of 29 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/
Matched 0 of 29 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

---

## How to view interactively

Drag ``otel-proto-timesource.perfView.xml.zip`` onto https://profiler.firefox.com (or run ``samply load <file>``). App-code frames appear as ``0x<hex>`` because Kotlin/Native release builds drop ``-g`` on the ``-opt`` conflict; system DLL symbols resolve via Microsoft's public symbol server.

