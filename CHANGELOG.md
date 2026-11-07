# Changelog

## Unreleased

- 2026-11-07: Implement Issue #3 drawer chrome with independent search/frequent-row placement, local recent/frequency recommendations, pin/exclude/hide/alias actions and management, exact Chinese/alias matching before launch-frequency ordering, automatic grid columns and keyboard-on-open default. Preserve native search styling, gestures and popup shortcuts. History is launcher-originated and local only; no Usage Access permission is added. Compilation and device acceptance pending.
- 2026-11-07: Issue #2 device investigation confirms ZUI Taskbar owner `com.zui.launcher`, navigation-bar-panel window type and signature|privileged READ_SETTINGS/WRITE_SETTINGS permissions. ADB provider query failed and run-as reports package not debuggable. Add a supported explicit ZUI HOME entry for native editing without changing default HOME. Direct native cross-app Taskbar editing/synchronization from our app remains blocked; no overlay or private API added.


- 2026-11-07: Actions run [42326549446](https://github.com/pulipulichen/Y700-Launcher-Plus/actions/runs/42326549446) passed packaging, package ID and permanent signer verification and published the [signed preview](https://github.com/pulipulichen/Y700-Launcher-Plus/releases/tag/lawnchair15-preview-42326549446-1). Verified the SHA-256 before installing over the prior APK on TB320FC. Confirmed cold start, default HOME, ZUI-owned Taskbar visibility, ZUI Recents, and return from Settings. Legacy preferences were backed up. Full device acceptance and migration remain pending.

- 2026-11-07: Actions run [42325661068](https://github.com/pulipulichen/Y700-Launcher-Plus/actions/runs/42325661068) failed during `lintVitalLawnWithQuickstepPlayRelease` with 1,527 upstream resource errors across `ResAuto`, `MissingDefaultResource` and `ExtraTranslation`. Restore the upstream release packaging setting and run lint separately as an advisory check after the preview APK passes package, signature and checksum verification.

- 2026-11-07: Actions run [42323763050](https://github.com/pulipulichen/Y700-Launcher-Plus/actions/runs/42323763050) spent more than 18 minutes in the combined release assembly and debug lint step without publishing an APK. Build and lint the same release variant in separate bounded steps. The preview skips code and resource shrinking. Superseded preview runs are cancelled.

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

- 2026-11-07: Refine Issue #3 automatic grid sizing per active device profile for orientation changes; hide the manual column selector while automatic sizing is enabled. Add 11 host-only Java ranking/width regression checks to the preview workflow. Local Java runtime lacks javac, so execute these checks with Actions JDK 21. Record actual local-history privacy behavior and Taskbar evidence in doc/ISSUES-2-3.md. Device connection dropped before additional HOME/app checks; original ADB port refused reconnection.

- 2026-11-07: Run 42336382337 at 57b3927 passed all 11 drawer ranking/width checks, then failed `compileLawnWithQuickstepPlayReleaseKotlin` at DrawerPreferences.kt:39: nullable LauncherApps receiver. Correct the service lookup with a null-safe empty-list fallback. Run 42336095575 was superseded/cancelled (its log also contains that error). Preserve full app grid for empty focused search and reject stale asynchronous query results. Local checks also pass via `java com.sun.tools.javac.Main`. With baseline APK and Lawnchair default HOME, ADB confirmed ZUI Taskbar on-screen/visible on HOME, Settings and Calculator; restored original ZUI HOME. Correction compilation/device drawer validation pending.

- 2026-11-07: Run 42336927606 at c1817ee passed host checks, Android compilation, package/permanent signer checks and published lawnchair15-preview-42336927606-1. SHA-256 verified before successful in-place Y700 install and launch. Portrait screenshot/UI hierarchy confirms full app list plus fixed frequent row and search above the visible IME. Chrome search and a temporary y700alias alias returned expected apps; recommendations disappear during search. ADB text needed the existing Zhuyin IME switched to English with Shift (uncommitted phonetics were initially mistaken for missing input). Native fast-scroller touch bounds still extended behind fixed chrome; constrain its measured/layout bounds to the scrolling content. Rebuild and final device checks pending.

- 2026-11-07: Device checks on c1817ee passed all four independent search/frequent-row position combinations, alias and pinyin lookup, launch/reset, recent ranking, native popup actions, pin priority and exclusion/restoration. Temporary alias and pin/exclusion changes restored. Landscape + IME left only a 216 px app viewport with the full-height frequent row; add a 56 dp horizontal icon+label compact row in that configuration and remove the unused native empty-header gap. Assign distinct native icons to pin/exclude/hide/alias actions. Host regression checks now total 12. Final build/device verification pending.

- 2026-11-07: Run 42338607381 / b2546df passed 12 host checks, compilation, permanent signer/package verification and published its APK. SHA-256 b5306c0c560d7d3324606269b697a880cd8d6a9b8980b49f3d3423b9fa721fe3 verified before in-place install (version code 29900429). Landscape+IME viewport improved from 216 to 425 px with five compact icon/name slots; fast-scroller bounds now match the app region. Screenshot review caught a remaining 99 px native header clip cropping first-row icons after the gutter was removed. Match the empty-header clip to the zero gutter before final acceptance. No crash of the Y700 app observed; the intervening NotificationShade was the idle lock screen, dismissed through normal wm keyguard handling.
