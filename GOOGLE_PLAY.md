# Google Play paid release checklist

- Application ID stays info.pulipuli.y700launcherplus.
- Permanent private upload keystore is managed in PVE CT 9102 (`android-keystore`). See keystore/README.md.
- Four SIGNING_* GitHub Actions secrets were synced from CT 9102 on 2026-11-06; verify the Secrets configuration before release.
- Use Google Play App Signing. Preserve a secure off-host backup of the upload key and password.
- Trigger Signed Google Play upload AAB manually with a new vMAJOR.MINOR.PATCH tag; AABs are published to GitHub Releases.
- The workflow uses UTC epoch minutes as increasing versionCode; avoid two releases in one minute.
- Verify current Play target SDK policy at submission time (current targetSdk 35).
- Provide privacy policy, accurate Data safety declarations, paid-app setup, and required legal details.
- Review IP, device support, permissions, and native ZUI taskbar compatibility before publishing.

## Drawer recommendations and search aliases

The Lawnchair preview stores launcher-originated launch counts/timestamps, pinned/excluded app component/profile keys and optional search aliases in app-private local preferences. These records are not uploaded, do not require Usage Access, and are excluded by the existing Android backup allowlist. Clearing app storage or uninstalling removes them. Any future cloud backup or system Usage Access integration requires a new data-flow and privacy review. Document these actual behaviors in the final privacy policy and Data safety submission; the app is not ready for Play submission yet.

Existing Lawnchair web suggestions are a separate data flow: when enabled, typed search queries can be sent to the configured provider, including an alias the user types. Do not describe all search activity as offline or local-only in the privacy policy. Recommendation counters and timestamps are not included in those requests.
