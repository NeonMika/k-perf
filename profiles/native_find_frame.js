'use strict';
// Search a PerfView CPU-stacks XML zip for frames whose name matches a regex;
// report aggregate self/total time, top callers, and heaviest call chains.
// Mirror of profiles/find_frame.js for V8 / jvm_find_frame.js for JFR.
//
// Usage: node native_find_frame.js <profile.perfView.xml.zip> <regex> [topCallers=10]

const fs = require('fs');
const zlib = require('zlib');

const inFile = process.argv[2];
const pattern = process.argv[3] && new RegExp(process.argv[3], 'i');
const topCallers = Number(process.argv[4] || 10);
if (!inFile || !pattern) {
  console.error('usage: node native_find_frame.js <profile.perfView.xml.zip> <regex> [topCallers]');
  process.exit(2);
}

function readXml(file) {
  const buf = fs.readFileSync(file);
  if (file.endsWith('.gz') || (buf[0] === 0x1f && buf[1] === 0x8b)) return zlib.gunzipSync(buf).toString('utf8');
  if (buf[0] === 0x50 && buf[1] === 0x4b && buf[2] === 0x03 && buf[3] === 0x04) return readSingleEntryZip(buf);
  return buf.toString('utf8');
}
function readSingleEntryZip(buf) {
  if (buf.readUInt32LE(0) !== 0x04034b50) throw new Error('not a zip');
  const compMethod = buf.readUInt16LE(8);
  const compressedSize = buf.readUInt32LE(18);
  const fileNameLen = buf.readUInt16LE(26);
  const extraLen = buf.readUInt16LE(28);
  const dataStart = 30 + fileNameLen + extraLen;
  const data = buf.slice(dataStart, dataStart + compressedSize);
  if (compMethod === 0) return data.toString('utf8');
  if (compMethod === 8) return zlib.inflateRawSync(data).toString('utf8');
  throw new Error(`unsupported zip compression method ${compMethod}`);
}

const xml = readXml(inFile);

const frames = new Map();
const stacks = new Map();
const samples = [];

const reFrame  = /<Frame ID="(\d+)">([\s\S]*?)<\/Frame>/g;
const reStack  = /<Stack\s+ID="(\d+)"\s+CallerID="(-?\d+)"\s+FrameID="(\d+)"\s*\/>/g;
const reSample = /<Sample\s+ID="\d+"\s+Time="([\d.]+)"\s+StackID="(\d+)"\s*\/>/g;

let m;
while ((m = reFrame.exec(xml)) !== null) {
  const name = m[2].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&').replace(/&quot;/g, '"');
  frames.set(Number(m[1]), name);
}
while ((m = reStack.exec(xml)) !== null) {
  stacks.set(Number(m[1]), { callerId: Number(m[2]), frameId: Number(m[3]) });
}
while ((m = reSample.exec(xml)) !== null) {
  samples.push({ time: Number(m[1]), stackId: Number(m[2]) });
}

if (samples.length === 0) {
  console.log(`Pattern /${process.argv[3]}/ matched 0 samples (profile has no CPU samples).`);
  process.exit(0);
}

samples.sort((a, b) => a.time - b.time);
const weights = new Float64Array(samples.length);
for (let i = 0; i < samples.length; i++) weights[i] = (i > 0) ? Math.max(0, samples[i].time - samples[i - 1].time) : 0;
if (samples.length > 1) weights[0] = weights[1];

function buildChain(stackId) {
  // Returns array of frame names from leaf to root.
  const out = [];
  let cur = stackId, depth = 0;
  while (cur != null && cur !== -1 && depth < 4096) {
    const s = stacks.get(cur);
    if (!s) break;
    out.push(frames.get(s.frameId) || `(frame#${s.frameId})`);
    cur = s.callerId;
    depth++;
  }
  return out;
}

let selfMs = 0, totalMs = 0, matchingSamples = 0;
const callerSelf = new Map();   // caller frame -> self ms while match was leaf
const callerTotal = new Map();  // caller frame -> total ms across stacks containing match
const onTopChains = [];          // up to 5 chains where match is the leaf

for (let i = 0; i < samples.length; i++) {
  const chain = buildChain(samples[i].stackId);
  let matchIdx = -1;
  for (let j = 0; j < chain.length; j++) {
    if (pattern.test(chain[j])) { matchIdx = j; break; }
  }
  if (matchIdx < 0) continue;
  matchingSamples++;
  const w = weights[i];
  totalMs += w;
  if (matchIdx === 0) selfMs += w;
  const callerKey = matchIdx + 1 < chain.length ? chain[matchIdx + 1] : '(root)';
  if (matchIdx === 0) callerSelf.set(callerKey, (callerSelf.get(callerKey) || 0) + w);
  callerTotal.set(callerKey, (callerTotal.get(callerKey) || 0) + w);
  if (matchIdx === 0 && onTopChains.length < 5) onTopChains.push({ key: chain[0], chain });
}

console.log(`Pattern /${process.argv[3]}/`);
console.log(`Matched ${matchingSamples} of ${samples.length} samples`);
console.log(`Aggregate: self=${selfMs.toFixed(0)} ms (on top), total=${totalMs.toFixed(0)} ms (anywhere in stack)`);
console.log('');

console.log(`=== Top ${topCallers} CALLERS ===`);
console.log('  match-self ms | match-total ms | caller');
[...callerTotal.entries()].sort((a, b) => b[1] - a[1]).slice(0, topCallers).forEach(([k, t]) => {
  const s = callerSelf.get(k) || 0;
  const ks = k.length > 80 ? '...' + k.slice(-77) : k;
  console.log(`${s.toFixed(0).padStart(13)}  | ${t.toFixed(0).padStart(14)}  | ${ks}`);
});
console.log('');

console.log('=== 5 sample chains (root <- ... <- match) ===');
for (const c of onTopChains) {
  const ks = c.key.length > 80 ? '...' + c.key.slice(-77) : c.key;
  console.log(`[match: ${ks}]`);
  for (let i = c.chain.length - 1; i >= 0; i--) {
    const fn = c.chain[i];
    const fs = fn.length > 80 ? '...' + fn.slice(-77) : fn;
    console.log(`  ${'  '.repeat(c.chain.length - 1 - i)}${fs}`);
  }
  console.log('');
}
