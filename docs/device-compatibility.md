
# Sony KD-50X85K — Device Compatibility

Last verified: 2026-11-09

## Device

- Model: Sony KD-50X85K
- Product: BRAVIA 4K VH21
- Android TV version: 12
- API level: 31
- Build: STT2.230505.001.S136
- Build revision: 682241

## Development Connectivity

- Development host: Voyager (Debian 13)
- ADB version: 1.0.41
- Connection: TCP/IP, port 5555
- TV IP at testing: 192.168.0.229
- Status: Connected and authorised

## HOME Launcher Behaviour

### Verified

Android HOME role holder:

    com.spocky.projengmenu

Registered HOME intent resolution:

    com.google.android.apps.tv.launcherx/.home.HomeActivity

Active HOME task:

    com.spocky.projengmenu/.ui.home.MainActivity

Google LauncherX remains present as a background HOME task.

Projectivy accessibility service is enabled:

    com.spocky.projengmenu/com.spocky.projengmenu.services.ProjectivyAccessibilityService

Pressing HOME on the Sony remote brings Projectivy to the foreground.

### Assessment

Native HOME registration is supported by the platform.

Projectivy holds the HOME role and runs as an Android HOME task.

The relationship between Android's HOME role, intent resolution and Projectivy's accessibility service requires further investigation.

### Proposed FalCor Approach

1. Implement FalCor as a native HOME candidate.
2. Test HOME role selection without disabling Google TV.
3. Establish and test launcher rollback procedures.
4. Consider accessibility interception only if required.

No launcher configuration changes have been performed during verification.


## HDMI Input Switching

Status: Verified from Voyager over LAN (2026-11-09).

### Input Mapping

- HDMI 1: Voyager / PS5 (occasionally connected).
- HDMI 2: DS9 Intel NUC.
- HDMI 3: Sony HT-S100F soundbar (ARC/eARC).
- HDMI 4: Unassigned.

### Verified Method

Sony BRAVIA REST API:

- Endpoint: `/sony/avContent`
- Method: `setPlayContent`
- Version: `1.0`
- Destination URI: `extInput:hdmi?port=2`
- Authentication: `X-Auth-PSK` HTTP header

Successfully switched from Projectivy to DS9 using an authenticated HTTP request from Voyager.

### Television Configuration

- Authentication: Normal and Pre-Shared Key.
- Simple IP control: Off.
- Control4: Off.

### Outstanding Verification

- Verified: FalCor can access the Sony REST API via `127.0.0.1` and switch to HDMI 2 using PSK authentication.
- Determine secure storage and configuration of the PSK.
- Verify input switching remains functional following television restart.
- Implement a replaceable Sony HDMI switching adapter.

Never store the PSK in Git.

## Application Compatibility

Status: Not tested.

Required investigation:

- Installed application package identifiers.
- Application launch intents.
- Remote navigation and focus behaviour.
- Hardware shortcut behaviour.

## Recovery

Status: Not tested.

Google LauncherX and Projectivy remain installed.

Do not disable either launcher or modify HOME role assignments until a recovery procedure is established and tested.

## Native HOME Integration — 2026-11-10

- PASS — FalCor registers as a native HOME candidate.
- PASS — ADB can assign FalCor the HOME role.
- PASS — Explicit HOME intent launches FalCor as a HOME task.
- FAIL — Physical HOME button opens FalCor using native
  registration and role assignment alone.
- With Projectivy accessibility disabled, the physical HOME
  button returns to Google LauncherX.
- Explicit activation of FalCor as a HOME task does not
  resolve the physical HOME button behaviour.

### Architecture Decision

Implement an optional, narrowly scoped accessibility service
to redirect Google LauncherX to FalCor.

Keep Google LauncherX and Projectivy installed and enabled.
Native HOME registration will be retained.

The accessibility service must be explicitly enabled by the
operator and must not inspect application content or collect
user data.

## Native HOME Integration — 2026-11-10

- PASS — FalCor registers as a native HOME candidate.
- PASS — ADB can assign FalCor the HOME role.
- PASS — Explicit HOME intent launches FalCor as a HOME task.
- FAIL — Physical HOME button opens FalCor using native
  registration and role assignment alone.
- With Projectivy accessibility disabled, the physical HOME
  button returns to Google LauncherX.
- Explicit activation of FalCor as a HOME task does not
  resolve the physical HOME button behaviour.

### Architecture Decision

Implement an optional, narrowly scoped accessibility service
to redirect Google LauncherX to FalCor.

Keep Google LauncherX and Projectivy installed and enabled.
Native HOME registration will be retained.

The accessibility service must be explicitly enabled by the
operator and must not inspect application content or collect
user data.

## Native HOME Replacement — 2026-11-11

Final working configuration on Sony KD-50X85K:

- FalCor registered as native HOME and assigned the HOME role.
- Google LauncherX disabled for Android user 0.
- Google SetupWraith disabled for Android user 0.
- FalCor accessibility service disabled.
- Projectivy retained for recovery.
- Television connected via Ethernet.

HOME resolution returns com.falcor.launcher/.MainActivity.
Startup, persistence and normal navigation verified.

This configuration supersedes the accessibility-based
workaround described above for this television.

### Restore Google TV components

    adb shell pm enable --user 0 com.google.android.tungsten.setupwraith
    adb shell pm enable --user 0 com.google.android.apps.tv.launcherx

Projectivy recovery remains available using the previously
documented HOME assignment and accessibility procedure.

### Remaining compatibility considerations

SetupWraith owns a saved Wi-Fi configuration on this device.
The television currently uses Ethernet.

Casting, Wi-Fi reconnection, factory reset and future
firmware updates have not been verified with SetupWraith
disabled.

The accessibility implementation remains available as an
optional compatibility fallback.
