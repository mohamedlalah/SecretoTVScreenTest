package com.secreto.tvscreentest.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

@Composable
fun SecretoTVScreenTestTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFF65DEFF), secondary = Color(0xFFA797FF),
        background = Color(0xFF080F20), surface = Color(0xFF17243C),
        onBackground = Color.White, onSurface = Color.White
    ), typography = Typography, content = content)
}
