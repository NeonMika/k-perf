'use strict';
// Search a .cpuprofile for frames whose function name matches a pattern.
// Reports self-time, total-time, and the unique callers (parents) of each match.
// Usage: node find_frame.js <profile> <regex> [topCallers=10]

const fs = require('fs');
const inFile = process.argv[2];
const pattern = new RegExp(process.argv[3], 'i');
const topCallers = Number(process.argv[4] || 10);
if (!inFile || !process.argv[3]) {
  console.error('usage: node find_frame.js <profile> <regex> [topCallers]');
  process.exit(2);
}
const prof = JSON.parse(fs.readFileSync(inFile, 'utf8'));
const nodesById = new Map();
for (const n of prof.nodes) nodesById.set(n.id, n);
const parentById = new Map();
for (const n of prof.nodes) if (n.children) for (const c of n.children) parentById.set(c, n.id);

const selfTimeUs = new Map();
const samples = prof.samples || [];
const deltas = prof.timeDeltas || [];
for (let i = 0; i < samples.length; i++) {
  const id = samples[i];
  selfTimeUs.set(id, (selfTimeUs.get(id) || 0) + (deltas[i] || 0));
}
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

function abbrUrl(u) {
  if (!u) return '';
  const idx = u.lastIndexOf('build\\js\\');
  if (idx >= 0) return '...\\' + u.slice(idx + 'build\\js\\'.length);
  const idx2 = u.lastIndexOf('build/js/');
  if (idx2 >= 0) return '.../' + u.slice(idx2 + 'build/js/'.length);
  if (u.length > 70) return '...' + u.slice(-67);
  return u;
}

// Find all matching node ids.
const matches = prof.nodes.filter(n => {
  const name = (n.callFrame.functionName || '').toString();
  const url = (n.callFrame.url || '').toString();
  return pattern.test(name) || pattern.test(url);
});

console.log(`Found ${matches.length} matching node(s) for /${process.argv[3]}/`);
console.log('');

// Aggregate parents -> sum of (matching child total time)
const callerSelf = new Map();   // caller key -> self time spent IN match while caller is on stack
const callerTotal = new Map();  // caller key -> total time of matching subtrees called from this caller
const callerHits = new Map();
for (const m of matches) {
  const pid = parentById.get(m.id);
  if (pid == null) continue;
  const p = nodesById.get(pid);
  const cf = p.callFrame;
  const key = `${cf.functionName || '(anonymous)'}\t${cf.url || ''}:${cf.lineNumber}`;
  callerSelf.set(key, (callerSelf.get(key) || 0) + (selfTimeUs.get(m.id) || 0));
  callerTotal.set(key, (callerTotal.get(key) || 0) + (totalTimeUs.get(m.id) || 0));
  callerHits.set(key, (callerHits.get(key) || 0) + (m.hitCount || 0));
}

// Show aggregated stats for the matched function
let totalMatchSelf = 0, totalMatchTotal = 0, totalMatchHits = 0;
for (const m of matches) {
  totalMatchSelf += selfTimeUs.get(m.id) || 0;
  totalMatchTotal += totalTimeUs.get(m.id) || 0;
  totalMatchHits += m.hitCount || 0;
}
console.log(`Aggregate match: self=${(totalMatchSelf/1000).toFixed(1)} ms, total=${(totalMatchTotal/1000).toFixed(1)} ms, hits=${totalMatchHits}`);
console.log('');

console.log(`=== Top ${topCallers} CALLERS of matched frames (by self-in-match) ===`);
console.log(`  match-self ms |  match-total ms |   hits  |  caller (file:line)`);
const rows = [...callerSelf.entries()]
  .sort((a, b) => b[1] - a[1])
  .slice(0, topCallers);
for (const [k, ms] of rows) {
  const [name, urlLine] = k.split('\t');
  const t = callerTotal.get(k) || 0;
  const h = callerHits.get(k) || 0;
  const ms2 = (ms/1000).toFixed(1).padStart(13);
  const tot2 = (t/1000).toFixed(1).padStart(15);
  const hh = String(h).padStart(7);
  console.log(`${ms2}  | ${tot2}   | ${hh}  | ${name}  (${abbrUrl(urlLine)})`);
}

// Walk a few stack chains upward from the heaviest matching nodes
console.log('');
console.log('=== Heaviest 5 matching call chains (root <- ... <- match) ===');
const heaviest = [...matches]
  .sort((a, b) => (selfTimeUs.get(b.id) || 0) - (selfTimeUs.get(a.id) || 0))
  .slice(0, 5);
for (const m of heaviest) {
  const chain = [];
  let cur = m.id;
  let depth = 0;
  while (cur != null && depth < 12) {
    const n = nodesById.get(cur);
    chain.push(`${n.callFrame.functionName || '(anon)'}@${abbrUrl(n.callFrame.url || '')}:${n.callFrame.lineNumber}`);
    cur = parentById.get(cur);
    depth++;
  }
  console.log(`[self ${(selfTimeUs.get(m.id)/1000).toFixed(1)}ms]`);
  for (let i = chain.length - 1; i >= 0; i--) console.log(`  ${'  '.repeat(chain.length - 1 - i)}${chain[i]}`);
  console.log('');
}
