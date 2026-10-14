package com.khodehydro.faimess.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ReferenceColors = lightColorScheme(
    primary = Lime,
    onPrimary = Ink,
    primaryContainer = SoftSurface,
    onPrimaryContainer = Ink,
    secondary = Leaf,
    onSecondary = Color.White,
    background = FeedBackground,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    onSurfaceVariant = MutedInk,
    surfaceVariant = SoftSurface,
    outline = Color(0xFFE4E9DF),
    error = HeartRed,
)

/** Intentionally light and non-dynamic: the first milestone matches the supplied reference. */
@Composable
fun FrindleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ReferenceColors,
        typography = FrindleTypography,
        content = content,
    )
}
