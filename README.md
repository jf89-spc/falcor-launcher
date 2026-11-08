
# FalCor Launcher

**Home Entertainment Interface**

FalCor is a private, native Android TV launcher designed for the Sony KD-50X85K.

It provides a cinematic, remote-friendly interface to the existing home entertainment infrastructure, replacing the clutter of a conventional television home screen with a small number of clearly defined destinations.

## Project Status

**Phase: Foundation and platform verification**

The application has not yet been implemented.

Default launcher behaviour, HDMI input switching and device-specific Android TV compatibility require verification before implementation.

## Design

FalCor follows an understated futuristic aesthetic inspired by LCARS and cinematic science-fiction interfaces.

Design principles:

- Cinematic space backgrounds and restrained typography.
- Subtle LCARS-inspired visual elements.
- Clear, consistent focus states.
- Remote-first navigation using the television's D-pad.
- Minimal navigation depth and no unnecessary scrolling.
- Lightweight, responsive operation.
- Preserve access to standard Android TV functionality.

## Primary Destinations

### Holodeck

Launch the Jellyfin Android TV client connected to the Holodeck media server.

A separate fork or reskin of the Jellyfin Android TV client may be developed to provide dedicated Holodeck branding.

### DS9

Switch the television to the HDMI input connected to the DS9 Intel NUC.

DS9 operates its own launcher and provides access to gaming systems and Moonlight streaming. FalCor will not duplicate those controls.

### Sport

Provide access to Stan Sport and NBA viewing applications.

### Free-to-Air

Provide access to Australian catch-up television services:

- ABC iview
- SBS On Demand
- 7plus
- 9Now
- 10

## Technical Direction

The preferred implementation uses:

- Kotlin.
- Jetpack Compose for TV.
- Native Android TV application architecture.
- Configuration-driven destinations and actions.
- Replaceable adapters for device-specific functionality.
- D-pad and television remote navigation.

Platform compatibility and library versions will be verified before the Android project is generated.

Home Assistant is not a required dependency.

## Platform Compatibility

**Target device:** Sony KD-50X85K

The following capabilities require investigation and device testing:

1. Android version, SDK and Compose compatibility.
2. Default HOME launcher registration and replacement.
3. HDMI input switching from a third-party application.
4. Launching installed streaming applications.
5. Remote control behaviour and hardware shortcuts.
6. Safe recovery to the original television interface.

Compatibility findings will be recorded in `docs/device-compatibility.md`.

## Development

Development is performed on a private GitHub repository.

The operator manages implementation, testing and Git commits, with LLM-assisted development.

Primary development environment: Voyager (Debian 13).

Development follows small, independently testable changes. Platform-specific functionality must be verified rather than assumed.

See `AGENTS.md` for development conventions.

## Documentation

- `README.md` — Project overview and development status.
- `AGENTS.md` — Development conventions and constraints.
- `ROADMAP.md` — Planned, active and completed work.
- `CHANGELOG.md` — Significant implemented changes.
- `docs/architecture.md` — Architecture and design decisions.
- `docs/device-compatibility.md` — Platform verification findings.
- `docs/testing.md` — Testing and recovery procedures.

Documents not yet present will be introduced incrementally.

## Licence

Private project. No public licence has been granted.
