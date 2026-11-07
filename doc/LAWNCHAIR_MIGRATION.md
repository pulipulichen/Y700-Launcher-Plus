# Lawnchair migration baseline (2026-11-07)

## Source and preservation
- Existing Y700 Launcher Plus baseline: `d3781b47ce67b85182d3b7e23c192789c7c0d227`.
- Immutable-by-convention backup branch: `backup/pre-lawnchair-20261107` (do not force push).
- Migration branch: `feature/lawnchair15-integration`.
- Upstream: https://github.com/LawnchairLauncher/lawnchair
- Candidate: `v15.0.0-beta3.0` / `15-beta`; pin exact commit before importing.
- Alternative: `16-dev` nightly, not selected for first integration.

## Licensing gate
Upstream root LICENSE.txt is Apache-2.0. Existing Y700 project root LICENSE is Unlicense. Do not relicense imported upstream code as Unlicense. Retain upstream copyright, license notices and any NOTICE files, audit third-party and submodule licenses, and check branding/trademarks before any commercial Play release.

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
- [ ] Full Lawnchair source imported and compiled.
- [ ] Signed release APK produced.
- [ ] Device test passed.

## Operational blocker
Y700 MCP device_info returned: `Tunnel-client has not been seen for 300 seconds`. Reconnect the Y700 tunnel before deployment; do not claim device testing until connected.
