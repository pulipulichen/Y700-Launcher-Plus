# Taskbar and drawer implementation / acceptance

Updated: 2026-11-07. Implementation is an incremental `lawnchair/drawer.patch` applied after the existing identity and Chinese-search patches to the pinned Lawnchair 15 source. The legacy Compose app is not the preview build target.

## Issue 3 behavior

- Full-screen drawer retains native search input, app list, popup shortcuts, gestures and animation controllers. Search defaults to the bottom and the frequent row sits above it. Each has an independent bottom/top switch. At the top, search precedes recommendations; at the bottom, recommendations precede search. No overlapping configurations are offered.
- The fixed row contains icons and labels. Its count follows available width (80 dp per slot, at most 12). Grid columns automatically follow the active device profile width; disable automatic columns to use the native manual selector.
- Each opening recomputes recommendations. Pinned apps sort first, then `ln(1 + launches) + 4 / (1 + ageDays)`, followed by case-insensitive label and component/profile key for deterministic ties. Future timestamps have zero age. Until there are launch events, alphabetical order provides a working fallback.
- Launches are recorded by the launcher's successful activity-launch path. This deliberately uses launcher-local history without requesting system Usage Access; app use outside this launcher is not observed. No prompt for unused permissions is needed. Uninstalling or clearing app storage removes history. The Android backup allowlist does not include the history preference file.
- Long-press retains Lawnchair app info, shortcuts and customization, and adds pin/unpin, exclude/include, hide and search alias. Pin/exclusion managers remain available even if a pinned/excluded app is absent from the row. Native hidden-app settings restore hidden apps. Private/work-profile apps never appear in the personal frequent row.
- Searching hides recommendations; empty input brings the row back. Native launch/return handling resets the search. The keyboard-on-open default is enabled, with the existing native setting available.
- Both app-only and local-search paths match displayed names plus independently stored search aliases and bidirectional Traditional/Simplified Chinese normalization. Exact normalized title/alias matches sort before other matches; launch count breaks ties. Existing pinyin support and hidden-app rules remain.
- Search settings reuse the existing Lawnchair drawer-search page and its approved native options. No additional search sources or unapproved new switches were added. Aliases can also be managed from drawer preferences.
- Preference and action strings are supplied in English, Traditional Chinese, Simplified Chinese and Japanese.

## Issue 2 observed boundary

Device: Lenovo TB320FC, Android 15. Previously recorded ZUI version: 17.0.350. The currently installed preview was `15.0.0-beta3-y700`, version code 29900275. At investigation time the selected HOME was `com.zui.launcher/.drawer.NormalLauncher`.

Read-only ADB evidence:

- The `Taskbar` window is owned by UID 10103 / `com.zui.launcher`, type `NAVIGATION_BAR_PANEL`, with bottom navigation/system-gesture inset providers.
- `com.zui.launcher.permission.READ_SETTINGS` and `WRITE_SETTINGS` are `signature|privileged`.
- Querying `content://com.zui.launcher.settings/favorites` from shell failed in the provider permission path (this firmware returned a NullPointerException). This is a failed query, not proof that all possible vendor integrations are impossible.
- `run-as com.zui.launcher id` returned `package not debuggable`.
- SystemUI binds ZUI Quickstep's `TouchInteractionService`; the native taskbar is not the third-party HOME hotseat.

The app provides an explicit standard HOME intent scoped to `com.zui.launcher`, so users can edit the native bar in its owner without changing their default HOME. This is a convenience entry, **not direct in-app synchronization**. Existing Lawnchair HOME hotseat editing remains separate.

No root, signature-permission grant, private database editing, proprietary API or overlay replacement was added. Direct cross-app Taskbar editing/synchronization from the third-party launcher remains blocked pending a supported vendor integration or a separate product decision. Keep Issue 2 open.

## Validation state

- Patch/resource review performed; compile, signature, host ranking tests and Release results are recorded in CHANGELOG.md when available.
- The ADB connection initially worked, then disappeared; connection to the original port 35595 was refused and mDNS returned no service. The subsequent planned HOME / Settings / file-manager test did not execute any successful ADB action. Default HOME was therefore not changed by that attempt.
- Device acceptance must still cover all four position combinations, portrait/landscape, IME and back behavior, native long-press actions, search aliases/Chinese, launch reset, and persistence after restart. Do not mark Issue 3 fully accepted until these pass.
