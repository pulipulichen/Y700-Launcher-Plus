# Changelog

## Unreleased
- Initial Phase 0 HOME replacement compatibility test scaffold.

## 2026-11-06
- Added a GitHub Actions Phase 0 debug APK build and lint workflow for Android API 36 using JDK 17 and Gradle 8.13. It publishes the test APK and SHA-256 checksum to the `phase0-latest` prerelease.
- Superseded failure: Actions run 42227187422 (commit caf2560d0c908192db77db56049574c03dc0f3aa) stopped at SDK setup with `sdkmanager: command not found`; the runner had no Android command-line tools on PATH. Added pinned `android-actions/setup-android@v4.0.4`.
- Superseded failure: run 42227386430 (commit 5bd8ad2804e2bf9dd0f1a48d0205d58fb096cb47) passed SDK/JDK/Gradle setup but failed at `:app:checkDebugAarMetadata` because `android.useAndroidX` was unset. Added the property in `gradle.properties`.
- Verification passed: run 42227767334 (commit 4644d26e97c50bf034749a0498712e98312c841b) completed `:app:assembleDebug` and `:app:lintDebug`. Published the APK and checksum to [phase0-latest](https://github.com/pulipulichen/Y700-Launcher-Plus/releases/tag/phase0-latest).
- Installed APK on Y700 TB320FC; SHA-256 matched `fa6f849982bef6cac759a94dd1d33788d653e6767b98b147f8943d1aa67d9989`. Android accepted the app as the default HOME and launched its activity. The ZUI Taskbar window reported `Surface shown=true`, `isOnScreen=true` and `isVisible=true` while that activity was focused.
- Visual coexistence is not confirmed: the NotificationShade window remained focused over the launcher, and the accessibility dump did not expose the test app's view nodes. Restored ZUI Launcher as default HOME and brought its task to the front; the Phase 0 app remains installed.
- Still unverified: clear-screen custom HOME visibility, custom HOME while another app is foreground, portrait/landscape, split screen, floating windows and behavior after reboot. Raw device logs were not published.
