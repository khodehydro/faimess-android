# Reference design · milestone 01

The user supplied a Frindle social-feed screenshot. This milestone reproduces its visual language in native Android Compose, rather than embedding the image in a WebView or drawing a fake phone around the app.

## Launch composition

1. White header: lime four-lobed brand mark, bold Frindle wordmark, quiet circular create and notification controls, red unread dot.
2. Horizontally scrolling stories: Me, Marcus, Abdullah, Sienna, Alex, Joanna. Green rings, self-story add badge, red demo LIVE badge.
3. Cool, almost-white feed canvas with generous rounded white cards.
4. Sienna post first: small avatar, name, time, public globe; two-line expandable travel caption; green mountain ridge thumbnail with translucent play affordance and small speaker icon; red heart, comments and share totals.
5. Esther Howard post next, visible below the first card.
6. Centered, translucent olive capsule navigation over the feed, with five circular controls and a lime Home selection. Icons remain unlabeled visually as in the reference, with accessible labels and selection semantics.

## Design tokens

| Token | Value |
| --- | --- |
| Lime accent | `#8EF20B` |
| Ink | `#202819` |
| Feed background | `#F5F6F7` |
| Muted text | `#93998F` |
| Leaf link | `#79B93F` |
| Heart / live | `#FF3D45` |
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

Change local fixtures in `data/DemoContent.kt`; change colors/typography in `ui/theme`; replace WebP assets under `res/drawable-nodpi`. See [ARCHITECTURE.md](ARCHITECTURE.md) for the component map.

Android Studio previews in `FrindleApp.kt` cover a 393 dp phone, 320 dp compact phone, and 1.5× text. CI captures the actual rendered Compose home screen into the `app-screenshots` artifact.
