# Asset provenance

## Brand mark and launcher icon

`app/src/main/res/drawable-nodpi/brand_logo.png` is a byte-identical copy (SHA-256 `0e93ccce…6c644b`) of `src/assets/brand/faimess-logo.png` from the FAIMESS web repository `khodehydro/faimess-doc` (branch `arena/01a10c49-faimess-doc`, commit `73ae3448`): the white cat on the brand purple `#8267F0`, 150 × 150, flat two-colour artwork. It is the team's own brand asset, shared by the website, its PWA icons and this app.

`app/src/main/res/mipmap-*/ic_launcher_foreground.png` (108 dp at mdpi … xxxhdpi) are not separate drawings: `tools/brand_assets.py` decodes the logo into a coverage field and re-renders the cat alone, with alpha edges, inside the adaptive-icon safe zone. The adaptive icon's background layer and the splash window are the flat `@color/brand_primary`. Re-run the script whenever the logo file changes.

## Album and playlist artwork (milestone 03)

`album_*.webp` (10 files) and `playlist_*.webp` (6 files) under `app/src/main/res/drawable-nodpi/` are byte-identical copies of the FAIMESS web app's demo artwork in `src/assets/photos/albums/` and `src/assets/photos/playlists/` (`khodehydro/faimess-doc`, commit `c575e7c`), already cropped to 320–420 px squares and colour-graded there. They are referenced by `Track.cover` / `Playlist.cover` in `DemoContent.kt`.

**Licence warning, inherited from the site's `docs/photos.md`:** the web project documents these covers as *temporary mock-up placeholders* cropped from web image-search results (Pinterest, wallpaper sites, Dreamstime previews, …), not licensed stock. They are fine for this offline demo, but — exactly like the website — they must be replaced with licensed or commissioned artwork before any public or commercial release. Replace the files under the same names; no code changes are needed. The artist and album names are fictional.

## Photography

`app/src/main/res/drawable-nodpi/` contains two original AI-generated scenic photographs and six original AI-generated fictional profile portraits created for this UI milestone on 2026-10-14:

- `mountain_ridge.webp`: lush green ridge, narrow stone trail, mist; used to approximate the reference's travel thumbnail.
- `forest_lake.webp`: green mountain lake and small boat.
- `avatar_me.webp`, `avatar_marcus.webp`, `avatar_abdullah.webp`, `avatar_sienna.webp`, `avatar_alex.webp`, `avatar_jo.webp`: fictional people, not verified accounts or representations of named real people.

The scene images are resized to at most 1200 × 900, the avatars to 192 × 192, and all are WebP. The optimized image set totals about 550 KiB. Full-size generation sources are excluded with `design-assets/` in `.gitignore`.

The user's reference is the layout direction, not the source of these photograph files. No claim is made that the generated photographs exactly reproduce the original photos.

## Font

Manrope, Copyright 2020 The Manrope Project Authors, distributed under the SIL Open Font License 1.1.

- Upstream: https://github.com/google/fonts/tree/main/ofl/manrope
- Bundled license: [licenses/Manrope-OFL.txt](licenses/Manrope-OFL.txt)
- The upstream variable font was instantiated at weights 400, 500, 600, 700 and 800 for deterministic Compose loading on API 26+; the shapes are unchanged.

## Icons

Two sources, both plain `VectorDrawable` XML with no icon library dependency:

- **Material Icons (Google), Apache License 2.0** — the navigation pairs `ic_home_outline/filled`, `ic_explore_outline/filled`, `ic_calendar_outline/filled`, `ic_person_outline/filled`, plus `ic_search`, `ic_play_circle`, `ic_queue_music` and `ic_shuffle`. Each file is the 24 px path from `google/material-design-icons` (`src/<category>/<name>/materialicons[outlined]/24px.svg`) converted to Android path data, with the licence noted in a comment at the top of the file. The site itself draws stroke-only "Lets Icons" glyphs; Android uses Material's filled/outlined pairs because the request was a filled icon for the selected tab and an outlined one otherwise.
- **Project-authored** — the remaining small monochrome icons (heart, comment, send, bookmark, bell, globe, more, close, check, plus, arrows, play) are drawn in project XML using basic geometry. The first milestone's four-lobed placeholder logo and its hand-drawn navigation icons were removed.

## Gradle Wrapper

The wrapper scripts and JAR are from Gradle's official `v8.11.1` GitHub tag, under Apache License 2.0:

https://github.com/gradle/gradle/tree/v8.11.1

See [licenses/Gradle-Apache-2.0.txt](licenses/Gradle-Apache-2.0.txt). Gradle and AndroidX dependencies retain their respective upstream licenses.
