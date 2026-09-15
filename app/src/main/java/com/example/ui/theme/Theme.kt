package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SaffronGoldLight,
    onPrimary = CharcoalDark,
    primaryContainer = SaffronGold,
    onPrimaryContainer = WarmWhite,
    secondary = KurdishCrimsonLight,
    onSecondary = WarmWhite,
    secondaryContainer = KurdishCrimson,
    onSecondaryContainer = WarmWhite,
    tertiary = MountainOlive,
    background = CharcoalDark,
    onBackground = WarmWhite,
    surface = CharcoalSurface,
    onSurface = WarmWhite,
    surfaceVariant = CharcoalCard,
    onSurfaceVariant = WarmSand,
    outline = CharcoalCardBorder,
    error = SafetyAlertRed,
    errorContainer = KurdishCrimson
)

private val LightColorScheme = lightColorScheme(
    primary = KurdishCrimson,
    onPrimary = WarmWhite,
    primaryContainer = SaffronGoldLight,
    onPrimaryContainer = CharcoalDark,
    secondary = SaffronGold,
    onSecondary = CharcoalDark,
    secondaryContainer = WarmSand,
    onSecondaryContainer = CharcoalDark,
    tertiary = MountainOlive,
    background = WarmWhite,
    onBackground = WarmTextPrimary,
    surface = WarmWhite,
    onSurface = WarmTextPrimary,
    surfaceVariant = WarmSand,
    onSurfaceVariant = WarmTextSecondary,
    outline = Color(0xFFDCD4C5),
    error = SafetyAlertRed,
    errorContainer = SafetyAlertContainer
)

@Composable
fun ChefMKTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.surface.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    // Enforce RTL for Persian language
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
