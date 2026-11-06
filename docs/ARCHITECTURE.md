# Architecture

The app is a non-root Android HOME launcher. It uses only public Android APIs and Jetpack Compose.

## Current launcher shell

- Queries activities that declare the standard MAIN/LAUNCHER intent, using a narrow manifest package-visibility query.
- Refreshes the package catalog when HOME resumes. The drawer offers search and hidden-app filtering.
- Stores ordered home pages, dock shortcuts, hidden apps and grid settings as one versioned layout in app-private preferences. Older pin sets migrate to page one.
- Leaves system navigation and the native ZUI taskbar owned by Android/ZUI.

The package/application ID is `info.pulipuli.y700launcherplus` and must remain stable for sideloaded updates.

## Device-specific boundary

The Android HOME screen and ZUI taskbar were visible together in the supplied TB320FC screenshot. This confirms the basic HOME/taskbar coexistence path on that device and software build. Rotation, split screen, floating windows, reboot persistence, other Y700 generations and future ZUI updates still require device checks.
