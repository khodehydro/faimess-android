package com.khodehydro.faimess.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * FAIMESS palette — the light theme tokens of the web app, copied one-to-one from its
 * `@theme` block (src/index.css) and docs/design-tokens.json. The names follow the site so a
 * colour can be looked up in either codebase; `Primary` is also the field colour of the logo.
 */

// Brand purple: active tab, primary buttons, badges, the unread dot.
val Primary = Color(0xFF8267F0)
val PrimaryDeep = Color(0xFF6B4FDD)
val PrimarySoft = Color(0xFFECE7FE)
val PrimaryFaint = Color(0xFFF6F4FF)

// Secondary accents.
val Teal = Color(0xFF79BFB3)
val TealDeep = Color(0xFF4E8E86)
val TealSoft = Color(0xFFE4F2EF)
val Mint = Color(0xFF58B879)
val MintSoft = Color(0xFFE7F5EC)
val Flame = Color(0xFFF2743D)
val FlameDeep = Color(0xFFD95F2B)
val FlameSoft = Color(0xFFFDECE2)

// Text.
val Ink = Color(0xFF22252E)
val InkBody = Color(0xFF3C4049)
val InkMuted = Color(0xFF8A909A)
val InkFaint = Color(0xFFB9BEC7)

// Surfaces and hairlines. Cards stay pure white (`Color.White`), as on the site.
val CanvasBackground = Color(0xFFEAEAEC)
val Shell = Color(0xFFF1F2F5)
val Subtle = Color(0xFFF7F8FA)
val Line = Color(0xFFEDEEF1)
val LineStrong = Color(0xFFE4E6EA)

// Hearts and the LIVE badge use the same rose the site's favourites use (Tailwind rose-500).
val Heart = Color(0xFFF43F5E)
