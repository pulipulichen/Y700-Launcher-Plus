# Google Play paid release checklist

- Application ID stays info.pulipuli.y700launcherplus.
- Permanent private upload keystore exists on puli-home-pve. See keystore/README.md.
- Configure all four SIGNING_* GitHub Actions secrets before running the signed AAB workflow.
- Use Google Play App Signing. Preserve a secure off-host backup of the upload key and password.
- Trigger Signed Google Play upload AAB manually with a new vMAJOR.MINOR.PATCH tag; AABs are published to GitHub Releases.
- The workflow uses UTC epoch minutes as increasing versionCode; avoid two releases in one minute.
- Verify current Play target SDK policy at submission time (current targetSdk 35).
- Provide privacy policy, accurate Data safety declarations, paid-app setup, and required legal details.
- Review IP, device support, permissions, and native ZUI taskbar compatibility before publishing.
