
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

Status: Not tested.

Required investigation:

- Android TV input APIs and permission restrictions.
- Sony BRAVIA input switching capabilities.
- Whether unprivileged applications can request HDMI changes.
- Alternative mechanisms if direct switching is restricted.

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
