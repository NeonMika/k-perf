'use strict';
// Reads a PerfView CPU-stacks XML zip (`*.perfView.xml.zip`) produced by
// `PerfView.exe UserCommand SaveCPUStacks <etl.zip>` and prints top frames
// by self-time + total-time, plus a per-module attribution table.
//
// PerfView XML stacks format (locale-independent, structured):
//   <Frames>
//     <Frame ID="N">name</Frame>     // typically "<full-path-to-module>!<symbol>"
//   </Frames>
//   <Stacks>
//     <Stack ID="N" CallerID="M" FrameID="K"/>   // each stack node points at a frame
//   </Stacks>                                     //  + its parent stack (chain via CallerID)
//   <Samples>
//     <Sample ID="N" Time="ms" StackID="K"/>     // one CPU sample, points at leaf stack
//   </Samples>
//
// Self-time of a frame  = sum of sample weights where leaf-frame == that frame.
// Total-time of a frame = sum of sample weights where the frame appears anywhere
//                         in the leaf->root chain (de-duped per sample so recursion
//                         doesn't over-count).
// Per-module attribution = self-time aggregated by module name extracted from the
//                          frame label (e.g. ".../ntdll!?"  -> "ntdll").
//
// Usage: node native_analyze.js <profile.perfView.xml.zip> [topN=30]

const fs = require('fs');
const zlib = require('zlib');
const path = require('path');

const inFile = process.argv[2];
const topN = Number(process.argv[3] || 30);
if (!inFile) {
  console.error('usage: node native_analyze.js <profile.perfView.xml.zip> [topN]');
  process.exit(2);
}

// --- read the file. Accept .xml, .xml.zip, or .xml.gz transparently.
function readXml(file) {
  const buf = fs.readFileSync(file);
  if (file.endsWith('.gz') || (buf[0] === 0x1f && buf[1] === 0x8b)) {
    return zlib.gunzipSync(buf).toString('utf8');
  }
  // ZIP signature 'PK\x03\x04'
  if (buf[0] === 0x50 && buf[1] === 0x4b && buf[2] === 0x03 && buf[3] === 0x04) {
    // Inflate the first (and only, in PerfView's case) entry. We do this by
    // hand to avoid taking an npm dependency: ZIP local-file-header is at offset 0,
    // followed by deflate data. PerfView's zips contain a single deflate stream.
    return readSingleEntryZip(buf);
  }
  return buf.toString('utf8');
}

// Minimal ZIP reader for single-entry ZIPs. PerfView writes one entry per zip.
// Local file header: 4-byte sig, 2 version, 2 flags, 2 method, 2/2 mtime/mdate,
// 4 crc, 4 compressedSize, 4 uncompressedSize, 2 filenameLen, 2 extraLen, then name+extra+data.
function readSingleEntryZip(buf) {
  if (buf.readUInt32LE(0) !== 0x04034b50) throw new Error('not a zip');
  const compMethod = buf.readUInt16LE(8);          // 0 = stored, 8 = deflate
  const compressedSize = buf.readUInt32LE(18);
  const fileNameLen = buf.readUInt16LE(26);
  const extraLen = buf.readUInt16LE(28);
  const dataStart = 30 + fileNameLen + extraLen;
  const dataEnd = dataStart + compressedSize;
  const data = buf.slice(dataStart, dataEnd);
  if (compMethod === 0) return data.toString('utf8');
  if (compMethod === 8) return zlib.inflateRawSync(data).toString('utf8');
  throw new Error(`unsupported zip compression method ${compMethod}`);
}

const xml = readXml(inFile);

// --- regex parse the three tables. The XML is regular and self-closing for
// stacks/samples, so regex is reliable here and avoids an XML-parser dep.
const frames = new Map();   // id -> name string
const stacks = new Map();   // id -> { callerId, frameId }
const samples = [];         // [{ time, stackId }]

const reFrame  = /<Frame ID="(\d+)">([\s\S]*?)<\/Frame>/g;
const reStack  = /<Stack\s+ID="(\d+)"\s+CallerID="(-?\d+)"\s+FrameID="(\d+)"\s*\/>/g;
const reSample = /<Sample\s+ID="\d+"\s+Time="([\d.]+)"\s+StackID="(\d+)"\s*\/>/g;

let m;
while ((m = reFrame.exec(xml)) !== null) {
  // Decode &lt; / &gt; / &amp; etc. since frame names can contain these.
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
  console.log(`Profile: ${path.basename(inFile)}`);
  console.log('No CPU samples in profile (workload may have been too short, or capture failed).');
  process.exit(0);
}

// --- compute weights from time deltas. PerfView samples ~1 ms apart by default.
// We use successive deltas; the first sample inherits the second's delta.
samples.sort((a, b) => a.time - b.time);
const weights = new Float64Array(samples.length);
for (let i = 0; i < samples.length; i++) {
  weights[i] = (i > 0) ? Math.max(0, samples[i].time - samples[i - 1].time) : 0;
}
// Smear sample 0's weight from sample 1's delta (avoids 0-weight first sample).
if (samples.length > 1) weights[0] = weights[1];
const totalWeight = weights.reduce((a, b) => a + b, 0);

