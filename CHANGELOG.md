# Changelog

All notable changes to FalCor Launcher will be documented in this file.

This project follows the [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) format.

Version numbering will be introduced when application builds become available.

## [Unreleased]

### Added

- Established private GitHub repository `falcor-launcher`.
- Added `AGENTS.md` defining development conventions, collaboration responsibilities and project constraints.
- Added `README.md` documenting the project scope, design direction, destinations and technical approach.
- Added `ROADMAP.md` defining development phases and verification requirements.

- Added Kotlin/Jetpack Compose Android application scaffold.
- Added Android TV manifest support and application banner.
- Integrated Compose for TV with four focusable destination buttons.
- Added initial Sony compatibility and device testing documentation.

- Added cinematic home interface using the approved FalCor artwork.
- Added Holodeck application launching.
- Added Sony REST HDMI switching adapter.
- Added Android Keystore-backed PSK storage and single-press DS9 switching.

- Added cinematic home interface using the approved FalCor artwork.
- Added Holodeck application launching.
- Added Sony REST HDMI switching adapter.
- Added Android Keystore-backed PSK storage and single-press DS9 switching.

- Added dynamic Android TV application discovery.
- Added persistent, configurable Sport and Free-to-Air collections.
- Added application pickers and collection management.
- Added long-press application management for reordering and removal.

- Added functional settings gear with D-pad navigation.
- Added central maintenance menu with collection management and Android TV settings.
- Applied dark FalCor styling to maintenance and collection dialogs.
- Added Sony PSK configuration, replacement and reset controls.

### Verified

- Android 12 / API 31 compatibility on Sony KD-50X85K.
- Successful debug APK build, installation and launch.
- Application launches from Projectivy's application menu.
- D-pad LEFT/RIGHT navigation and initial focus.
- OK selection updates the selected destination.
- HOME and BACK return to Projectivy.
- Sony REST API switches to DS9 on HDMI 2 using PSK authentication from Voyager.

### Notes

- All four primary destinations are functional.
- HDMI switching from FalCor via localhost is verified. Credential persistence is verified following application restart.
- Native HOME replacement and recovery remain untested.
- Debug APKs are available; no release has been published.
