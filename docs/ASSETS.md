# Asset provenance

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

The small monochrome vector icons and four-lobed logo are authored in project XML using basic geometry. They do not require an icon CDN or an extended icon dependency.

## Gradle Wrapper

The wrapper scripts and JAR are from Gradle's official `v8.11.1` GitHub tag, under Apache License 2.0:

https://github.com/gradle/gradle/tree/v8.11.1

See [licenses/Gradle-Apache-2.0.txt](licenses/Gradle-Apache-2.0.txt). Gradle and AndroidX dependencies retain their respective upstream licenses.
