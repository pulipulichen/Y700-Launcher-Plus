# Nova Launcher feature parity

Reference scope: [Nova Launcher](https://www.novalauncher.com/), its [Prime feature list on Google Play](https://play.google.com/store/apps/details?id=com.teslacoilsw.launcher), and the [official FAQ](https://www.novalauncher.com/faq/). The project uses its own tablet layout and Material styling.

## 0.2.0

- Home and searchable app drawer are separate. Long-press an app in the drawer to add it to the current home page, add it to the dock, or hide it from the drawer.
- Swipe horizontally between home pages. Settings can add a page or remove the last empty page. Long-press a home icon to move it to a neighboring page or remove it.
- Configure home columns and label visibility. Hidden apps can be restored in Settings.
- Launcher dock shortcuts are shared across home pages. The native ZUI taskbar is still drawn and controlled by ZUI.
- Upgrade keeps previous pins on the first page. No new Android permissions.

## Remaining milestones

- Desktop editor: persistent cell coordinates and order, drag and drop, subgrid placement, widgets and resizing, folders, pinned app shortcuts, wallpaper, page management, scroll effects and dock pages.
- Drawer and search: tabs and folders, independent drawer layout, horizontal and vertical modes, customizable cards, searchable shortcuts, contacts, calculations and unit conversions, and available external integrations.
- Appearance: icon packs, individual icon and label editing, adaptive icon shapes, size and spacing, custom theme and label colors, explicit light/dark/scheduled modes.
- Prime interactions: configurable single- and multi-finger gestures, pinch and double-tap, per-icon swipe actions, notification dots and badges, and optional system actions gated by public APIs and user-granted permissions.
- Backup and restore: versioned export/import through Android document providers, missing-app handling, and widget rebinding.
- Optional integrations: assess Google Discover, third-party search and other service access individually. Verify requirements and behavior before claiming parity.

## Y700 acceptance

Run on the TB320FC in both orientations. Upgrade the previous APK without clearing its data, open a home page, change pages, open and dismiss the drawer, add/remove a dock shortcut, hide/restore an app, launch and return from another app, enter split screen or a floating window, and reboot. Verify the native ZUI taskbar remains visible and usable when ZUI is configured to show it. Test the dock and the ZUI taskbar separately, including the bottom-most touch targets. Record the APK source commit and ZUI build.
