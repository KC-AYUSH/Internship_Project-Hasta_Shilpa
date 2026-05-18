package com.example.hastashilpa.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BambooGreen = Color(0xFF4E6B3A)
val BambooGreenLight = Color(0xFF7A9C5E)
val WarmBrown = Color(0xFF8B5E3C)
val CreamWhite = Color(0xFFF5F0E8)
val LightSage = Color(0xFFE8EFE0)
val DarkBrown = Color(0xFF3E2A1A)
val MutedGold = Color(0xFFB8860B)

private val EarthyColorScheme = lightColorScheme(
    primary = BambooGreen,
    onPrimary = Color.White,
    primaryContainer = LightSage,
    onPrimaryContainer = DarkBrown,
    secondary = WarmBrown,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0E0D0),
    background = CreamWhite,
    surface = Color.White,
    onBackground = DarkBrown,
    onSurface = DarkBrown,
)

@Composable
fun HastaShilpaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EarthyColorScheme,
        content = content
    )
}