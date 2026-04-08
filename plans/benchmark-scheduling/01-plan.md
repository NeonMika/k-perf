# Plan: Add automatic scheduling to benchmark workflows

**Status:** done
**Note:** Created retroactively — this plan was not written before implementation. That was a mistake.

## Task

Add automatic triggers to the four benchmark GitHub Actions workflows.

## Changes

| Workflow | Before | After |
|---|---|---|
| `benchmark-windows-small.yml` | `workflow_dispatch` only | `push` + `schedule: '0 0 * * *'` + `workflow_dispatch` |
| `benchmark-linux-small.yml` | `workflow_dispatch` only | `push` + `schedule: '0 0 * * *'` + `workflow_dispatch` |
| `benchmark-windows-large.yml` | `workflow_dispatch` only | `schedule: '0 0 * * *'` + `workflow_dispatch` |
| `benchmark-linux-large.yml` | `workflow_dispatch` only | `schedule: '0 0 * * *'` + `workflow_dispatch` |

## Notes

- Result-push commits use `[skip ci]` in their message, preventing re-trigger loops on the small benchmarks.
- Midnight UTC = `cron: '0 0 * * *'`.
- Large benchmarks are expensive (50 reps × 500 steps); not run on every push.

## Commit

```
ci: schedule benchmarks and run small suite on every push

Small benchmarks (Windows + Linux):
- Trigger on push (result commits use [skip ci] to avoid loops)
- Trigger on schedule: daily at midnight UTC (cron: '0 0 * * *')
- Keep workflow_dispatch for manual runs

Large benchmarks (Windows + Linux):
- Trigger on schedule: daily at midnight UTC (cron: '0 0 * * *')
- Keep workflow_dispatch for manual runs

Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>
```
