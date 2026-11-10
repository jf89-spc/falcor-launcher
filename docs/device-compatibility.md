
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
