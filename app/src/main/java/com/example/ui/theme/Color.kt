package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark Premium Theme Colors for AirPods Control
val BgDarkNavy = Color(0xFF070B14)
val SurfaceDarkNavy = Color(0xFF0E1726)
val CardDarkNavy = Color(0xFF131F33)
val CardBorderNavy = Color(0xFF1C2D47)
val CardLightBorder = Color(0xFF283B59)

val CyanAccent = Color(0xFF00E5FF)
val CyanGlow = Color(0xFF38E1FF)
val BlueElectric = Color(0xFF2979FF)
val PurpleNeon = Color(0xFF8B5CF6)
val PurpleAccent = Color(0xFF7C4DFF)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val SuccessGreen = Color(0xFF00E676)
val WarningAmber = Color(0xFFFFB300)
val DangerRed = Color(0xFFFF5252)

val BluePurpleGradient = Brush.horizontalGradient(
    listOf(BlueElectric, PurpleAccent)
)

val CyanBlueGradient = Brush.horizontalGradient(
    listOf(CyanAccent, BlueElectric)
)

val WaveGradient = Brush.horizontalGradient(
    listOf(Color(0xFF8B5CF6), Color(0xFF38BDF8), Color(0xFF00E5FF))
)
