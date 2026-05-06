'use strict';
// Reads a Java Flight Recorder (.jfr) file via the JDK's `jfr` CLI and prints
// top frames by self-time and total-time, mirroring profiles/analyze.js.
//
// Each `jdk.ExecutionSample` event in the recording is one stack snapshot of a
// runnable thread. With the JFR `profile` settings the sample period is 10 ms,
// so we approximate self-time = (samples-with-frame-on-top) * 10 ms and
// total-time = (samples-with-frame-anywhere-in-stack) * 10 ms.
//
// Usage: node jvm_analyze.js <recording.jfr> [topN=30] [intervalMs=10]

const { spawnSync } = require('child_process');
const path = require('path');

const inFile = process.argv[2];
const topN = Number(process.argv[3] || 30);
const intervalMs = Number(process.argv[4] || 10);
if (!inFile) {
  console.error('usage: node jvm_analyze.js <recording.jfr> [topN] [intervalMs]');
  process.exit(2);
}

const r = spawnSync('jfr', ['print', '--json', '--events', 'jdk.ExecutionSample', inFile],
                    { maxBuffer: 1024 * 1024 * 512, encoding: 'utf8' });
if (r.status !== 0) {
  console.error('jfr CLI failed:');
  console.error(r.stderr);
  process.exit(r.status || 1);
}
const data = JSON.parse(r.stdout);
const events = (data && data.recording && data.recording.events) || [];

if (events.length === 0) {
  // Common when the workload is shorter than JFR's safepoint-sampling latency
  // (e.g. JVM k-perf at ~1 s). Report cleanly and exit 0 so batch runners can
  // continue past this profile and the SUMMARY.md still gets written.
  console.log(`Profile: ${path.basename(inFile)}`);
  console.log('No jdk.ExecutionSample events in recording.');
  console.log('Likely causes: workload too short for JFR sampling, or recording started without settings=profile.');
  process.exit(0);
}

function frameKey(f) {
  const cls = (f.method && f.method.type && f.method.type.name) || '?';
  const m = (f.method && f.method.name) || '?';
  return `${cls}.${m}`;
}

const selfMs = new Map();
const totalMs = new Map();
const hits = new Map();

for (const ev of events) {
  const frames = (ev.values && ev.values.stackTrace && ev.values.stackTrace.frames) || [];
  if (frames.length === 0) continue;
  // frames[0] is the topmost (currently executing) frame -> self-time.
  const top = frameKey(frames[0]);
  selfMs.set(top, (selfMs.get(top) || 0) + intervalMs);
  // Each frame in the stack accrues total-time from this sample once,
  // dedup'd so a recursive method doesn't over-count.
  const seen = new Set();
  for (const f of frames) {
    const k = frameKey(f);
    if (seen.has(k)) continue;
    seen.add(k);
    totalMs.set(k, (totalMs.get(k) || 0) + intervalMs);
  }
  hits.set(top, (hits.get(top) || 0) + 1);
}

const totalSamples = events.length;
const wallMs = totalSamples * intervalMs;
console.log(`Profile: ${path.basename(inFile)}`);
console.log(`Wall (samples*${intervalMs}ms): ${(wallMs/1000).toFixed(1)} s, ${totalSamples} samples`);
console.log('');

function abbr(name) {
  // Trim noisy package prefixes for display.
  return name.length > 70 ? '...' + name.slice(-67) : name;
}

function table(title, m, sortMap) {
  console.log(`=== Top ${topN} by ${title} ===`);
  const rows = [...m.entries()]
    .map(([k, v]) => ({ k, self: selfMs.get(k) || 0, total: totalMs.get(k) || 0, hits: hits.get(k) || 0 }))
    .sort((a, b) => sortMap.get(b.k) - sortMap.get(a.k))
    .slice(0, topN);
  console.log('  self ms | total ms |  samples | function');
  for (const r of rows) {
    const s = String(r.self).padStart(7);
    const t = String(r.total).padStart(8);
    const h = String(r.hits).padStart(8);
    console.log(`${s}  | ${t}  | ${h}  | ${abbr(r.k)}`);
  }
  console.log('');
}

table('SELF time', selfMs, selfMs);
table('TOTAL (inclusive) time', totalMs, totalMs);
