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
