# Lawnchair migration baseline (2026-11-07)

## Source and preservation
- Existing Y700 Launcher Plus baseline: `d3781b47ce67b85182d3b7e23c192789c7c0d227`.
- Immutable-by-convention backup branch: `backup/pre-lawnchair-20261107` (do not force push).
- Initial planning branch: `feature/lawnchair15-integration`, merged to `main` as #1.
- Upstream: https://github.com/LawnchairLauncher/lawnchair
- Selected source: `v15.0.0-beta3.0` at `505dbc40e6154c05158b5d0271c45f6a885a411b`. SystemUI is pinned to `6a11ef767998885838a599331b5485f768b3d725`.

## Licensing gate
Upstream root LICENSE.txt is Apache-2.0. Existing Y700 project root LICENSE is Unlicense. Do not relicense imported upstream code as Unlicense. Retain upstream copyright, license notices and any NOTICE files, audit third-party and submodule licenses, and check branding/trademarks before any commercial Play release.

## Preview build

Run `./lawnchair/prepare.sh /path/to/new-checkout` to fetch the complete upstream tree, verify both pins and apply the Y700 changes. Use JDK 21, Android SDK 36.1, Build Tools 36.1.0 and the upstream Gradle wrapper. The CI workflow builds `assembleLawnWithQuickstepPlayRelease` with the CT 9102 certificate, verifies the APK package and certificate, and publishes the APK and SHA-256 under a separate preview tag. The existing sideload and Play workflows still build the legacy source.

Export a legacy JSON backup before testing the preview. Lawnchair has its own layout database and does not import this JSON yet. Android may ask you to select the default HOME again because the activity changes. The preview uses the same app ID and signing key, so it replaces the legacy app on upgrade. To test a return to legacy without clearing app data, use a legacy build with a version code at least as high as the installed preview. On-device upgrade and rollback are pending.

The preview includes upstream Launcher3/Lawnchair workspace and settings. Track Y700-specific Dock selection, native ZUI taskbar behavior, Traditional/Simplified Chinese and pinyin search, legacy JSON migration, Nova/Prime acceptance and the [Google Drive specification](google-drive-backup-restore-spec.md) separately. Test the stock unrooted Y700 before promoting the preview. Confirm native taskbar pinning, recent apps, split screen and floating windows in both orientations and after reboot.

## Integration plan
1. Obtain upstream at pinned tag/commit and import the actual complete Launcher3/Lawnchair build tree, including required submodules; do not merely copy individual launcher activities.
2. Keep original Y700 implementation in backup branch. Preserve package identity and signing fingerprint; assess upgrade and migration safety before installation.
3. Build using upstream-supported Gradle/Java/SDK matrix. Do not assume existing :app Gradle module applies to Lawnchair.
4. Restore Y700 dock app-selection and ZUI compatibility logic as separate changes. Verify HOME, landscape/tablet layout, widgets, gestures and fallback when proprietary ZUI dock cannot be bridged without root.
5. Integrate settings, localization, local ZIP/Google Drive backup with migration and rollback testing.
6. GitHub Actions: test, lint and build; verify permanent signing certificate; publish APK and SHA256 to GitHub Releases, not workflow artifacts. Never expose keystore or secrets.
7. On Y700: connect Wireless ADB, record launcher package and current HOME, install APK, verify signature and startup, test dock/widgets and revert HOME on failure.

## Current gates
- [x] Existing project backed up to branch.
- [x] Apache-2.0 upstream root license identified.
- [ ] All transitive licenses and trademarks audited.
- [x] Complete upstream checkout and SystemUI pins verified.
- [ ] Build, lint and APK identity checks passed.
- [ ] Signed release APK produced.
- [ ] Device test passed.

## Operational blocker
Y700 MCP device_info returned: `Tunnel-client has not been seen for 300 seconds`. Reconnect the Y700 tunnel before deployment; do not claim device testing until connected.
