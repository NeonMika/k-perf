'use strict';
// Search a JFR recording for execution-sample frames whose method name
// matches a regex; report aggregate self/total time, top callers, and
// heaviest call chains. Mirrors profiles/find_frame.js for JS profiles.
//
// Usage: node jvm_find_frame.js <recording.jfr> <regex> [topCallers=10] [intervalMs=10]

const { spawnSync } = require('child_process');

const inFile = process.argv[2];
const pattern = process.argv[3] && new RegExp(process.argv[3], 'i');
const topCallers = Number(process.argv[4] || 10);
const intervalMs = Number(process.argv[5] || 10);
if (!inFile || !pattern) {
  console.error('usage: node jvm_find_frame.js <recording.jfr> <regex> [topCallers] [intervalMs]');
  process.exit(2);
}

const r = spawnSync('jfr', ['print', '--json', '--events', 'jdk.ExecutionSample', inFile],
                    { maxBuffer: 1024 * 1024 * 512, encoding: 'utf8' });
if (r.status !== 0) { console.error(r.stderr); process.exit(r.status || 1); }
const events = JSON.parse(r.stdout).recording.events;

// Empty recordings (workload shorter than JFR's safepoint sampling latency)
// are normal; report cleanly and exit 0 so batch runners stay alive.
if (!events || events.length === 0) {
  console.log(`Pattern /${process.argv[3]}/ matched 0 samples (recording has no jdk.ExecutionSample events).`);
  process.exit(0);
}

function frameKey(f) {
  const cls = (f.method && f.method.type && f.method.type.name) || '?';
  const m = (f.method && f.method.name) || '?';
  return `${cls}.${m}`;
}

let matchingSamples = 0;
let selfMs = 0;
const callerSelf = new Map();   // caller (frame[i+1]) -> self ms while match was on top
const callerTotal = new Map();  // caller -> total ms across stacks where match appears
const chains = []; // [{topMatch, chainKeys}]

for (const ev of events) {
  const frames = (ev.values && ev.values.stackTrace && ev.values.stackTrace.frames) || [];
  if (frames.length === 0) continue;
  // Find the topmost frame matching the pattern.
  let matchIdx = -1;
  for (let i = 0; i < frames.length; i++) {
    if (pattern.test(frameKey(frames[i]))) { matchIdx = i; break; }
  }
  if (matchIdx < 0) continue;
  matchingSamples++;
  // self-in-match is intervalMs only when match is on top
  if (matchIdx === 0) selfMs += intervalMs;
  // caller is the next frame outward (or "(root)" if match is the bottom)
  const callerKey = matchIdx + 1 < frames.length ? frameKey(frames[matchIdx + 1]) : '(root)';
  if (matchIdx === 0) callerSelf.set(callerKey, (callerSelf.get(callerKey) || 0) + intervalMs);
  callerTotal.set(callerKey, (callerTotal.get(callerKey) || 0) + intervalMs);

  if (chains.length < 200) {
    chains.push({
      onTop: matchIdx === 0,
      key: frameKey(frames[matchIdx]),
      chain: frames.slice(matchIdx).map(frameKey)
    });
  }
}

const totalMs = matchingSamples * intervalMs;
console.log(`Pattern /${process.argv[3]}/ matched ${matchingSamples} samples`);
console.log(`Aggregate: self=${selfMs} ms (on top), total=${totalMs} ms (anywhere in stack)`);
console.log('');

console.log(`=== Top ${topCallers} CALLERS ===`);
console.log('  match-self ms | match-total ms | caller');
const rows = [...callerTotal.entries()].sort((a, b) => b[1] - a[1]).slice(0, topCallers);
for (const [k, t] of rows) {
  const s = (callerSelf.get(k) || 0);
  console.log(`${String(s).padStart(13)}  | ${String(t).padStart(14)}  | ${k}`);
}
console.log('');

console.log('=== 5 sample chains (root <- ... <- match) ===');
const onTopChains = chains.filter(c => c.onTop).slice(0, 5);
for (const c of onTopChains) {
  console.log(`[match: ${c.key}]`);
  for (let i = c.chain.length - 1; i >= 0; i--) console.log(`  ${'  '.repeat(c.chain.length - 1 - i)}${c.chain[i]}`);
  console.log('');
}
