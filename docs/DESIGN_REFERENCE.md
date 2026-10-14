# Reference design

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
4. Sienna post first: small avatar, name, time, public globe; two-line expandable travel caption; green mountain ridge thumbnail with translucent play affordance and small speaker icon; red heart, comments and share totals.
5. Esther Howard post next, visible below the first card.
6. Centered, translucent capsule navigation over the feed, with five circular controls and a filled Home selection. (Now a white glass capsule with the selection in brand purple, like the site's bottom menu.) Icons remain unlabeled visually as in the reference, with accessible labels and selection semantics.

## Layout tokens

| Token | Value |
| --- | --- |
| Typeface | Manrope, bundled weights 400–800 |
| Header | 70 dp |
| Feed gutters | 16 dp |
| Post corner radius | 27 dp |
| Photo corner radius | 21 dp |
| Media aspect ratio | 1.4 : 1 |
| Story ring / photo | 57 / 50 dp |
| Bottom capsule | Up to 284 dp; 48 dp interactive circles |
| Content column | Centered, at most 600 dp on wide displays |

## Intentional differences and limits

- The screenshot's iPhone bezel, notch, time and OS icons are not app content. Android supplies its own status/navigation bars, and the app respects those insets.
- The original photo files and original design measurements were not available. Similar original AI-generated photos and approximate proportions are used; this is not a claim of pixel-identical source imagery.
- The first card is a **still-image video preview**, not a real player. Opening it explains this. LIVE is also explicitly identified as a demo in the story viewer.
- The floating bar uses a translucent gradient, not an expensive real-time blur; this is consistent from API 26 upward.
- Secondary tab screens are intentionally lightweight demo destinations, not final product requirements. The calendar is an honest empty state.
- The launch appearance stays light and English even when the system uses dark mode. Dynamic color and RTL have not been introduced because they would change the reference layout.
- Interactive hit areas are enlarged where possible while preserving the compact visual controls. Content reflows and scrolls at larger font scales; the caption truncation is measured rather than hard-coded to a character count.

## Replacing visuals later

Change local fixtures in `data/DemoContent.kt`; change colors/typography in `ui/theme`; replace WebP assets under `res/drawable-nodpi`; a new logo goes to `res/drawable-nodpi/brand_logo.png` followed by `python3 tools/brand_assets.py`. See [ARCHITECTURE.md](ARCHITECTURE.md) for the component map.

Android Studio previews in `FaimessApp.kt` cover a 393 dp phone, 320 dp compact phone, and 1.5× text. CI captures the actual rendered Compose home screen into the `app-screenshots` artifact.
