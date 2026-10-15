# Reference design

## Player (milestone 04) — taken from the FAIMESS web app

Read from `khodehydro/faimess-doc` (branch `arena/01a10c49-faimess-doc`, commit `c575e7c`): `src/sections/MiniPlayer.tsx`, `src/sections/PlayerSection.tsx`, `src/app/PlayerContext.tsx`, `src/app/CompactShell.tsx`, `src/data/player.ts`, `src/data/lyrics.ts`, `docs/audio.md`.

- **Audio:** the site plays one synthesized master, `src/assets/audio/faimess-demo.mp3` (2:40), for every song in its catalogue; position and duration come from the element and `ended` advances the queue. Android bundles the same file as `res/raw/faimess_demo.mp3` and plays it with `MediaPlayer` (`PlayerViewModel`). Track rows still print the catalogue length; the seek bar shows the real file's clock, as on the site.
- **Bottom stack (`CompactShell.tsx`):** `MiniPlayer` sits in a column directly above `MobileNav`, both pinned to the bottom with a small gap. Android keeps the same column: the mini bar (62 dp, the capsule's height) 8 dp above the menu, inside the same 16 dp gutters; it slides up when the first song starts and the scrolling screens add its height to their bottom padding (`LocalBottomChromeHeight`).
- **Mini player (`MiniPlayer.tsx`):** `rounded-[22px] bg-primary ring-1 ring-white/20` with a purple shadow; 42 px cover, 13 px bold white title, 12 px `artist · album` at 75 % white, a white 36 px play/pause circle with the glyph in primary-deep, a next button at 80 % white, and the whole bar opens the full player; no progress strip. Android: the same capsule in `Primary` with a 1 dp white/20 border and `PrimaryDeep` shadow; the cover is **circular** (the request), title `titleSmall`, subtitle `bodySmall` semibold at 75 % white, white 36 dp toggle (`PrimaryDeep` glyph), skip-next at 85 % white.
- **Full player (`PlayerSection.tsx`):** header with a plays pill (`▶ 2.4M plays`, `compactNumber`) and a queue pill (`n/m`), the collapse button, a 118 px cover that scales to 0.97 / 0.86 opacity with a pause overlay while paused, 18 px bold title over `artist · album`, a 6 px scrubbable seek bar with a 13 px knob and `m:ss` / `-m:ss` clocks, transport `like · add | prev · 44 px primary play · next | share · download`, a secondary row with the repeat pill (off → all → one), the bilingual lyrics panel (original line + Persian translation, active line by `activeLineIndex`) and queue / liked drawers. Android (`PlayerScreen`): the same header pills, a larger cover (up to 280 dp, 24 dp radius) with the same dim + pause glyph, `headlineSmall` title, Material 3 slider restyled to a 6 dp track and 14 dp knob, `like · prev · 64 dp primary play · next · share`, shuffle and repeat pills, then **Lyrics** / **Up next** tabs: lyrics as `ko` over `fa` (RTL), active line in ink with the translation in primary-deep and the rest at 55 %, scrolled into view and tappable to seek; the queue as the shared track rows with the playing song tinted (`border-primary/35 bg-primary-faint/60`, a play glyph instead of its number). Add-to-playlist, download, anthem, lyric story and the comments bar are not built — no dead buttons.
- **Queue rules (`PlayerContext.tsx`):** `play(track, source)` makes the tapped list the queue; next wraps manually; a finished song stops at the end with repeat off, restarts the queue with repeat all, replays with repeat one; the liked set starts as `nt1, tr3, tr5`. Android adds shuffle (Fisher–Yates with the playing song kept first, like the detail page's Shuffle button) and "previous restarts after 3 s".
- **Data:** `plays` per track and the lyric sheets are the site's (`PLAYS` in `player.ts`, `LYRICS` in `lyrics.ts`); the lyrics are original demo lines written for FAIMESS's fictional roster, not transcriptions. `Afterimage` (nt7) has no sheet, so its panel shows the "no lyrics yet" state.

## Playlist feed and navigation (milestone 03) — taken from the FAIMESS web app

The home feed now shows **playlists made by listeners** instead of photos, and the bottom menu follows the site's navigation rules. Everything below was read from `khodehydro/faimess-doc` (branch `arena/01a10c49-faimess-doc`, commit `c575e7c`):

- **Post card:** the header, expandable caption and like / comment / share row are unchanged. The 1.4 : 1 photo was replaced by a *playlist panel* on the same `Subtle` background with the same 21 dp radius: a 56 dp cover (12 dp radius), the playlist name, the site's facts line `N tracks · M min · Mood` (`BrowseDetailView.tsx`: `minutesOf` rounds the total and never prints less than one minute), a primary-purple play-circle glyph, the first four songs as track rows, and `+n more tracks` in primary-deep. Tapping the panel opens the playlist page.
- **Track row:** the site's detail-page row (`rounded-[14px] border line/80 bg-surface px-2.5 py-2 gap-2.5`): index in faint tabular label text, square artwork (36 dp in the card, 44 dp on the page, 10/12 dp radius), bold title, `artist · album` in semibold muted text, and the `m:ss` clock at the end.
- **Playlist page (`PlaylistScreen`, 0.3.1):** built like the home screen. The header is the home header's twin — same 70 dp white bar, with the playlist's **circular 34 dp cover** where the brand mark is, the **playlist name** where the wordmark is (same extra-bold, −0.02 em style; long names are measured and shrunk from 22 to 15 sp before ellipsizing), a **share** circle where create is and a **primary-purple play** circle where the bell is, plus a small back arrow at the start. Below it: an *about* card in the feed card's clothes (author row with options menu, full caption, `tracks / length / mood` chips, `Play` and `Shuffle` pills, and the feed's own like / comment / share row — one like shared with the card), `TRACKS` with count and total length followed by every song as a track row, `ARTISTS` (lettered discs, songs per artist), `MORE PLAYLISTS` (other feed playlists; tapping swaps the page) and `COMMENTS` (local comments + a button into the comments sheet). The floating menu stays visible above the page; choosing a tab, the back arrow or the system Back gesture leaves it.
- **Bottom menu:** the site's `MobileNav` keeps the active item in primary with the glyph "filled" by a heavier stroke. Android uses Material Icons pairs instead — `*_outline` for idle tabs and `*_filled` for the selected one — inside the same translucent white capsule. The capsule holds Home / Discover / Calendar / Profile (4 × 48 dp + 7 dp padding, at most 236 dp wide); **Search is a separate 62 dp circle** with the same height, shadow and hairline, 10 dp to the end of the capsule. On a 320 dp phone the capsule shrinks to 206 dp so the pair still fits the 16 dp gutters.
- **Demo catalogue:** tracks, artists, albums, durations and artwork are the site's own demo data (`src/data/feed.ts`, `src/data/player.ts`, `src/assets/photos/{albums,playlists}`); playlist names, moods and covers follow `src/data/library.ts` / `server/db.ts`. Model fields mirror the backend rows (`tracks`: id, title, artist, album, seconds; `playlists`: name, curator, mood, photo, track ids).
- **Not changed on purpose:** stories, people, the composer flow, the Manrope typeface and the set of tabs. (Audio playback was wired in milestone 04, above.)

## Brand (milestone 02) — taken from the FAIMESS web app

The name, logo and colours come from the FAIMESS website repository (`khodehydro/faimess-doc`, branch `arena/01a10c49-faimess-doc`), not from the first milestone's screenshot:

- **Name:** `FAIMESS` — `app_name`, the launcher label, the header wordmark (extra-bold, −0.02 em tracking, as in `src/ui/Logo.tsx` on the site) and the share text.
- **Logo:** the site's single brand raster, `src/assets/brand/faimess-logo.png` (white cat on brand purple, 150 × 150), copied byte-for-byte to `res/drawable-nodpi/brand_logo.png`. The header clips it to the same 32.5 % corner the site uses. The adaptive launcher icon and the splash icon are cut from that file by `tools/brand_assets.py` (foreground = the cat with soft alpha edges, background = flat `#8267F0`), the same approach as `tools/appicons.py` on the web side.
- **Colours:** `ui/theme/Color.kt` mirrors the light `@theme` tokens of `src/index.css` / `docs/design-tokens.json` one-to-one (see the table below). The splash window is the brand purple with the cat centred, like the site's mobile splash sheet. The site's dark palette exists but is not wired into the Android theme yet.
- **Unchanged on purpose:** layout, copy, demo content and the Manrope typeface. The site uses Vazirmatn (Latin + Persian) and Pretendard (Hangul); switching fonts is a separate step.

| Token | Value | Used for |
| --- | --- | --- |
| Primary | `#8267F0` | Selected tab, unread dot, buttons, logo field, splash |
| Primary deep | `#6B4FDD` | Link-style text (More… / Less), add-story badge, labels |
| Primary soft / faint | `#ECE7FE` / `#F6F4FF` | Material primary container |
| Teal / deep / soft | `#79BFB3` / `#4E8E86` / `#E4F2EF` | Secondary; end of the story ring gradient |
| Mint / soft | `#58B879` / `#E7F5EC` | Success (reserved) |
| Flame / deep / soft | `#F2743D` / `#D95F2B` / `#FDECE2` | Tertiary, inline errors, LIVE ring |
| Ink / body / muted / faint | `#22252E` / `#3C4049` / `#8A909A` / `#B9BEC7` | Text |
| Canvas | `#EAEAEC` | Feed background behind the white cards |
| Shell / subtle | `#F1F2F5` / `#F7F8FA` | Icon trays, chips, image placeholders |
| Line / strong | `#EDEEF1` / `#E4E6EA` | Hairlines, outline |
| Heart | `#F43F5E` | Likes and the LIVE badge (the site's rose-500 favourites) |

## Layout reference · milestone 01

The user supplied a Frindle social-feed screenshot. The first milestone reproduced its visual language in native Android Compose, rather than embedding the image in a WebView or drawing a fake phone around the app. The structure below is still the one on screen; the lime/green values it mentions were replaced by the FAIMESS palette above in milestone 02.

## Launch composition

1. White header: brand mark, bold wordmark, quiet circular create and notification controls, unread dot. (Now the FAIMESS cat logo, `FAIMESS`, and a primary-purple dot.)
2. Horizontally scrolling stories: Me, Marcus, Abdullah, Sienna, Alex, Joanna. Gradient rings (now primary-deep → primary → teal, the site's unseen-story ring), self-story add badge, demo LIVE badge.
3. Cool, almost-white feed canvas with generous rounded white cards.
4. Sienna post first: small avatar, name, time, public globe; two-line expandable caption; the media area (a landscape thumbnail with a play affordance in milestone 01, a playlist panel with four track rows since milestone 03); red heart, comments and share totals.
5. Esther Howard post next, visible below the first card.
6. Centered, translucent capsule navigation over the feed, with circular controls and a filled Home selection. (Now a white glass capsule of four tabs with the selection in brand purple, plus a separate search circle, like the site's bottom menu.) Icons remain unlabeled visually as in the reference, with accessible labels and selection semantics.

## Layout tokens

| Token | Value |
| --- | --- |
| Typeface | Manrope, bundled weights 400–800 |
| Header | 70 dp |
| Feed gutters | 16 dp |
| Post corner radius | 27 dp |
| Playlist panel radius | 21 dp (track rows 14 dp, thumbs 10–12 dp) |
| Panel cover / page cover | 56 dp / 76 dp |
| Story ring / photo | 57 / 50 dp |
| Bottom capsule | Up to 236 dp + 10 dp gap + 62 dp search circle; 48 dp interactive circles |
| Content column | Centered, at most 600 dp on wide displays |

## Intentional differences and limits

- The screenshot's iPhone bezel, notch, time and OS icons are not app content. Android supplies its own status/navigation bars, and the app respects those insets.
- The original photo files and original design measurements were not available. Similar original AI-generated photos and approximate proportions are used; this is not a claim of pixel-identical source imagery.
- Nothing plays audio yet: Play, Shuffle and track taps explain this in a snackbar instead of pretending. LIVE is also explicitly identified as a demo in the story viewer.
- The floating bar uses a translucent gradient, not an expensive real-time blur; this is consistent from API 26 upward.
- Secondary tab screens are intentionally lightweight demo destinations, not final product requirements. The calendar is an honest empty state.
- The launch appearance stays light and English even when the system uses dark mode. Dynamic color and RTL have not been introduced because they would change the reference layout.
- Interactive hit areas are enlarged where possible while preserving the compact visual controls. Content reflows and scrolls at larger font scales; the caption truncation is measured rather than hard-coded to a character count.

## Replacing visuals later

Change local fixtures (tracks, playlists, posts) in `data/DemoContent.kt`; change colors/typography in `ui/theme`; replace WebP assets under `res/drawable-nodpi` (`album_*`, `playlist_*`, avatars, story scenes); a new logo goes to `res/drawable-nodpi/brand_logo.png` followed by `python3 tools/brand_assets.py`. See [ARCHITECTURE.md](ARCHITECTURE.md) for the component map.

Android Studio previews in `FaimessApp.kt` cover a 393 dp phone, 320 dp compact phone, and 1.5× text. CI captures the actual rendered Compose home screen into the `app-screenshots` artifact.
