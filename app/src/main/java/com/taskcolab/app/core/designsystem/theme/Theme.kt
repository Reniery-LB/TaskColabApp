package com.taskcolab.app.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TaskColabBlue,
    onPrimary = TaskColabWhite,
    primaryContainer = TaskColabDesaturatedBlue,
    secondary = TaskColabBlueSecondary,
    onSecondary = TaskColabWhite,
    tertiary = TaskColabActionBlue,
    error = TaskColabDanger,
    onError = TaskColabWhite,
    background = TaskColabBackground,
    onBackground = TaskColabInk,
    surface = TaskColabWhite,
    onSurface = TaskColabInk,
    surfaceVariant = TaskColabLine,
    onSurfaceVariant = TaskColabMuted,
    outline = TaskColabLine
)

private val DarkColorScheme = darkColorScheme(
    primary = TaskColabLightBlue,
    onPrimary = TaskColabBlack,
    secondary = TaskColabBlueSecondary,
    onSecondary = TaskColabWhite,
    error = TaskColabDanger,
    background = Color(0xFF101626),
    onBackground = TaskColabWhite,
    surface = Color(0xFF16213E),
    onSurface = TaskColabWhite
)

@Composable
fun TaskColabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = TaskColabTypography,
        content = content
    )
}
