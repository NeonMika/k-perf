# Profile -- otel (JSON/HTTP) (native)

> WARNING: capture failed in this run; analyzing the previously captured profile instead.
> Profile file was last written at **2026-05-20 20:33:51** -- not from the most recent `run-all-profiles.ps1` invocation.
> Common cause on Windows 11: Smart App Control blocks unsigned `samply.exe`.

**Variant:** `otel`  
**Platform:** native  
**SUMMARY rendered:** 2026-05-20 20:42:17  
**Profile file last captured:** 2026-05-20 20:33:51  
**Profile file:** [otel.perfView.xml.zip](otel.perfView.xml.zip)  
**Wall time (capture run):** n/a (capture skipped or failed)  
**Workload-reported time:** unknown (no marker line in stdout)  

---

## Top 30 frames

```
Profile: otel.perfView.xml.zip
Frames: 41, Stacks: 1013, Samples: 39, recorded weight: 0.05 s

=== Top 30 by SELF time ===
  self ms | total ms |   hits | function
     32  |       38  |     21  | c:\windows\system32\ntoskrnl!?
      9  |       53  |      6  | c:\windows\system32\ntdll!?
      5  |       29  |      6  | ...ject\kmp-examples\comparison-otel\build\bin\mingwx64\releaseexecutable\main!?
      2  |        8  |      1  | c:\windows\system32\drivers\fltmgr.sys!?
      2  |        2  |      1  | c:\windows\system32\drivers\tcpip.sys!?
      1  |       14  |      1  | c:\windows\system32\webio!?
      1  |        4  |      1  | c:\windows\system32\drivers\fileinfo.sys!?
      1  |        1  |      1  | c:\windows\system32\drivers\wd\wdfilter.sys!?
      1  |        5  |      1  | c:\windows\system32\drivers\appid.sys!?

=== Top 30 by TOTAL (inclusive) time ===
  self ms | total ms |   hits | function
      0  |       54  |      0  | Process64 main (20896) Args: 
      9  |       53  |      6  | c:\windows\system32\ntdll!?
      0  |       44  |      0  | ?!?
     32  |       38  |     21  | c:\windows\system32\ntoskrnl!?
      0  |       36  |      0  | c:\windows\system32\kernel32!?
      5  |       29  |      6  | ...ject\kmp-examples\comparison-otel\build\bin\mingwx64\releaseexecutable\main!?
      0  |       26  |      0  | Thread (25104) CPU=20ms (Startup Thread)
      0  |       17  |      0  | c:\windows\system32\winhttp!?
      1  |       14  |      1  | c:\windows\system32\webio!?
      0  |       14  |      0  | c:\windows\system32\msvcrt!?
      0  |       14  |      0  | Thread (27348) CPU=13ms
      0  |       12  |      0  | Thread (2208) CPU=7ms
      0  |       10  |      0  | c:\windows\system32\kernelbase!?
      2  |        8  |      1  | c:\windows\system32\drivers\fltmgr.sys!?
      1  |        5  |      1  | c:\windows\system32\drivers\appid.sys!?
      0  |        5  |      0  | c:\windows\system32\ws2_32!?
      0  |        4  |      0  | c:\windows\system32\mswsock!?
      1  |        4  |      1  | c:\windows\system32\drivers\fileinfo.sys!?
      0  |        3  |      0  | c:\windows\system32\drivers\condrv.sys!?
      0  |        3  |      0  | c:\windows\system32\iphlpapi!?
      0  |        2  |      0  | Thread (4228) CPU=2ms
      0  |        2  |      0  | c:\windows\system32\drivers\wof.sys!?
      2  |        2  |      1  | c:\windows\system32\drivers\tcpip.sys!?
      0  |        2  |      0  | c:\windows\system32\drivers\afd.sys!?
      0  |        1  |      0  | c:\windows\system32\dnsapi!?
      1  |        1  |      1  | c:\windows\system32\drivers\wd\wdfilter.sys!?
      0  |        1  |      0  | c:\windows\system32\combase!?
      0  |        1  |      0  | c:\windows\system32\drivers\partmgr.sys!?
      0  |        1  |      0  | c:\windows\system32\drivers\volmgr.sys!?
      0  |        1  |      0  | c:\windows\system32\drivers\fvevol.sys!?

=== Self-time attributed by MODULE / loaded binary ===
  self ms |    %  |   hits | module
     32  |  59.4%  |     21  | ntoskrnl
      9  |  17.3%  |      6  | ntdll
      5  |   9.0%  |      6  | main
      2  |   3.4%  |      1  | fltmgr
      2  |   2.8%  |      1  | tcpip
      1  |   2.4%  |      1  | webio
      1  |   2.3%  |      1  | fileinfo
      1  |   1.9%  |      1  | wdfilter
      1  |   1.7%  |      1  | appid
```

## Targeted suspect searches

### Clock / time-reading frames

Regex: `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant`

```
Pattern /now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant/
Matched 0 of 39 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Persistent-list / O(n^2) lookups

Regex: `AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2`

```
Pattern /AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2/
Matched 0 of 39 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### Long-polyfill arithmetic (JS only)

Regex: `^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$`

```
Pattern /^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$/
Matched 0 of 39 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

### OTel SDK Span construction

Regex: `Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor`

```
Pattern /Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor/
Matched 0 of 39 samples
Aggregate: self=0 ms (on top), total=0 ms (anywhere in stack)

=== Top 5 CALLERS ===
  match-self ms | match-total ms | caller

=== 5 sample chains (root <- ... <- match) ===
```

---

## How to view interactively

Drag ``otel.perfView.xml.zip`` onto https://profiler.firefox.com (or run ``samply load <file>``). App-code frames appear as ``0x<hex>`` because Kotlin/Native release builds drop ``-g`` on the ``-opt`` conflict; system DLL symbols resolve via Microsoft's public symbol server.

