# Architecture

The app is a non-root Android HOME launcher. It uses only public Android APIs and Jetpack Compose.

## Current launcher shell

- Queries activities that declare the standard MAIN/LAUNCHER intent, using a narrow manifest package-visibility query.
- Shows installed launchable apps in an adaptive grid, with search and direct launch actions.
- Stores the user's pinned app package names in app-private preferences. The first run pins up to eight apps; users can add or remove pins.
- Leaves system navigation and the native ZUI taskbar owned by Android/ZUI.

The package/application ID is `info.pulipuli.y700launcherplus` and must remain stable for sideloaded updates.

## Device-specific boundary

The Android HOME screen and ZUI taskbar were visible together in the supplied TB320FC screenshot. This confirms the basic HOME/taskbar coexistence path on that device and software build. Rotation, split screen, floating windows, reboot persistence, other Y700 generations and future ZUI updates still require device checks.
