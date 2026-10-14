package com.khodehydro.faimess.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FaimessColors = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimarySoft,
    onPrimaryContainer = PrimaryDeep,
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = TealSoft,
    onSecondaryContainer = TealDeep,
    tertiary = Flame,
    onTertiary = Color.White,
    tertiaryContainer = FlameSoft,
    onTertiaryContainer = FlameDeep,
    background = CanvasBackground,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Subtle,
    onSurfaceVariant = InkMuted,
    outline = LineStrong,
    outlineVariant = Line,
    error = FlameDeep,
    onError = Color.White,
    errorContainer = FlameSoft,
    onErrorContainer = FlameDeep,
)

/**
 * The website's light theme, not dynamic colour: the palette is the brand, so it must not change
 * per device. The site also ships a dark palette; it is not wired here yet.
 */
@Composable
fun FaimessTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FaimessColors,
        typography = FaimessTypography,
        content = content,
    )
}
