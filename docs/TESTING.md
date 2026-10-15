# Build and verification

## Prerequisites

JDK 17, Android SDK 35, a working internet connection for the **first build**, and the checked-in Gradle Wrapper. Android Studio setup and clone instructions are in the root README. The app itself does not need internet.

## Local verification

macOS / Linux:

```bash
./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest :app:assemblePerformance
```

Windows PowerShell:

```powershell
.\gradlew.bat --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest :app:assemblePerformance
```

With an emulator or Android 8+ device connected:

```bash
./gradlew :app:connectedDebugAndroidTest
```

Use `gradlew.bat` instead on Windows. Tests are under `app/src/test` and `app/src/androidTest`.

## Coverage

### Unit tests

- Immutable/reversible likes and saved posts, non-negative counts.
- Comment whitespace/length validation and count updates.
- Compact label boundaries; case-insensitive search over owner, caption, playlist name/mood and track title/artist.
- `formatClock` / `formatLength` (`3:28`, `20 min`, `1 h 12 min`) and the demo playlists only referencing catalogue tracks.
- Initial offline fixtures; publish ordering and validation.
- State reconstruction from a new SavedStateHandle.
- Unknown IDs, bounded local post/comment storage, invalid story input.
- Player queue rules (`PlayerLogicTest`): next/previous with and without wrap, repeat off/all/one, the repeat cycle, shuffle keeping the playing song first without losing tracks, the active lyric line, `PlayerState` derivations (progress, remaining, seeded likes), and every lyric sheet belonging to a catalogue song in time order.

### Instrumented Compose tests

- Reference launch elements and selected Home tab.
- Like interaction and Activity recreation.
- Story open, next and close.
- Comment entry and blank-input prevention.
- Publish flow and blank-input prevention.
- Saving and retrieving a post from Profile.
- All five navigation destinations; the selected tab is the only one marked selected, and Search lives outside the capsule.
- Search empty state and clearing the query.
- Opening a playlist from its card: the page shows songs hidden behind "+n more tracks", Play starts the first song in the mini player, back returns to the feed with the mini player still there.
- Tapping a song on the playlist page: the mini player shows it, pause/resume toggles its label, tapping the bar opens the full player with the title, the `Song 2 of 6` queue pill and the bilingual lyric sheet; the *Up next* tab jumps back to the first song (`Song 1 of 6`), next advances, repeat and like update their labels and unliking shows its snackbar; Back collapses the player and the mini player keeps playing.
- The feed card's play glyph starts the playlist without leaving the feed; the mini player's next button advances it.
- Liking on the playlist page updates the same like on the feed card (system Back closes the page).
- Discover tiles open the same playlist page.
- The playlist page's *more playlists* row swaps the page, its comments button opens the comments sheet, and the floating menu stays usable above the page.
- Capture of the actual rendered home screen (`home.png`).

## GitHub Actions

[Android CI](https://github.com/khodehydro/faimess-android/actions/workflows/android.yml) runs on pushes to `main` and `arena/**`, pull requests targeting `main`, and manual dispatch.

1. **Build, unit tests & lint** verifies the wrapper, installs the toolchain, compiles the app/tests, runs unit tests and Android Lint, and uploads the debug APK and reports.
2. **Compose device tests & screenshot** runs on an Android 15 / API 35 emulator after the build job, and uploads device-test reports and app screenshots.

Open a successful run and look under **Artifacts**:

| Artifact | Contents |
| --- | --- |
| `faimess-debug-apk` | Installable debug `app-debug.apk` |
| `faimess-performance-apk` | Optimized, non-debuggable, debug-signed local test `app-performance.apk` |
| `verification-reports` | Unit tests and lint HTML reports |
| `device-test-reports` | Instrumented Compose test reports |
| `app-screenshots` | Actual rendered Compose home screenshot |

Artifacts are temporary GitHub Actions downloads, not version-controlled build outputs or a signed production release. A GitHub sign-in may be required to download them.

## Manual visual review

- Compare the launch screen to the user's reference at approximately 393 dp width.
- Scroll through posts and horizontally through stories; verify the bottom bar overlays rather than displaces the feed.
- Try gesture navigation and three-button navigation; no content controls should sit under system bars.
- Try 320 dp width, a wide device, landscape, and larger font scale. Scroll when content does not fit.
- Open input sheets with the keyboard visible; verify their controls and Back behavior.
- Turn on airplane mode: all reference imagery and type remain available.
- In Android Studio, open the three `ReferenceFeedPreview` configurations.

## Validation transparency

The development sandbox does not include Java, an Android SDK or an emulator, and its network cannot fetch Google Maven directly. A successful static check in that environment is **not** an Android build. CI is the source for compile/test results; the development log records the observed run status separately.

## Responsiveness regression checks

Version 0.1.1 adds rapid like/save state tests, latest-only Snackbar tests, batched touch input, navigation/state checks and an actual memory-cache reuse test for the bundled scenery. These are correctness tests, not a measured claim of touch latency or FPS.

Screenshot capture now waits for the asynchronous photos and uses the test automation shell to save `/sdcard/Download/faimess-screenshots/home.png`, which survives APK cleanup. `UiAutomation.executeShellCommand` starts each program directly rather than through a shell, so the test issues `mkdir`, `screencap` and `stat` as three separate commands (the 0.1.1 version chained them with `&&`, which was never interpreted and produced an empty result). This is test-only; the normal app does not take screenshots or require storage permissions. `.github/scripts/run-device-tests.sh` preserves test failures and publishes their log tail as a check annotation.

Use the [performance guide](PERFORMANCE.md) to compare on a real device without the debugger.
