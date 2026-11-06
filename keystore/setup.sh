#!/usr/bin/env bash
set -euo pipefail
# This project has an existing permanent key; never generate a replacement.
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIR="${Y700_SIGNING_DIR:-/root/.local/share/y700-launcher-plus-signing}"
KEY="$DIR/release.keystore"
PASSFILE="$DIR/signing.password"
ALIAS="y700-launcher-plus"
REPO="pulipulichen/Y700-Launcher-Plus"
EXPECTED="8F:4C:5A:D0:18:AA:5F:E5:32:C4:F1:FF:12:60:EF:08:02:82:48:9D:C6:D2:03:2A:5E:BD:62:AF:18:EE:54:B3"
MODE="${1:---verify}"

usage() {
  cat <<'EOF'
Usage: ./keystore/setup.sh [--verify|--sync-github]
Default signing directory: /root/.local/share/y700-launcher-plus-signing (CT 9102).
Override Y700_SIGNING_DIR only when restoring the same permanent identity.
--verify: confirm identity without printing secrets.
--sync-github: verify and update four encrypted GitHub Actions SIGNING_* secrets.
The permanent identity is managed inside PVE CT 9102 (android-keystore).
The script never generates or replaces an existing signing key.
EOF
}
case "$MODE" in
  --verify|--sync-github) ;;
  --help|-h) usage; exit 0 ;;
  *) usage >&2; exit 2 ;;
esac
[[ $# -le 1 ]] || { usage >&2; exit 2; }
umask 077
[[ -s "$KEY" && -s "$PASSFILE" ]] || { echo "ERROR: missing permanent key or password; refusing to rotate key." >&2; exit 1; }
command -v openssl >/dev/null || { echo "ERROR: openssl is required." >&2; exit 1; }
fingerprint="$(openssl pkcs12 -in "$KEY" -passin "file:$PASSFILE" -clcerts -nokeys 2>/dev/null | openssl x509 -noout -fingerprint -sha256 | cut -d= -f2)"
[[ "$fingerprint" == "$EXPECTED" ]] || { echo "ERROR: unexpected signing certificate; no secrets changed." >&2; exit 1; }
echo "Verified permanent signing certificate: $fingerprint"
if command -v keytool >/dev/null 2>&1; then
  password="$(tr -d '\n' < "$PASSFILE")"
  keytool -list -keystore "$KEY" -storepass "$password" -alias "$ALIAS" >/dev/null 2>&1 || { echo "ERROR: password or alias invalid." >&2; exit 1; }
  unset password
fi
if [[ "$MODE" == --sync-github ]]; then
  gh_cmd="${GH_BIN:-gh}"
  command -v "$gh_cmd" >/dev/null || { echo "ERROR: gh CLI is required." >&2; exit 1; }
  "$gh_cmd" auth status >/dev/null 2>&1 || { echo "ERROR: authenticate gh with an authorized account first." >&2; exit 1; }
  base64 < "$KEY" | tr -d '\n' | "$gh_cmd" secret set SIGNING_KEYSTORE_BASE64 --repo "$REPO"
  tr -d '\n' < "$PASSFILE" | "$gh_cmd" secret set SIGNING_STORE_PASSWORD --repo "$REPO"
  tr -d '\n' < "$PASSFILE" | "$gh_cmd" secret set SIGNING_KEY_PASSWORD --repo "$REPO"
  printf '%s' "$ALIAS" | "$gh_cmd" secret set SIGNING_KEY_ALIAS --repo "$REPO"
  echo "Updated four repository signing secrets without displaying their values."
fi
