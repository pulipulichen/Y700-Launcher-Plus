# Changelog

## Unreleased

- 2026-11-07: Actions run [42323346868](https://github.com/pulipulichen/Y700-Launcher-Plus/actions/runs/42323346868) reached Android manifest merging and failed because the Y700 icon conflicted with the upstream Quickstep icon. Use the same Y700 drawable in all three app manifests.

- 2026-11-07: Added Traditional/Simplified Chinese aliases, accent-insensitive pinyin and pinyin initials to both Lawnchair app search paths. Matching uses Android ICU and its default readings for characters with multiple pronunciations. Hidden-app search rules still apply. Android compile and device search acceptance are pending.

- 2026-11-07: Integrated a pinned Lawnchair 15 Beta 3 preview build with the existing Y700 package ID, icon and permanent certificate. The preview has a separate Release tag and starts with a fresh Lawnchair layout. Confirmed upstream and SystemUI pins. Local Gradle 9.3.0 wrapper download failed with `Network is unreachable`. Actions run [42323139795](https://github.com/pulipulichen/Y700-Launcher-Plus/actions/runs/42323139795) verified the source, SDK and permanent certificate, then stopped because the upstream root project has no `testLawnWithQuickstepPlayReleaseUnitTest` task. Use the supported assemble and lint tasks. Wireless ADB discovery returned no device. Compilation, legacy data migration and Y700 device acceptance are pending.

- 2026-11-07: Implemented home-page reorder, confirmed deletion and default HOME selection; the default follows its content when pages move. Added first-run launcher Dock setup with searchable selection, reorder and removal, and independent drawer columns. The Dock explanation explicitly distinguishes the ZUI system taskbar.
- Layout JSON v2 adds defaultPage and drawerColumns while retaining v1 import and the existing preferences key. Added 11 JVM regression tests for page operations and backup migration/validation; Actions now runs these before APK/lint publication. Verified Actions run 42314866537 on commit aa6024b33c77bf886f9543b7c7097d4625bb6f49: JVM tests, assembleDebug, lintDebug, permanent-certificate checks and Release publication succeeded. Confirmed launcher-latest contains the 26,497,971-byte APK and SHA-256 checksum with matching source commit. Device acceptance remains pending; no emulator used.


- 2026-11-07: Require the CT 9102 permanent certificate for every published sideload APK, remove the CI test-key fallback, and compare the built APK certificate before publication. Verified the existing launcher-latest APK checksum and certificate against CT 9102 (Actions run 42308372590).

- Set the supplied 512×512 PNG as the app and round launcher icon.

- Created permanent RSA-3072/PKCS12 signing identity without committing private material, then securely migrated it into the dedicated PVE CT 9102 (`android-keystore`) and uploaded all four GitHub Actions `SIGNING_*` secrets using its authenticated GitHub CLI. Added a pinned certificate verification script, permanent signing for debug/APK and Google Play AAB, a manual signed AAB release workflow, and kept the legacy fixed-test-key debug fallback pending repository-secret configuration. See `keystore/README.md` for one-time APK signer migration.

- Added versioned layout export and confirmed restore using Android document providers in the same launcher APK.

- 0.2.0 separates home pages, app drawer and launcher dock, adds home grid settings and hidden apps, and migrates existing pins. CI uses the existing fixed signing workflow and publishes to `launcher-latest`. See `docs/NOVA-PARITY.md` for remaining feature and Y700 acceptance checks.
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
