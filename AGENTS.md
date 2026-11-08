
# FalCor Launcher — Development Conventions

## Project

FalCor is a private, native Android TV launcher for the Sony KD-50X85K.

The interface follows the established cinematic, remote-first design direction, with understated LCARS influences.

Primary destinations:
- Holodeck — Jellyfin Android TV client.
- DS9 — Switch the television to the DS9 HDMI input.
- Sport — Stan Sport and NBA viewing applications.
- Free-to-Air — Australian catch-up television applications.

DS9 has its own launcher. FalCor must not duplicate its gaming or system controls.

## Source of truth

The Git repository contains the authoritative project code and documentation. Chat history is not a substitute for version control.

## Collaboration

Development is performed collaboratively between the operator and an LLM. The operator executes commands, tests changes and manages Git commits.

Do not assume direct repository access or autonomous execution.

## Change management

- Make small, testable changes.
- Inspect existing files before editing.
- Preserve working functionality.
- Provide exact file paths.
- Do not invent test results or claim unverified functionality works.
- Explain destructive or irreversible operations before suggesting them.
- Do not store secrets in the repository.
- Avoid unnecessary dependencies or architectural complexity.
- Provide complete files when practical.

## Technical requirements

- Native Android TV application.
- Kotlin and Jetpack Compose for TV are preferred, subject to compatibility verification.
- D-pad and remote-first navigation.
- Configuration-driven destinations and actions.
- Sony-specific HDMI switching isolated behind a replaceable adapter.
- No unnecessary Home Assistant dependencies.
- Hidden maintenance/settings interface.
- Preserve access to standard Android TV functionality.

## Platform verification

Before depending on platform-specific functionality:

- Verify behaviour against the Sony KD-50X85K.
- Confirm Android version and API compatibility.
- Investigate HOME launcher registration and replacement restrictions.
- Verify HDMI input switching methods and permissions.
- Verify installed application launch mechanisms.
- Distinguish documented capabilities from tested device behaviour.

Do not invent Android APIs, package names, permissions or Sony-specific capabilities.

## Testing and recovery

- Test functionality incrementally on the actual television.
- Preserve a working method of restoring the previous launcher.
- Document rollback procedures before modifying default HOME behaviour.
- Do not disable system packages without a verified recovery procedure.
- Report testing status accurately.

## Documentation

- README.md — Installation, usage and architecture overview.
- AGENTS.md — Development rules and project constraints.
- ROADMAP.md — Planned, active and completed work.
- CHANGELOG.md — Significant implemented changes.
- docs/ — Architecture, device compatibility and testing notes.

## Session handover

When requested, produce a concise development handover containing:

- Current Git branch and commit, if known.
- Completed and tested work.
- Outstanding problems.
- Relevant files changed.
- Next agreed implementation step.

Do not treat planned work as completed work.
