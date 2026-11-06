# Y700 Launcher Plus

A personal, non-root Android HOME launcher for Lenovo Legion Y700 tablets. The first device check confirms that the custom HOME screen can appear with the original ZUI taskbar visible on a TB320FC running Android 15 / ZUI 17.0.350.

## Current build

- Adaptive app grid with search.
- Pinned apps saved on device; first run pins up to eight launchable apps.
- Launch apps directly from the custom HOME screen.
- Original ZUI taskbar remains owned by ZUI and was visible in the supplied device screenshot.
- Traditional Chinese and English interface strings.
- No Root access, privileged permissions, or proprietary ZUI APIs.

The app is for personal use and sideloading. GitHub Actions compiles and lints a debug APK, then publishes the APK and SHA-256 checksum as a prerelease asset. No emulator is used in CI.

Sideload builds from GitHub Actions use a fixed test keystore supplied by the repository secret `Y700_TEST_KEYSTORE_BASE64`. Its PKCS12 alias is `androiddebugkey` and its temporary test password is `android`. If an older APK uses a different certificate, Android requires a one-time reinstall, which clears app data. Keep the same keystore for subsequent updates.

See [Phase 0 device checks](docs/PHASE0.md) and [architecture](docs/ARCHITECTURE.md).

Independent project; not affiliated with Lenovo. ZUI, Legion and Y700 are trademarks of their respective owners.
