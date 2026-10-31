package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val TkDarkColorScheme = darkColorScheme(
    primary = TkGold,
    onPrimary = TkGreenDark,
    primaryContainer = TkGreenPrimary,
    onPrimaryContainer = TkGoldLight,
    secondary = TkGreenMedium,
    onSecondary = TkSurfaceLight,
    secondaryContainer = TkGreenDark,
    onSecondaryContainer = TkTextDarkSecondary,
    tertiary = TkGoldLight,
    background = TkBackgroundDark,
    onBackground = TkTextDarkPrimary,
    surface = TkSurfaceDark,
    onSurface = TkTextDarkPrimary,
    surfaceVariant = TkSurfaceDark,
    onSurfaceVariant = TkTextDarkSecondary,
    outline = TkBorderDark,
    error = TkError
)

private val TkLightColorScheme = lightColorScheme(
    primary = TkGreenPrimary,
    onPrimary = TkSurfaceLight,
    primaryContainer = TkGreenLight,
    onPrimaryContainer = TkGreenDark,
    secondary = TkGoldDark,
    onSecondary = TkSurfaceLight,
    secondaryContainer = TkGoldLight,
    onSecondaryContainer = TkGreenDark,
    tertiary = TkGreenMedium,
    background = TkBackgroundLight,
    onBackground = TkTextPrimary,
    surface = TkSurfaceLight,
    onSurface = TkTextPrimary,
    surfaceVariant = TkGreenLight,
    onSurfaceVariant = TkTextSecondary,
    outline = TkBorder,
    error = TkError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep TK VEL signature emerald & gold branding consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> TkDarkColorScheme
        else -> TkLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
