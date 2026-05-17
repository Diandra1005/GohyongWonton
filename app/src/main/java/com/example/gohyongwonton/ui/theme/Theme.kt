package com.example.gohyongwonton.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary          = Color(0xFFD04F2A),   // oranye merah — warna makanan
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFFFDDD3),
    secondary        = Color(0xFF8B5E3C),   // coklat kayu
    tertiary         = Color(0xFFE8A020),   // kuning keemasan
    background       = Color(0xFFFFFBF8),
    surface          = Color.White
)

private val DarkColors = darkColorScheme(
    primary          = Color(0xFFFFB59D),
    primaryContainer = Color(0xFF7A2D12),
    secondary        = Color(0xFFD4A87A),
    tertiary         = Color(0xFFFFD180)
)

@Composable
fun GohyongWontonTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content     = content
    )
}
