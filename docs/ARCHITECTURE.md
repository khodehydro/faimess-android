# Architecture and next changes

## Stack

One Android application module, Kotlin, Compose Material 3, an Activity, a lifecycle-aware ViewModel, StateFlow and SavedStateHandle. Coil loads bundled photos asynchronously into a shared memory cache; all image requests use local resource IDs. No backend, DI framework, WebView or extra permissions are needed.

## Source map

```text
app/src/main/
├── AndroidManifest.xml
├── java/com/khodehydro/faimess/
│   ├── MainActivity.kt                 # Splash, edge-to-edge, app entry
│   ├── data/DemoContent.kt             # All people, stories and sample posts
│   ├── model/FeedModels.kt             # Immutable models, pure transformations, counters
│   └── ui/
│       ├── FeedViewModel.kt            # Local mutations and small saved-state snapshots
│       ├── LatestSnackbar.kt           # Latest feedback wins; no stale message queue
│       ├── FrindleApp.kt               # App shell, navigation, overlays, Android sharing
│       ├── components/
│       │   ├── AppHeader.kt
│       │   ├── StoriesRow.kt
│       │   ├── PostCard.kt
│       │   ├── FloatingNavigation.kt
│       │   ├── FeedSheets.kt
│       │   ├── StoryViewer.kt
│       │   └── Common.kt
│       ├── screens/                    # Home, Discover, Calendar, Profile, Search
│       └── theme/                      # Colors, typography, light reference theme
└── res/
    ├── drawable/                       # Vector icons
    ├── drawable-nodpi/                 # Offline optimized photos
    ├── font/                           # Bundled Manrope
    └── values/                         # Strings and startup theme
```

## State and events

- Composables receive immutable models and callbacks. Post rendering does not talk to an API or mutate fixtures.
- `FeedViewModel` owns a `StateFlow<FeedUiState>` and applies explicit like/save/comment/publish events.
- `FeedActions` contains pure transformations, query filtering and input limits, independently unit tested.
- `SavedStateHandle` stores primitives / string lists for likes, saves, local captions, comments, self story and the unread marker. It restores from Android's saved instance state; it is **not disk persistence**. Swiping away/finishing the task or clearing app data can discard local edits.
- New local posts are bounded at 20, comments at 50 in total, captions at 500 characters and comments at 280 characters, keeping saved instance state small.
- Selected tab, active sheet/story and text drafts use `rememberSaveable`. A `SaveableStateHolder` retains independent tab UI state and the home list retains its scroll position.
- Home uses `LazyColumn` / stories use `LazyRow` with stable IDs; Discover uses `LazyVerticalGrid`.
- Modal sheets and the story dialog own their Android Back behavior. Back from a secondary tab returns Home before leaving the Activity.
- Sharing opens the real Android text share sheet; no fake remote post URL is generated and counters do not falsely increment when the chooser opens.

## Where to change product requirements

| Request | Main location |
| --- | --- |
| Change logo/name | `AppHeader.kt`, `strings.xml`, launcher drawables |
| Adjust visual tokens | `ui/theme/` |
| Change post layout | `PostCard.kt` |
| Add/remove a tab | `FeedTab` in `FloatingNavigation.kt` and `FrindleScaffold` in `FrindleApp.kt` |
| Change initial content | `DemoContent.kt` |
| Introduce real API/storage | Add a repository/data source; replace ViewModel fixture reconstruction |
| Add real video | Replace the preview in `PostCard.kt` / media sheet with lifecycle-aware Media3 playback |
| Add upload/auth/notifications | New integrations, after their behavior and permissions are specified |
| Persist user edits | Add Room/DataStore as appropriate; do not treat SavedStateHandle as a database |
| Translate/add RTL or dark theme | New resources, direction-aware layouts, explicit alternate theme and screenshots |

Avoid carrying the demo's drawable resource IDs into future network-domain models: a production media model should represent local and remote sources explicitly. Keep transport, authentication and persistence outside composables.

## Responsiveness changes (0.1.1)

`components/OfflinePhoto.kt` centralizes asynchronous local-image loading and shared cache sizing. `PostInteractions` is remembered by the app and carries IDs, while a post's header, caption and media receive narrowly scoped inputs. `LatestSnackbar` cancels superseded feedback, not actions. See [PERFORMANCE.md](PERFORMANCE.md) for the optional optimized build and honest on-device measurement guidance.
