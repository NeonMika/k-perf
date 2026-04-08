# Plan: Add README badges and prominent plugin usage section

**Status:** active

## Problem statement

1. The README has no CI status badges — visitors cannot tell at a glance whether the build is green, when the last benchmark ran, or whether the publish pipeline is healthy.
2. The "apply the plugin in your project" snippet is buried in the Plugins section — it should be prominent near the top for first-time users.
3. There is no guidance in the Copilot instructions telling the agent to keep badges in sync when workflows change.

## Changes

### README.md — badges block

Inserted just below the repo subtitle (after the opening description and before the Table of Contents `---` rule). Groups:

| Group | Badge type | Count |
|---|---|---|
| Build (push/PR) | GitHub Actions | 6 |
| Benchmarks (manual) | GitHub Actions | 4 |
| Maven Central artifacts | shields.io | 3 |
| Publish workflow | GitHub Actions | 1 |

Build workflows covered: `build-all-on-{windows,ubuntu}{,-separated,-highly-separated}.yml`  
Benchmark workflows covered: `benchmark-{windows,linux}-{large,small}.yml`  
Maven Central artifacts: `io.github.neonmika:{k-perf,KIRHelperKit,instrumentation-overhead-analyzer}`  
Publish workflow: `publish.yml`

Tests are exercised inside the build workflows (Gradle runs JUnit 5) — no separate test badge needed.

### README.md — prominent usage section

New `## 🔌 Add k-perf to Your Project` section inserted immediately after "How It Works" (before Repository Structure). Contains:
- `settings.gradle.kts` repository block
- `build.gradle.kts` plugin apply + minimal `kperf {}` config
- Link to full config table in the Plugins section

### .github/copilot-instructions.md — badge maintenance rule

New `### README badges` rule in the `Agentic Behavior` section requiring the badge section to be updated whenever a GitHub Actions workflow is added, renamed, or removed.

## Commit message

```
docs: add CI badges, Maven Central badges, and prominent usage section

- Add GitHub Actions status badges for all 11 workflows (build,
  benchmark, publish) to the README header
- Add shields.io Maven Central version badges for all three published
  artifacts (k-perf, KIRHelperKit, instrumentation-overhead-analyzer)
- Add a prominent "Add k-perf to Your Project" section right after
  "How It Works" so the plugin apply snippet is visible without scrolling
- Add a README badge maintenance rule to .github/copilot-instructions.md
  requiring badges to be updated whenever workflows change

Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>
```
