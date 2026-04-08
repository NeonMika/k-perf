# Plan: Restore full 6-step agentic workflow in copilot-instructions.md

**Status:** done

## Problem

The previous simplification of the Agentic Behavior section stripped the detailed
6-step workflow the user originally requested:
1. Make a clear plan → `01-plan.md`
2. Analyze requirements, lay out architecture/implementation → `02-architecture.md`
3. Plan how to test → `03-test-plan.md`
4. Implement
5. Test
6. Commit

The steps 2, 3, and 5 are now missing. The section also lost the file naming
convention, the status-tracking guidance, and the superseding rules for evolving plans.

## Changes to `.github/copilot-instructions.md`

Rewrite the `### Repository planning workflow` subsection to:

- Restore all 6 steps verbatim, mapped to their respective files
- Keep the hard gate ("never modify files before plan exists") added in the previous commit
- Restore the full file naming convention: `01-plan.md`, `02-architecture.md`, `03-test-plan.md`, `04-commit-message.md`
- Restore the status tracking rule (`active` / `superseded` / `done`)
- Restore the superseding rule (new file must reference the old, explain the change)
- Restore the "show plan path and ask for confirmation" rule
- Add that each step's output file should be shown to the user before proceeding to the next step
- Add the user's invitation for additional ideas as a note for future expansion

## Commit message

```
docs(instructions): restore full 6-step planning workflow

The simplification in the previous commit removed the detailed steps the
user explicitly requested: architecture analysis, test planning, and the
file naming convention. Restore and expand:

- All 6 steps with their output files (01-plan, 02-architecture,
  03-test-plan, 04-commit-message)
- Each step's output must be shown to the user before proceeding
- Status tracking (active / superseded / done)
- Superseding rules for evolving requirements
- Keep the hard gate added previously (no files before plan exists)

Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>
```
