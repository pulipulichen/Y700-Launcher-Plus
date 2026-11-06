# Phase 0 compatibility test

## Device and recorded result

Device: Lenovo Legion Tab Y700, model TB320FC; Android 15 (SDK 35); ZUI 17.0.350; build `TB320FC_CN_OPEN_USER_Q00031.0_V_ZUI_17.0.350_ST_250418`.

The Phase 0 APK was built by GitHub Actions run [42227767334](https://github.com/pulipulichen/Y700-Launcher-Plus/actions/runs/42227767334), installed over Wi-Fi ADB, and verified against SHA-256 `fa6f849982bef6cac759a94dd1d33788d653e6767b98b147f8943d1aa67d9989`. Android accepted `info.pulipuli.y700launcherplus/.MainActivity` as the selected HOME and launched it successfully.

The supplied screenshot now confirms that the custom HOME content is rendered while the original ZUI taskbar is visible at the bottom of the screen. This verifies basic visual coexistence on TB320FC / Android 15 / ZUI 17.0.350. It does not establish behavior in other modes or on other builds.

Earlier window-manager checks found a ZUI-owned `Taskbar` window of type `NAVIGATION_BAR_PANEL`, with `isOnScreen=true` and `isVisible=true` while the custom HOME Activity was focused. The earlier capture was obscured by NotificationShade; the later screenshot resolves that visual uncertainty.

## Follow-up checks

- Check the custom HOME while another app is foreground.
- Repeat in landscape.
- Check split-screen and floating-window modes.
- Check the selected HOME after reboot.
- Restore stock ZUI launcher as default HOME after compatibility testing if desired.

## ADB diagnostics

Use non-root diagnostics to record the device state:

```sh
adb shell getprop ro.build.fingerprint
adb shell getprop ro.build.version.release
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
adb shell dumpsys activity activities
adb shell dumpsys window
adb shell dumpsys package info.pulipuli.y700launcherplus
adb logcat -d -v time > y700-phase0-logcat.txt
```

Review and redact personal information before sharing raw logs.
