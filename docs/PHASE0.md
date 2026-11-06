# Phase 0 compatibility test

1. Record Y700 model, Android version, ZUI version and build fingerprint.
2. Install debug APK; select Y700 Launcher Plus as default HOME.
3. Test native taskbar visibility on HOME, inside apps, in both orientations, split screen, floating windows, and after reboot.
4. Restore stock ZUI launcher as default HOME after testing.

## ADB diagnostics (no root)

```sh
adb shell getprop ro.build.fingerprint
adb shell getprop ro.build.version.release
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
adb shell dumpsys activity activities
adb shell dumpsys window
adb shell dumpsys package info.pulipuli.y700launcherplus
adb logcat -d -v time > y700-phase0-logcat.txt
```

Never publish raw logs without reviewing and redacting personal information.

## Observed results (2026-11-06)

Device: Lenovo Legion Tab Y700, model TB320FC; Android 15 (SDK 35); ZUI 17.0.350; build `TB320FC_CN_OPEN_USER_Q00031.0_V_ZUI_17.0.350_ST_250418`.

The Phase 0 APK was built by GitHub Actions run [42227767334](https://github.com/pulipulichen/Y700-Launcher-Plus/actions/runs/42227767334), installed over Wi-Fi ADB, and verified against SHA-256 `fa6f849982bef6cac759a94dd1d33788d653e6767b98b147f8943d1aa67d9989`. The package manager accepted `info.pulipuli.y700launcherplus/.MainActivity` as the selected HOME and the activity started successfully.

Window-manager checks found a ZUI-owned `Taskbar` window of type `NAVIGATION_BAR_PANEL`. Its surface was shown and reported `isOnScreen=true` and `isVisible=true` on stock HOME, in Tachiyomi Reader, and while the Phase 0 Activity was the focused app. The NotificationShade window remained on top during the custom HOME attempt, so the visual layout and visible coexistence with the custom HOME could not be confirmed.

At the end of this run, the default HOME was restored to `com.zui.launcher/.drawer.NormalLauncher`, and the stock launcher task was brought to the front. The Phase 0 APK remains installed.

### Remaining checks

- Re-run custom HOME with the screen clear and verify the actual rendered Taskbar.
- Check the custom HOME while another app is foreground.
- Repeat in portrait and landscape.
- Check split-screen and floating-window modes.
- Check the selected HOME after reboot.
