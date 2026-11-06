# Y700 Launcher Plus

A personal, non-root Android HOME launcher for Lenovo Legion Y700 tablets. The first device check confirms that the custom HOME screen can appear with the original ZUI taskbar visible on a TB320FC running Android 15 / ZUI 17.0.350.

## Current build

- Separate home pages, searchable app drawer, and an optional set of pinned dock shortcuts.
- Swipe between pages. Long-press icons to place, move, remove, or hide them.
- Change home grid columns and labels in Settings. Existing home pins are migrated on upgrade.
- The native ZUI taskbar is controlled by ZUI. See the Phase 0 findings and the current device acceptance checklist.
- Traditional Chinese and English interface strings.
- No Root access, privileged permissions, or proprietary ZUI APIs.

The app currently supports personal sideloading and is planned for a paid Google Play release. GitHub Actions compiles and lints a debug APK, then publishes the APK and SHA-256 checksum as a prerelease asset. No emulator is used in CI.

The permanent PKCS12/RSA-3072 signing identity is stored privately on the PVE host. Both debug APKs and Google Play upload AABs use the same identity once all four `SIGNING_*` repository secrets are configured. Until then, sideload debug builds retain the existing fixed `Y700_TEST_KEYSTORE_BASE64` signing fallback; AAB releases refuse the test key. Switching identities requires a one-time uninstall/reinstall after exporting the launcher layout. See [keystore setup](keystore/README.md) and [Google Play checklist](GOOGLE_PLAY.md).

See [Phase 0 device checks](docs/PHASE0.md) [architecture](docs/ARCHITECTURE.md), and the [Nova feature roadmap](docs/NOVA-PARITY.md).

Independent project; not affiliated with Lenovo. ZUI, Legion and Y700 are trademarks of their respective owners.
