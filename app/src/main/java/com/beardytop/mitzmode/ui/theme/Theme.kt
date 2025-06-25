package com.beardytop.mitzmode.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = GoldAccent,
    tertiary = SilverAccent,
    background = GradientStart,
    surface = GradientEnd
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueLight,
    secondary = GoldAccent,
    tertiary = SilverAccent,
    background = DarkGradientStart,
    surface = DarkGradientEnd
)

@Composable
fun MitzModeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}