
# FalCor Launcher — Testing

## Target Device

- Sony KD-50X85K
- Android 12 / API 31
- Development host: Voyager (Debian 13)
- Testing method: ADB over LAN

## Test 001 — Initial Android TV Deployment

Date: 2026-11-10

### Results

| Test | Result |
|---|---|
| Debug APK build | PASS |
| ADB installation | PASS |
| Launch via ADB | PASS |
| Launch via Projectivy All Apps | PASS |
| Compose template renders | PASS |
| HOME returns to Projectivy | PASS |
| BACK returns to Projectivy | PASS |

### Observations

- Default application displays a black background with "Hello Android!" in the upper-left corner.
- Application launches without an observed crash.
- Sony remote HOME and BACK navigation work as expected.
- Projectivy remains the active HOME launcher.
- No launcher configuration changes were required.

### Not Yet Tested

- D-pad focus and navigation.
- Compose for TV components.
- FalCor interface rendering.
- Application destination launching.
- HDMI switching initiated from FalCor.
- Native HOME role assignment and recovery.

### Recovery

FalCor is currently installed as an ordinary Android TV application.

Projectivy remains accessible through HOME.

The application can be removed through Android TV application settings without changing the existing launcher.

## Test 002 — Compose for TV Navigation

Date: 2026-11-10

### Results

- PASS — Application launches on Sony KD-50X85K.
- PASS — Initial destination receives focus.
- PASS — LEFT/RIGHT navigates between four destinations.
- PASS — OK selects destination and updates selection state.
- PASS — BACK returns to Projectivy.
- PASS — HOME returns to Projectivy.

### Limitations

- Destinations are placeholders without launch actions.
- Visual styling is provisional.
- Native HOME replacement has not been tested.

## Test 003 — Holodeck and DS9 Integration

Date: 2026-11-10

### Results

- PASS — Holodeck launches the installed Jellyfin Android TV client.
- PASS — HOME and BACK from Jellyfin return to Projectivy.
- PASS — DS9 displays a PSK entry dialog.
- PASS — Authenticated Sony REST request switches to HDMI 2.
- PASS — Sony REST API accessible from FalCor via localhost.

### Limitations

- DS9 requires manual PSK entry for each switch.
- Secure persistent credential storage remains pending.
- FalCor is not yet the default HOME launcher.

## Test 003 — Holodeck and DS9 Integration

Date: 2026-11-10

### Results

- PASS — Holodeck launches the installed Jellyfin Android TV client.
- PASS — HOME and BACK from Jellyfin return to Projectivy.
- PASS — DS9 displays a PSK entry dialog.
- PASS — Authenticated Sony REST request switches to HDMI 2.
- PASS — Sony REST API accessible from FalCor via localhost.

### Limitations

- DS9 requires manual PSK entry for each switch.
- Secure persistent credential storage remains pending.
- FalCor is not yet the default HOME launcher.

## Test 004 — Persistent DS9 Credentials

Date: 2026-11-10

- PASS — Sony PSK saved following successful HDMI switching.
- PASS — DS9 switches without prompting for the PSK.
- PASS — Credential persists after application force-stop and restart.
- NOT TESTED — Persistence following television reboot.
- NOT TESTED — Credential replacement after authentication failure.
