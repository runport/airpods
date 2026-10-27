package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AirPodsColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Color.Black,
    primaryContainer = CardDarkNavy,
    onPrimaryContainer = CyanAccent,
    secondary = BlueElectric,
    onSecondary = Color.White,
    secondaryContainer = CardBorderNavy,
    onSecondaryContainer = Color.White,
    tertiary = PurpleNeon,
    onTertiary = Color.White,
    background = BgDarkNavy,
    onBackground = TextPrimary,
    surface = SurfaceDarkNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardDarkNavy,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderNavy
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AirPodsColorScheme,
        typography = Typography,
        content = content
    )
}
