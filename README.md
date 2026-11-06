# Y700 Launcher Plus

An independent Android HOME launcher for Lenovo Legion Y700 tablets. Phase 0 tests whether a third-party launcher can coexist with ZUI's native taskbar without root access. **Compatibility is not yet confirmed.**

## Status

- Phase 0 debug APK builds and passes lint in GitHub Actions.
- On the Y700, Android accepted the Phase 0 app as HOME and its Activity launched. Window-manager diagnostics reported the ZUI Taskbar visible, but the notification shade obscured the launcher UI, so pixel-level coexistence remains unverified.
- Still to test: app foreground, portrait/landscape, split screen, floating windows and reboot.
- Planned: tablet app drawer, favorites, customizable workspace and fallback taskbar where required.
- Commercialization: future paid Google Play app with permanent application ID, signing identity, privacy documentation and release AAB.

## Development

The Phase 0 workflow at `.github/workflows/android-phase0.yml` uses JDK 17, Android SDK 36, and Gradle 8.13. It compiles and lints the debug app on relevant `main` changes and manual dispatch, then publishes the APK and SHA-256 checksum to the `phase0-latest` prerelease. CI installs Gradle directly; a Gradle Wrapper is not committed.

See the [Phase 0 test plan](docs/PHASE0.md) and [architecture](docs/ARCHITECTURE.md).

Independent project; not affiliated with Lenovo. ZUI, Legion and Y700 are trademarks of their respective owners.
