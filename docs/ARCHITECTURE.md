# Architecture and next changes

## Stack

One Android application module, Kotlin, Compose Material 3, an Activity, a lifecycle-aware ViewModel, StateFlow and SavedStateHandle. Coil loads bundled photos asynchronously into a shared memory cache; all image requests use local resource IDs. No backend, DI framework, WebView or extra permissions are needed.

## Source map

```text
app/src/main/
├── AndroidManifest.xml
├── java/com/khodehydro/faimess/
│   ├── MainActivity.kt                 # Splash, edge-to-edge, app entry
│   ├── data/DemoContent.kt             # People, stories, the site's demo tracks/playlists, sample posts
│   ├── data/DemoLyrics.kt              # The site's bilingual lyric sheets per track id
│   ├── model/FeedModels.kt             # Immutable models (Track, Playlist, FeedPost…), pure transformations, formatters
│   ├── model/PlayerModels.kt           # PlayerState, RepeatMode, LyricLine, PlayerLogic (queue rules)
│   └── ui/
│       ├── FeedViewModel.kt            # Local mutations and small saved-state snapshots
│       ├── PlayerViewModel.kt          # MediaPlayer session: queue, clock, repeat/shuffle, likes, audio focus
│       ├── LatestSnackbar.kt           # Latest feedback wins; no stale message queue
│       ├── FaimessApp.kt               # App shell, navigation, overlays, Android sharing
│       ├── components/
│       │   ├── AppHeader.kt
│       │   ├── StoriesRow.kt
│       │   ├── PostCard.kt             # Card = header + caption + PlaylistPanel + engagement row
│       │   ├── TrackRow.kt             # Track row, playlist cover, facts line (shared by card and page)
│       │   ├── FloatingNavigation.kt   # FeedTab (outlined/filled icon pair), capsule + search circle
│       │   ├── MiniPlayer.kt           # Purple capsule above the menu: cover, title, play/pause, next
│       │   ├── FeedSheets.kt
│       │   ├── StoryViewer.kt
│       │   └── Common.kt
│       ├── screens/                    # Home, Discover, Calendar, Profile, Search, PlaylistScreen, PlayerScreen
│       └── theme/                      # FAIMESS palette (Color.kt), typography, light theme
└── res/
    ├── drawable/                       # Vector icons (project-authored + Material Icons pairs)
    ├── drawable-nodpi/                 # Avatars, story scenes, album_*/playlist_* covers, brand_logo.png
    ├── mipmap-*/                       # Adaptive launcher foreground cut from the logo (see tools/)
    ├── font/                           # Bundled Manrope
    ├── raw/                            # faimess_demo.mp3 — the site's demo master
    └── values/                         # Strings and startup theme
```

## State and events

