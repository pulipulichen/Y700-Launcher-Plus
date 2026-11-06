# Changelog

## Unreleased
- Actions run 42236542284 (commit 8b6c219) failed in Compile and lint with fixed test signing at `:app:mergeExtDexDebug` with `OutOfMemoryError: Java heap space`. Set the Gradle heap to 2 GiB and rebuilt.

- GitHub Actions now signs sideload APKs with a fixed temporary test keystore and verifies the APK signature before publishing to `launcher-latest`.
- Removed the unconditional taskbar hint. On the test Y700, the native ZUI Taskbar was hidden with `open_taskbar=0`. Restoring the original `zui_taskbar_feature_enable=0` and setting `open_taskbar=1` restored the ZUI window and its touchable region while Launcher Plus remained the default HOME.

- Replaced the Phase 0 placeholder screen with the first personal-use launcher shell: searchable app grid, direct app launch, and locally persisted pinned apps.
- Added Traditional Chinese strings and the minimum Android package-visibility query needed to list launchable apps.
- Confirmed custom HOME content and the native ZUI taskbar are visible together in the supplied TB320FC screenshot.

## 2026-11-06

- Added a GitHub Actions Android debug APK build and lint workflow for Android API 36 using JDK 17 and Gradle 8.13. It publishes the test APK and SHA-256 checksum as a prerelease.
- Superseded failure: Actions run 42227187422 (commit caf2560d0c908192db77db56049574c03dc0f3aa) stopped at SDK setup with `sdkmanager: command not found`; the runner had no Android command-line tools on PATH. Added pinned `android-actions/setup-android@v4.0.4`.
- Superseded failure: run 42227386430 (commit 5bd8ad2804e2bf9dd0f1a48d0205d58fb096cb47) passed SDK/JDK/Gradle setup but failed at `:app:checkDebugAarMetadata` because `android.useAndroidX` was unset. Added the property in `gradle.properties`.
- Verification passed: run 42227767334 (commit 4644d26e97c50bf034749a0498712e98312c841b) completed `:app:assembleDebug` and `:app:lintDebug`. Published the APK and checksum to [phase0-latest](https://github.com/pulipulichen/Y700-Launcher-Plus/releases/tag/phase0-latest).
- Installed APK on Y700 TB320FC; SHA-256 matched `fa6f849982bef6cac759a94dd1d33788d653e6767b98b147f8943d1aa67d9989`. Android accepted `info.pulipuli.y700launcherplus/.MainActivity` as the default HOME and launched the activity.
- Earlier window-manager checks reported the ZUI Taskbar window visible while the custom HOME Activity was focused. The supplied screenshot now confirms visual coexistence: custom launcher content is visible above the original ZUI taskbar.
- Still to verify on the device: launch from another app, landscape, split screen, floating windows, and behavior after reboot. Raw device logs were not published.
