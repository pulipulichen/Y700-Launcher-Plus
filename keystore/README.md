# Permanent keystore for Y700 Launcher Plus

The permanent signing identity was created on 2026-11-06, not committed to the public repository.

- Signing manager: PVE CT 9102 (`android-keystore`); GitHub CLI logged in as `pulipulichen`
- Key: /root/.local/share/y700-launcher-plus-signing/release.keystore
- Password: /root/.local/share/y700-launcher-plus-signing/signing.password
- PKCS12, RSA-3072, alias y700-launcher-plus
- Certificate SHA-256: 8F:4C:5A:D0:18:AA:5F:E5:32:C4:F1:FF:12:60:EF:08:02:82:48:9D:C6:D2:03:2A:5E:BD:62:AF:18:EE:54:B3

Both source files are root-only. Back up the key and password together to an independently secured, preferably encrypted offline location. Do not generate or use a second key, including one from Android-Website-Shortcut.

## Configure GitHub Actions Secrets

The workflows expect these four secrets:

- SIGNING_KEYSTORE_BASE64: Base64-encoded PKCS12 bytes
- SIGNING_STORE_PASSWORD: value of signing.password
- SIGNING_KEY_PASSWORD: same value as store password
- SIGNING_KEY_ALIAS: y700-launcher-plus

Secrets management: https://github.com/pulipulichen/Y700-Launcher-Plus/settings/secrets/actions

Run from a clone of this repository **inside CT 9102**, where `gh auth login` is already completed:

    ./keystore/setup.sh --verify
    ./keystore/setup.sh --sync-github

On 2026-11-06 the permanent signing identity was safely moved from the PVE host to CT 9102, verified byte-for-byte, and all four `SIGNING_*` GitHub Actions secrets were uploaded using the CT's authenticated GitHub CLI. The script checks the SHA-256 certificate fingerprint before uploading encrypted secrets. It never prints passwords, private keys, or Base64. Set GH_BIN to an alternate gh executable if necessary.

## Avoid breaking installed apps

The sideload APK and Play upload AAB workflows require all four permanent signing secrets. The sideload workflow verifies the built APK against the pinned certificate before publishing it. Earlier Y700 APKs used the temporary test certificate.

Switching to the permanent signature cannot update APKs installed with the earlier test certificate in place. Export the launcher layout, uninstall the old test-signed app, then install the permanently signed APK after verifying the CI build. This migration is one time only.

Google Play App Signing can use a separate device-side app certificate from the upload key. Play-installed APKs may therefore not accept direct sideload updates signed with the upload certificate.

Do not put the keystore, password, properties, or Base64 into Git, logs, workflow artifacts, or GitHub Releases.
