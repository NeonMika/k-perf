# Profile -- otel-proto (Protobuf/gRPC) (native)

> WARNING: capture failed in this run; analyzing the previously captured profile instead.
> Profile file was last written at **2026-05-20 20:33:59** -- not from the most recent `run-all-profiles.ps1` invocation.
> Common cause on Windows 11: Smart App Control blocks unsigned `samply.exe`.

**Variant:** `otel-proto`  
**Platform:** native  
**SUMMARY rendered:** 2026-05-20 20:42:28  
**Profile file last captured:** 2026-05-20 20:33:59  
**Profile file:** [otel-proto.perfView.xml.zip](otel-proto.perfView.xml.zip)  
**Wall time (capture run):** n/a (capture skipped or failed)  
**Workload-reported time:** unknown (no marker line in stdout)  

---

## Top 30 frames

```
Profile: otel-proto.perfView.xml.zip
Frames: 34, Stacks: 543, Samples: 27, recorded weight: 0.04 s

=== Top 30 by SELF time ===
  self ms | total ms |   hits | function
     32  |       34  |     15  | c:\windows\system32\ntoskrnl!?
      3  |       41  |      5  | c:\windows\system32\ntdll!?
      3  |       17  |      5  | ...mp-examples\comparison-otel-proto\build\bin\mingwx64\releaseexecutable\main!?
      2  |        4  |      1  | c:\windows\system32\drivers\ntfs.sys!?
      1  |       37  |      1  | ?!?

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |   hits | function
      3  |       41  |      5  | c:\windows\system32\ntdll!?
      0  |       41  |      0  | Process64 main (5524) Args: 
      1  |       37  |      1  | ?!?
     32  |       34  |     15  | c:\windows\system32\ntoskrnl!?
      0  |       24  |      0  | Thread (26772) CPU=17ms (Startup Thread)
      0  |       21  |      0  | c:\windows\system32\kernel32!?
      3  |       17  |      5  | ...mp-examples\comparison-otel-proto\build\bin\mingwx64\releaseexecutable\main!?
      0  |        6  |      0  | c:\windows\system32\kernelbase!?
      0  |        5  |      0  | Thread (9240) CPU=6ms (tokio-rt-worker)
      0  |        5  |      0  | c:\windows\system32\ws2_32!?
      2  |        4  |      1  | c:\windows\system32\drivers\ntfs.sys!?
      0  |        4  |      0  | c:\windows\system32\drivers\fltmgr.sys!?
      0  |        3  |      0  | Thread (22240) CPU=2ms (tokio-rt-worker)
      0  |        3  |      0  | c:\windows\system32\drivers\luafv.sys!?
      0  |        3  |      0  | c:\windows\system32\drivers\bfs.sys!?
      0  |        3  |      0  | c:\windows\system32\drivers\bindflt.sys!?
      0  |        3  |      0  | c:\windows\system32\drivers\appid.sys!?
      0  |        3  |      0  | Thread (28012) CPU=1ms (tokio-rt-worker)
      0  |        3  |      0  | c:\windows\system32\drivers\condrv.sys!?
      0  |        2  |      0  | c:\windows\system32\mswsock!?
      0  |        2  |      0  | Thread (28220) CPU=1ms
      0  |        2  |      0  | c:\windows\system32\drivers\bam.sys!?
      0  |        2  |      0  | Thread (22184) CPU=2ms
      0  |        1  |      0  | c:\windows\system32\bcrypt!?
      0  |        1  |      0  | c:\windows\system32\rpcrt4!?
      0  |        1  |      0  | c:\windows\system32\fwpuclnt!?
      0  |        1  |      0  | Thread (26124) CPU=1ms (tokio-rt-worker)
      0  |        0  |      0  | c:\windows\system32\dnsapi!?
      0  |        0  |      0  | Thread (22864) CPU=1ms
      0  |        0  |      0  | Thread (28264) CPU=1ms

=== Self-time attributed by MODULE / loaded binary ===
  self ms |    %  |   hits | module
     32  |  78.2%  |     15  | ntoskrnl
      3  |   8.3%  |      5  | ntdll
      3  |   7.6%  |      5  | main
      2  |   4.6%  |      1  | ntfs
      1  |   1.3%  |      1  | (unresolved)
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/
Matched 0 of 27 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/
Matched 0 of 27 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Pattern /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/
Matched 0 of 27 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/
Matched 0 of 27 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

---

## How to view interactively

Drag ``otel-proto.perfView.xml.zip`` onto https://profiler.firefox.com (or run ``samply load <file>``). App-code frames appear as ``0x<hex>`` because Kotlin/Native release builds drop ``-g`` on the ``-opt`` conflict; system DLL symbols resolve via Microsoft's public symbol server.

