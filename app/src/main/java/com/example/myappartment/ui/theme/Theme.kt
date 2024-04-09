package com.example.myappartment.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.myappartment.ThemeState

private val DarkColorPalette = darkColors(
    primary = LightPink,
    primaryVariant = LightPink,
    secondary = Color.DarkGray,
    background = Color.Black,
    onSecondary = Color.White,
)

private val LightColorPalette = lightColors(
    primary = LightPink,
    primaryVariant = LightPink,
    secondary = Gray50,
    background = Color.White,
    onSecondary = Color.Black,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun MyAppartmentTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val isDark = ThemeState.darkModeState.value
    val colors = if (isDark) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}