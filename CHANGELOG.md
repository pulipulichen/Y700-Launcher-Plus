# Changelog

## Unreleased
- Initial Phase 0 HOME replacement compatibility test scaffold.

## 2026-11-06
- Added a GitHub Actions Phase 0 debug APK build and lint workflow for Android API 36 using JDK 17 and Gradle 8.13. It publishes the test APK and SHA-256 checksum to the `phase0-latest` prerelease. The first CI run and third-party HOME test are pending.
- Failure recorded: Actions run 42227187422 (commit caf2560d0c908192db77db56049574c03dc0f3aa) stopped at SDK setup with `sdkmanager: command not found`; the Ubuntu runner did not have Android command-line tools on PATH. The workflow had assumed the SDK manager was preinstalled. Replaced it with pinned `android-actions/setup-android@v4.0.4`, which installs the tools and requested API 36 packages; rerun pending.
