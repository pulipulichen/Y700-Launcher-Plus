#!/usr/bin/env bash
set -euo pipefail

project_root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
upstream_commit=505dbc40e6154c05158b5d0271c45f6a885a411b
systemui_commit=6a11ef767998885838a599331b5485f768b3d725
checkout=${1:?Usage: ./lawnchair/prepare.sh NEW_CHECKOUT_DIRECTORY}

git clone --depth=1 --branch v15.0.0-beta3.0 \
  https://github.com/LawnchairLauncher/lawnchair.git "$checkout"
cd -- "$checkout"
test "$(git rev-parse HEAD)" = "$upstream_commit"
git submodule update --init --depth=1
test "$(git -C platform_frameworks_libs_systemui rev-parse HEAD)" = "$systemui_commit"
git apply "$project_root/lawnchair/y700.patch"
git apply "$project_root/lawnchair/chinese-search.patch"
git apply "$project_root/lawnchair/drawer.patch"
install -D -m 644 "$project_root/app/src/main/res/mipmap-nodpi/ic_launcher.png" \
  lawnchair/res/drawable-nodpi/y700_launcher_icon.png
