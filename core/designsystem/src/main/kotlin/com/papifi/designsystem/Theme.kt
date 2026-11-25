package com.papifi.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PapifiPrimary,
    primaryContainer = PapifiPrimaryContainer,
    onPrimary = PapifiOnPrimary,
    onPrimaryContainer = PapifiOnPrimaryContainer,
    secondary = PapifiSecondary,
    secondaryContainer = PapifiSecondaryContainer,
    background = PapifiBackground,
    onBackground = PapifiOnBackground,
    surface = PapifiSurface,
    onSurface = PapifiOnSurface,
    error = PapifiError,
    onError = PapifiOnError
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8CD8B5),
    primaryContainer = Color(0xFF003822),
    onPrimary = Color(0xFF003921),
    onPrimaryContainer = Color(0xFFC4F5D0),
    secondary = Color(0xFFB3CCBD),
    secondaryContainer = Color(0xFF354B3F),
    background = Color(0xFF111412),
    onBackground = Color(0xFFE2E3DF),
    surface = Color(0xFF111412),
    onSurface = Color(0xFFE2E3DF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun PapifiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
