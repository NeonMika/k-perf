'use strict';
// Reads a Chrome/V8 .cpuprofile and prints top frames by self-time and by total-time.
// Usage: node analyze.js <path-to-.cpuprofile> [topN=40]

const fs = require('fs');
const path = require('path');

const inFile = process.argv[2];
const topN = Number(process.argv[3] || 40);
if (!inFile) {
  console.error('usage: node analyze.js <profile.cpuprofile> [topN]');
  process.exit(2);
}
const prof = JSON.parse(fs.readFileSync(inFile, 'utf8'));

const nodesById = new Map();
for (const n of prof.nodes) nodesById.set(n.id, n);

// childrenById: id -> Set of child ids (already in node.children, kept for symmetry)
// parentById: id -> parent id
const parentById = new Map();
for (const n of prof.nodes) {
  if (n.children) for (const c of n.children) parentById.set(c, n.id);
}

// self-time per node from samples + timeDeltas (deltas are microseconds)
const selfTimeUs = new Map();
const samples = prof.samples || [];
const deltas = prof.timeDeltas || [];
for (let i = 0; i < samples.length; i++) {
  const id = samples[i];
  const dt = deltas[i] || 0;
  selfTimeUs.set(id, (selfTimeUs.get(id) || 0) + dt);
}

// total-time per node = self + sum(children total)
const totalTimeUs = new Map();
function totalOf(id) {
  if (totalTimeUs.has(id)) return totalTimeUs.get(id);
  const n = nodesById.get(id);
  let t = selfTimeUs.get(id) || 0;
  if (n.children) for (const c of n.children) t += totalOf(c);
  totalTimeUs.set(id, t);
  return t;
}
for (const n of prof.nodes) totalOf(n.id);

// Aggregate by function key (functionName + url + lineNumber) so the same
// function across multiple call sites collapses into one row.
function frameKey(cf) {
  const name = cf.functionName || '(anonymous)';
  const url = cf.url || '';
  const line = cf.lineNumber == null ? -1 : cf.lineNumber;
  // url often empty for built-ins; bucket by name + url
  return `${name}\t${url}:${line}`;
}

const aggSelf = new Map();
const aggTotal = new Map();
const aggHit = new Map();
for (const n of prof.nodes) {
  const k = frameKey(n.callFrame);
  aggSelf.set(k, (aggSelf.get(k) || 0) + (selfTimeUs.get(n.id) || 0));
  aggTotal.set(k, (aggTotal.get(k) || 0) + (totalTimeUs.get(n.id) || 0));
  aggHit.set(k, (aggHit.get(k) || 0) + (n.hitCount || 0));
}

const totalUs = (prof.endTime - prof.startTime);
console.log(`Profile: ${path.basename(inFile)}`);
console.log(`Wall: ${(totalUs/1000).toFixed(1)} ms total, ${prof.nodes.length} nodes, ${samples.length} samples`);
console.log('');

function abbrUrl(u) {
  if (!u) return '';
  // Trim absolute path of bundle dir to keep table compact.
  const idx = u.lastIndexOf('build\\js\\');
  if (idx >= 0) return '...\\' + u.slice(idx + 'build\\js\\'.length);
  const idx2 = u.lastIndexOf('build/js/');
  if (idx2 >= 0) return '.../' + u.slice(idx2 + 'build/js/'.length);
  if (u.length > 60) return '...' + u.slice(-57);
  return u;
}

function formatTable(title, m, sortKey) {
  console.log(`=== Top ${topN} by ${title} ===`);
  const rows = [...m.entries()]
    .map(([k, v]) => {
      const [name, urlLine] = k.split('\t');
      const total = aggTotal.get(k) || 0;
      const self = aggSelf.get(k) || 0;
      const hits = aggHit.get(k) || 0;
      return { name, url: urlLine, self, total, hits };
    })
    .sort((a, b) => b[sortKey] - a[sortKey])
    .slice(0, topN);
  console.log('  self ms |  total ms |   hits |  function   (file)');
  for (const r of rows) {
    const selfMs = (r.self/1000).toFixed(1).padStart(8);
    const totMs = (r.total/1000).toFixed(1).padStart(9);
    const hits = String(r.hits).padStart(6);
    const nm = r.name.length > 50 ? r.name.slice(0, 47) + '...' : r.name;
    console.log(`${selfMs}  | ${totMs}  | ${hits}  | ${nm.padEnd(50)} (${abbrUrl(r.url)})`);
  }
  console.log('');
}

formatTable('SELF time', aggSelf, 'self');
formatTable('TOTAL (inclusive) time', aggTotal, 'total');
