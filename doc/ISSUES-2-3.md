# Taskbar and drawer implementation / acceptance

Updated: 2026-11-07. Implementation is an incremental `lawnchair/drawer.patch` applied after the existing identity and Chinese-search patches to the pinned Lawnchair 15 source. The legacy Compose app is not the preview build target.

## Issue 3 behavior

- Full-screen drawer retains native search input, app list, popup shortcuts, gestures and animation controllers. Search defaults to the bottom and the frequent row sits above it. Each has an independent bottom/top switch. At the top, search precedes recommendations; at the bottom, recommendations precede search. No overlapping configurations are offered.
- The fixed row contains icons and labels. Its count follows available width (80 dp per slot, at most 12). With the landscape IME visible it uses a 56 dp horizontal icon-and-label row and 160 dp slots to leave room for complete grid cells. Grid columns automatically follow the active device profile width; disable automatic columns to use the native manual selector.
- Each opening recomputes recommendations. Pinned apps sort first, then `ln(1 + launches) + 4 / (1 + ageDays)`, followed by case-insensitive label and component/profile key for deterministic ties. Future timestamps have zero age. Until there are launch events, alphabetical order provides a working fallback.
- Launches are recorded by the launcher's successful activity-launch path. This deliberately uses launcher-local history without requesting system Usage Access; app use outside this launcher is not observed. No prompt for unused permissions is needed. Uninstalling or clearing app storage removes history. The Android backup allowlist does not include the history preference file.
- Long-press retains Lawnchair app info, shortcuts and customization, and adds pin/unpin, exclude/include, hide and search alias. Pin/exclusion managers remain available even if a pinned/excluded app is absent from the row. Native hidden-app settings restore hidden apps. Private/work-profile apps never appear in the personal frequent row.
- Searching hides recommendations; empty input brings the row back. Native launch/return handling resets the search. The keyboard-on-open default is enabled, with the existing native setting available.
- Both app-only and local-search paths match displayed names plus independently stored search aliases and bidirectional Traditional/Simplified Chinese normalization. Exact normalized title/alias matches sort before other matches; launch count breaks ties. Existing pinyin support and hidden-app rules remain.
- Search settings reuse the existing Lawnchair drawer-search page and its native options. Existing web suggestion providers may send typed query text (including a typed alias) to the configured provider when enabled; local recommendation history itself is not sent. No additional search sources or unapproved new switches were added. Aliases can also be managed from drawer preferences.
- Preference and action strings are supplied in English, Traditional Chinese, Simplified Chinese and Japanese.

## Issue 2 observed boundary

Device: Lenovo TB320FC, Android 15. Previously recorded ZUI version: 17.0.350. The currently installed preview was `15.0.0-beta3-y700`, version code 29900275. Firmware build ID observed on device: `TB320FC_CN_OPEN_USER_Q00031.0_V_ZUXOS_1.1.350_ST_250418`. At investigation time the selected HOME was `com.zui.launcher/.drawer.NormalLauncher`.

Read-only ADB evidence:

- The `Taskbar` window is owned by UID 10103 / `com.zui.launcher`, type `NAVIGATION_BAR_PANEL`, with bottom navigation/system-gesture inset providers.
- `com.zui.launcher.permission.READ_SETTINGS` and `WRITE_SETTINGS` are `signature|privileged`.
- Querying `content://com.zui.launcher.settings/favorites` from shell failed in the provider permission path (this firmware returned a NullPointerException). This is a failed query, not proof that all possible vendor integrations are impossible.
- `run-as com.zui.launcher id` returned `package not debuggable`.
- SystemUI binds ZUI Quickstep's `TouchInteractionService`; the native taskbar is not the third-party HOME hotseat.

The app provides an explicit standard HOME intent scoped to `com.zui.launcher`, so users can edit the native bar in its owner without changing their default HOME. This is a convenience entry, **not direct in-app synchronization**. Existing Lawnchair HOME hotseat editing remains separate.

No root, signature-permission grant, private database editing, proprietary API or overlay replacement was added. Direct cross-app Taskbar editing/synchronization from the third-party launcher remains blocked pending a supported vendor integration or a separate product decision. Keep Issue 2 open.

## Validation state

- Incremental patch verified against the pinned source. All 16 added strings match across four locales, and compact-row XML is included in the source patch.
- All 12 pure Java ranking/responsive-width checks pass locally; Actions runs the same checks with JDK 21. No emulator was used.
- Run 42336382337 failed Kotlin compilation on nullable LauncherApps. Null-safe lookup corrected it. Run 42336927606 passed compilation, package/permanent-certificate verification, publication and the advisory lint step. Verified SHA-256 before in-place install of that APK on Y700.
- On that installed APK, device checks passed: default bottom/bottom positions above the visible keyboard; all four independent position combinations without overlapping bounds; name search (`chrome`), temporary alias (`y700alias`), pinyin (`jisuanji`); recommendations hidden during search; Calculator launch and return to the full cleared drawer; Calculator promoted by recent history; pin priority, exclude/restore and native shortcut popup; light/dark theme switch.
- The explicit native Taskbar entry opened `com.zui.launcher/.drawer.NormalLauncher`; pressing HOME returned to our launcher and retained its HOME selection.
- In baseline landscape+IME, the original full-height recommendation row left only a 216 px app viewport. The final patch uses compact recommendations and removes the empty-header gutter. It also bounds the native fast-scroller to the scrollable region and gives each popup action a distinct icon. Final-build verification is recorded below when complete.
- Temporary test alias/pin/exclusion values were restored. Rotation started and ended at 0; night mode started and ended at no. The prior default HOME is ZUI and must be restored after final device checks. The existing Zhuyin IME was temporarily switched to English for ADB input; switch it back after final checks.
- Comprehensive bidirectional Chinese query/alias permutations, TalkBack, extreme font scaling and a full device reboot were not exercised. The actual Chinese matching source uses Android ICU Traditional-Simplified normalization in both search paths. These limits do not establish Google Play readiness.

### Final targeted device regression

Code commit: `4103b295ef1a0b944c4505a39a527507b4a91681`. Run: [42339349407](https://github.com/pulipulichen/Y700-Launcher-Plus/actions/runs/42339349407). Release: [lawnchair15-preview-42339349407-1](https://github.com/pulipulichen/Y700-Launcher-Plus/releases/tag/lawnchair15-preview-42339349407-1).

The signed APK compiled, passed the permanent-certificate/package/12 host checks, and was downloaded with its checksum verified before in-place installation and a successful cold start. The final screenshot confirms complete first-row icons in landscape with the keyboard shown. Landscape bounds: app viewport and fast scroller y=107..532, compact frequent row y=532..685, search y=685..849, with the IME below. Portrait bounds: viewport/scroller y=107..1109, frequent row y=1109..1438, search y=1438..1602. The scroller no longer overlaps the fixed row or search input.

Normal row size/count returns on portrait rotation. Local history survived the upgrade and force-stop/cold start (Calculator remained first). Original ZUI HOME, portrait rotation 0, night mode no and the Zhuyin keyboard mode were restored. Temporary test aliases, pins and exclusions were cleared.

This completes the Issue 3 implementation and targeted Y700 acceptance. The additional QA limits listed above remain explicit; no full reboot, TalkBack or exhaustive language permutations were claimed. Issue 2 remains open for the direct native editing/synchronization requirement.