// --- aggregate. For each sample: walk the stack chain via CallerID (-1 = root sentinel,
// which we skip). Top frame (leaf) gets self-time; every distinct frame in the chain
// gets total-time once.
const selfMs = new Map();
const totalMs = new Map();
const hits = new Map();

for (let i = 0; i < samples.length; i++) {
  const w = weights[i];
  const leafStack = stacks.get(samples[i].stackId);
  if (!leafStack) continue;
  const leafName = frames.get(leafStack.frameId) || `(frame#${leafStack.frameId})`;

  selfMs.set(leafName, (selfMs.get(leafName) || 0) + w);
  hits.set(leafName, (hits.get(leafName) || 0) + 1);

  const seen = new Set();
  let cur = samples[i].stackId;
  let depth = 0;
  while (cur != null && cur !== -1 && depth < 4096) {
    const s = stacks.get(cur);
    if (!s) break;
    const nm = frames.get(s.frameId) || `(frame#${s.frameId})`;
    if (!seen.has(nm)) {
      seen.add(nm);
      totalMs.set(nm, (totalMs.get(nm) || 0) + w);
    }
    cur = s.callerId;
    depth++;
  }
}

// --- output ---
console.log(`Profile: ${path.basename(inFile)}`);
console.log(`Frames: ${frames.size}, Stacks: ${stacks.size}, Samples: ${samples.length}, recorded weight: ${(totalWeight/1000).toFixed(2)} s`);
console.log('');

function abbr(s) { return s.length > 80 ? '...' + s.slice(-77) : s; }

function table(title, m) {
  console.log(`=== Top ${topN} by ${title} ===`);
  const rows = [...m.entries()].sort((a, b) => b[1] - a[1]).slice(0, topN);
  console.log('  self ms | total ms |   hits | function');
  for (const [k, v] of rows) {
    const s = (selfMs.get(k) || 0).toFixed(0).padStart(7);
    const t = (totalMs.get(k) || 0).toFixed(0).padStart(8);
    const h = String(hits.get(k) || 0).padStart(6);
    console.log(`${s}  | ${t}  | ${h}  | ${abbr(k)}`);
  }
  console.log('');
}

table('SELF time', selfMs);
table('TOTAL (inclusive) time', totalMs);

// --- per-module attribution. Frame names are like:
//   "c:\\windows\\system32\\ntdll!?"          ->  module "ntdll"
//   "ntoskrnl!?"                              ->  module "ntoskrnl"
//   "comparison-k-perf-flushearly-true!?"     ->  module "comparison-k-perf-flushearly-true"
//   "Process64 foo (1234) Args: ..."          ->  module "(process)"     (PerfView synthetic)
//   "Thread (1234) CPU=Nms"                   ->  module "(thread)"      (PerfView synthetic)
//   "ROOT"/"BROKEN"/"OVERHEAD"/"?!?"          ->  module "(unresolved)"  (PerfView sentinels)
function moduleOf(frameName) {
  if (!frameName) return '(unknown)';
  if (frameName === 'ROOT' || frameName === 'BROKEN' || frameName === 'OVERHEAD' || frameName === '?!?') return '(unresolved)';
  if (frameName.startsWith('Process')) return '(process)';
  if (frameName.startsWith('Thread ')) return '(thread)';
  // Otherwise "[<path>]<module>!<symbol>". Strip path, strip extension, strip "!<symbol>".
  let s = frameName;
  const bang = s.indexOf('!');
  if (bang >= 0) s = s.slice(0, bang);
  // Last path component
  const lastSlash = Math.max(s.lastIndexOf('\\'), s.lastIndexOf('/'));
  if (lastSlash >= 0) s = s.slice(lastSlash + 1);
  // Strip extension
  s = s.replace(/\.(exe|dll|sys)$/i, '');
  return s || '(unknown)';
}

const moduleSelfMs = new Map();
const moduleHits = new Map();
for (const [name, ms] of selfMs.entries()) {
  const mod = moduleOf(name);
  moduleSelfMs.set(mod, (moduleSelfMs.get(mod) || 0) + ms);
  moduleHits.set(mod, (moduleHits.get(mod) || 0) + (hits.get(name) || 0));
}

console.log('=== Self-time attributed by MODULE / loaded binary ===');
console.log('  self ms |    %  |   hits | module');
const totalW = totalWeight || 1;
[...moduleSelfMs.entries()]
  .sort((a, b) => b[1] - a[1])
  .forEach(([mod, ms]) => {
    const pct = (ms / totalW * 100).toFixed(1).padStart(5);
    const sStr = ms.toFixed(0).padStart(7);
    const h = String(moduleHits.get(mod) || 0).padStart(6);
    console.log(`${sStr}  | ${pct}%  | ${h}  | ${mod}`);
  });
