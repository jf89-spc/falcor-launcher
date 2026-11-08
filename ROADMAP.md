# FalCor Launcher — Development Roadmap

## Project Status

**Current phase:** Foundation and platform verification.

FalCor has not yet been implemented or tested on the Sony KD-50X85K.

## Completed — Repository Foundation

- [x] Create private GitHub repository `falcor-launcher`.
- [x] Clone repository to Voyager.
- [x] Establish development conventions in `AGENTS.md`.
- [x] Establish project overview in `README.md`.

## Active — Foundation and Compatibility

- [ ] Create `ROADMAP.md` and `CHANGELOG.md`.
- [ ] Establish initial documentation under `docs/`.
- [ ] Verify Android version, API level and firmware build.
- [ ] Establish ADB connectivity to the Sony KD-50X85K.
- [ ] Investigate Projectivy and default HOME behaviour.
- [ ] Determine a safe launcher recovery procedure.
- [ ] Investigate supported HDMI input switching mechanisms.
- [ ] Verify application launching and remote-control compatibility.
- [ ] Confirm development SDK and Compose for TV compatibility.

**Exit criteria:** Documented platform findings, a working development environment and a safe recovery procedure.

## Planned — Application Foundation

- [ ] Create Android Studio project using Kotlin.
- [ ] Configure Jetpack Compose for TV.
- [ ] Establish application identity and configuration structure.
- [ ] Build an initial launchable activity.
- [ ] Verify installation and startup on the television.
- [ ] Establish basic D-pad navigation and focus handling.

## Planned — FalCor Interface

- [ ] Implement the agreed cinematic home screen.
- [ ] Implement Holodeck, DS9, Sport and Free-to-Air destinations.
- [ ] Implement remote navigation and consistent focus states.
- [ ] Add restrained LCARS-inspired interface elements.
- [ ] Implement configuration-driven destination definitions.

## Planned — Destination Integration

- [ ] Launch the installed Jellyfin Android TV client.
- [ ] Implement Sport application selection and launching.
- [ ] Implement Free-to-Air application selection and launching.
- [ ] Implement DS9 HDMI switching through a replaceable adapter.
- [ ] Handle missing or unavailable applications gracefully.

## Planned — System Integration

- [ ] Implement a hidden settings and maintenance interface.
- [ ] Verify Back, Home and remote shortcut behaviour.
- [ ] Integrate with the Android TV launcher mechanism where supported.
- [ ] Preserve access to standard Android TV settings and functions.
- [ ] Test recovery to the original television interface.

## Planned — Validation and Release

- [ ] Test startup, navigation and application launching.
- [ ] Test HDMI switching and recovery.
- [ ] Test system restart and launcher persistence.
- [ ] Evaluate responsiveness and resource consumption.
- [ ] Produce a documented installation and rollback procedure.
- [ ] Package an initial release APK.

## Future / Separate Work

- Investigate a Holodeck-branded fork or reskin of the Jellyfin Android TV client.
- Investigate hardware shortcut remapping where supported.
- Consider additional configuration options based on actual usage.

## Development Rules

Complete one logical change at a time.

Features remain planned until implemented. They remain unverified until tested on the target device.

Record significant completed changes in `CHANGELOG.md`.
