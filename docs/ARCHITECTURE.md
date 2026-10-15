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
│   ├── model/FeedModels.kt             # Immutable models (Track, Playlist, FeedPost…), pure transformations, formatters
│   └── ui/
│       ├── FeedViewModel.kt            # Local mutations and small saved-state snapshots
│       ├── LatestSnackbar.kt           # Latest feedback wins; no stale message queue
│       ├── FaimessApp.kt               # App shell, navigation, overlays, Android sharing
│       ├── components/
│       │   ├── AppHeader.kt
│       │   ├── StoriesRow.kt
│       │   ├── PostCard.kt             # Card = header + caption + PlaylistPanel + engagement row
│       │   ├── TrackRow.kt             # Track row, playlist cover, facts line (shared by card and page)
│       │   ├── FloatingNavigation.kt   # FeedTab (outlined/filled icon pair), capsule + search circle
│       │   ├── FeedSheets.kt
│       │   ├── StoryViewer.kt
│       │   └── Common.kt
│       ├── screens/                    # Home, Discover, Calendar, Profile, Search, PlaylistScreen (overlay page)
│       └── theme/                      # FAIMESS palette (Color.kt), typography, light theme
└── res/
    ├── drawable/                       # Vector icons (project-authored + Material Icons pairs)
    ├── drawable-nodpi/                 # Avatars, story scenes, album_*/playlist_* covers, brand_logo.png
    ├── mipmap-*/                       # Adaptive launcher foreground cut from the logo (see tools/)
    ├── font/                           # Bundled Manrope
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
- `PlaylistScreen` is an overlay inside `FaimessScaffold`, drawn above the tab content and the floating menu and below the snackbar host. It receives the post (so likes stay in sync with the card) and reports play requests upward; nothing in it talks to audio.
- Sharing opens the real Android text share sheet; no fake remote post URL is generated and counters do not falsely increment when the chooser opens.

## Where to change product requirements

| Request | Main location |
| --- | --- |
| Change logo/name | Replace `res/drawable-nodpi/brand_logo.png`, run `python3 tools/brand_assets.py`, edit `app_name` in `strings.xml` |
| Change brand colours | `ui/theme/Color.kt` (mirrors the web app's `@theme` tokens) and `res/values/colors.xml` |
| Change post layout | `PostCard.kt` (card) and `TrackRow.kt` (track rows, cover, facts) |
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
