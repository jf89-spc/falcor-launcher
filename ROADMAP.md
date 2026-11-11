# FalCor Launcher — Development Roadmap

## Project Status

**Current phase:** Compose for TV foundation complete; FalCor interface development active.

FalCor builds and runs on the Sony KD-50X85K. Initial D-pad navigation and selection are verified. Destination actions and HOME replacement are not yet implemented.

## Completed — Repository Foundation

- [x] Create private GitHub repository `falcor-launcher`.
- [x] Clone repository to Voyager.
- [x] Establish development conventions in `AGENTS.md`.
- [x] Establish project overview in `README.md`.

## Active — Foundation and Compatibility

- [x] Create `ROADMAP.md` and `CHANGELOG.md`.
- [x] Establish initial documentation under `docs/`.
- [x] Verify Android version, API level and firmware build.
- [x] Establish ADB connectivity to the Sony KD-50X85K.
- [x] Investigate Projectivy and default HOME behaviour.
- [ ] Determine a safe launcher recovery procedure.
- [x] Verify Sony REST API HDMI switching from Voyager over LAN.
- [x] Verify FalCor application launching and basic remote-control compatibility.
- [x] Confirm development SDK and Compose for TV compatibility.

**Remaining requirement:** Establish and test safe HOME launcher recovery before replacing Projectivy.

## Planned — Application Foundation

- [x] Create Android Studio project using Kotlin.
- [x] Configure Jetpack Compose for TV.
- [ ] Establish application identity and configuration structure.
- [x] Build an initial launchable activity.
- [x] Verify installation and startup on the television.
- [x] Establish basic D-pad navigation and focus handling.

## Active — FalCor Interface

- [x] Test four provisional destination buttons with D-pad and OK selection.

- [ ] Implement the agreed cinematic home screen.
- [ ] Implement Holodeck, DS9, Sport and Free-to-Air destinations.
- [ ] Implement remote navigation and consistent focus states.
- [ ] Add restrained LCARS-inspired interface elements.
- [ ] Implement configuration-driven destination definitions.

## Planned — Destination Integration

- [x] Launch the installed Jellyfin Android TV client.
- [x] Implement configurable Sport application selection and launching.
- [x] Implement configurable Free-to-Air application selection and launching.
- [x] Implement and verify DS9 HDMI switching through the Sony REST adapter.
- [x] Store Sony PSK securely and enable single-press DS9 switching.
- [ ] Verify stored Sony credentials survive television reboot.
- [ ] Test credential replacement following authentication failure.
- [x] Add credential configuration and reset to the maintenance interface.
- [x] Handle missing or unavailable applications gracefully.

## Completed — Configurable Application Collections

- [x] Discover installed Android TV applications dynamically.
- [x] Persist independently configurable Sport and Free-to-Air collections.
- [x] Add, remove and reorder applications using collection management.
- [x] Long-press an application card to manage it directly.
- [x] Preserve collection changes across application restarts.

## Planned — Collection Refinements

- [ ] Implement visual pick-up-and-move reordering using the D-pad.
  - Long-press an application and select Move.
  - Highlight the lifted card and animate neighbouring cards.
  - LEFT/RIGHT moves the selected card.
  - OK confirms and saves the new order.
  - BACK cancels and restores the original order.
  - Automatically scroll when moving beyond the visible cards.
- [ ] Refine collection card styling and spacing.
- [ ] Verify focus restoration across large scrolling collections.
- [ ] Verify instrumentation test data preservation.

## Planned — System Integration

- [x] Implement central settings and maintenance interface.
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

## Planned — Maintenance Interface Refinements

- [ ] Replace native Android dialogs with custom cinematic Compose for TV menus.
- [ ] Apply consistent FalCor typography, colours, spacing and amber focus indicators.
- [ ] Refine settings gear focus styling without obscuring the icon.
- [ ] Improve application picker presentation with app artwork and icons.
- [ ] Unify collection management, credential configuration and maintenance styling.
- [ ] Verify D-pad navigation and focus restoration throughout the maintenance interface.

## Visual Refinement

- [x] Separate background artwork from interactive UI.
- [x] Reduce home card size and improve spacing.
- [x] Establish Orbitron typography and card styling.
- [x] Apply home design language to application submenus.
- [x] Replace native maintenance dialogs with custom FalCor UI.
- [ ] Refine application artwork and navigation transitions.

## Screensaver

- [ ] Design a custom FalCor cinematic screensaver.
- [ ] Investigate native Android TV screensaver integration.
- [ ] Match the screensaver to the approved FalCor artwork
      and Orbitron typography.
- [ ] Verify idle activation, remote wake and standby behaviour.

## Screensaver Decision — Complete

- [x] Verify Android TV DreamService compatibility.
- [x] Evaluate local WallPanel photo integration.
- [x] Test Jellyfin's native screensaver.
- [x] Select Jellyfin for automatic idle activation.
- [x] Remove the experimental FalCor DreamService.

Custom FalCor screensaver development is deferred indefinitely.
Jellyfin provides the required functionality without additional
maintenance. Revisit only if requirements change.

## Application Reordering

- [x] Direct D-pad application reordering.
- [x] Save and cancel controls.
- [ ] Extended scrolling and focus regression tests.