- Composables receive immutable models and callbacks. Post rendering does not talk to an API or mutate fixtures.
- `FeedViewModel` owns a `StateFlow<FeedUiState>` and applies explicit like/save/comment/publish events.
- `FeedActions` contains pure transformations, query filtering and input limits, independently unit tested.
- `SavedStateHandle` stores primitives / string lists for likes, saves, local captions, comments, self story and the unread marker. It restores from Android's saved instance state; it is **not disk persistence**. Swiping away/finishing the task or clearing app data can discard local edits.
- New local posts are bounded at 20, comments at 50 in total, captions at 500 characters and comments at 280 characters, keeping saved instance state small.
- Selected tab, active sheet/story, the open playlist post (`openPlaylistPostId`) and text drafts use `rememberSaveable`. A `SaveableStateHolder` retains independent tab UI state and the home list retains its scroll position.
- Home uses `LazyColumn` / stories use `LazyRow` with stable IDs; Discover uses `LazyVerticalGrid`.
- Modal sheets, the story dialog and the playlist page own their Android Back behavior. Back from a secondary tab returns Home before leaving the Activity.
- `PlaylistScreen` is an overlay inside `FaimessScaffold`, drawn above the tab content and below the floating menu and the snackbar host. It receives the post and the shared `PostInteractions` (so likes, comments, options, sharing and author taps behave exactly as on the card), the other posts for its *more playlists* row, and reports play requests upward; nothing in it talks to audio. Choosing any tab closes it.
- Sharing opens the real Android text share sheet; no fake remote post URL is generated and counters do not falsely increment when the chooser opens.
- **Playback** lives in `PlayerViewModel` (an `AndroidViewModel` scoped to the Activity, so the session survives tab changes and the playlist page). It owns one `MediaPlayer` prepared from `res/raw/faimess_demo` with `USAGE_MEDIA` attributes, exposes `StateFlow<PlayerState>` (queue, index, playing, position/duration, repeat, shuffle, liked ids) and applies `play / playAll / shuffleAll / playAt / toggle / next / previous / seekTo / toggleRepeat / toggleShuffle / toggleLike`. A 250 ms ticker copies the engine's position into state while playing; `onCompletion` applies `PlayerLogic.nextIndex`. Audio focus is requested on start and abandoned on pause; a transient loss pauses and resumes on regain. If the engine cannot be created or reports an error, the session continues on a simulated clock (`SystemClock.elapsedRealtime`) with the catalogue length, so the interface and the device tests never depend on an audio route. The queue rules are pure (`PlayerLogic`) and unit tested. Playback ends with the Activity: there is no foreground service, media session or notification yet, and `PlayerState` is not persisted.
- `FaimessScaffold` provides `LocalBottomChromeHeight` (menu height, plus the mini player's once a song is loaded) and the scrolling screens read it as bottom content padding. The bottom stack is a `Column`: `AnimatedVisibility(MiniPlayer)` above `FloatingNavigation`, hidden together while the keyboard is up. `PlayerScreen` is a full-size overlay above the stack and below the snackbar host; `playerOpen` is `rememberSaveable` and the screen owns its Back handling.
- `PlaylistPlayback` (play all / shuffle / one song) is what the playlist page reports; `PostInteractions.onPlay` is the feed card's play glyph. Both resolve to `PlayerViewModel` calls in `FaimessApp` with the playlist as the queue and its name as the queue label.

## Where to change product requirements

| Request | Main location |
| --- | --- |
| Change logo/name | Replace `res/drawable-nodpi/brand_logo.png`, run `python3 tools/brand_assets.py`, edit `app_name` in `strings.xml` |
| Change brand colours | `ui/theme/Color.kt` (mirrors the web app's `@theme` tokens) and `res/values/colors.xml` |
| Change post layout | `PostCard.kt` (card; `PostAuthorHeader` and `PostEngagementRow` are shared with the playlist page) and `TrackRow.kt` (track rows, cover, facts) |
| Change the playlist page | `screens/PlaylistScreen.kt` |
| Change nav icons | The `FeedTab` pairs in `FloatingNavigation.kt` (`*_outline` idle, `*_filled` selected) |
| Add/remove a tab | `FeedTab` in `FloatingNavigation.kt` and `FaimessScaffold` in `FaimessApp.kt` |
| Change initial content | `DemoContent.kt` (tracks → playlists → posts) |
| Introduce real API/storage | Add a repository/data source mapping `/api/tracks` and `/api/social/playlists` onto `Track`/`Playlist`; replace ViewModel fixture reconstruction |
| Add audio playback | Replace the `onPlay` snackbar in `FaimessApp.kt` with a lifecycle-aware Media3 player fed by the track's `streamUrl` |
| Add upload/auth/notifications | New integrations, after their behavior and permissions are specified |
| Persist user edits | Add Room/DataStore as appropriate; do not treat SavedStateHandle as a database |
| Translate/add RTL or dark theme | New resources, direction-aware layouts, explicit alternate theme and screenshots |

Avoid carrying the demo's drawable resource IDs into future network-domain models: `Track.cover` / `Playlist.cover` are `@DrawableRes` only because the demo is offline; the API returns `photo` URLs, so a production media model should represent local and remote sources explicitly. Keep transport, authentication and persistence outside composables.

## Responsiveness changes (0.1.1)

`components/OfflinePhoto.kt` centralizes asynchronous local-image loading and shared cache sizing. `PostInteractions` is remembered by the app and carries IDs, while a post's header, caption and media receive narrowly scoped inputs. `LatestSnackbar` cancels superseded feedback, not actions. See [PERFORMANCE.md](PERFORMANCE.md) for the optional optimized build and honest on-device measurement guidance.
