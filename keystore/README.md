# Permanent keystore for Y700 Launcher Plus

The permanent signing identity was created on 2026-11-06, not committed to the public repository.

- Host: puli-home-pve
- Key: /root/.local/share/y700-launcher-plus/release.keystore
- Password: /root/.local/share/y700-launcher-plus/signing.password
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

From a clone of this repository on the PVE host, after authenticating GitHub CLI (gh):

    export Y700_SIGNING_DIR=/root/.local/share/y700-launcher-plus
    ./keystore/setup.sh --verify
    ./keystore/setup.sh --sync-github

The script checks the SHA-256 certificate fingerprint before uploading encrypted secrets. It never prints passwords, private keys, or Base64. Set GH_BIN to an alternate gh executable if necessary.

## Avoid breaking installed apps

Existing Y700 APKs were previously signed with the temporary Y700_TEST_KEYSTORE_BASE64 secret. Until the four SIGNING_* secrets are installed, debug CI continues using that same fixed test identity; partial SIGNING_* configurations cause explicit failure. The release AAB workflow requires the permanent identity without fallback.

Switching to the permanent signature cannot update APKs installed with the earlier test certificate in place. Export the launcher layout, uninstall the old test-signed app, then install the permanently signed APK after verifying the CI build. This migration is one time only.

Google Play App Signing can use a separate device-side app certificate from the upload key. Play-installed APKs may therefore not accept direct sideload updates signed with the upload certificate.

Do not put the keystore, password, properties, or Base64 into Git, logs, workflow artifacts, or GitHub Releases.
