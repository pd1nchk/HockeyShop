package com.podolyanchik.hockeyshop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = White,
    primaryContainer = LightBlue,
    onPrimaryContainer = DarkBlue,
    secondary = Beige,
    onSecondary = Black,
    secondaryContainer = LightBeige,
    onSecondaryContainer = DarkGray,
    tertiary = Gray,
    onTertiary = White,
    tertiaryContainer = LightGray,
    onTertiaryContainer = DarkGray,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    surfaceVariant = LightBeige,
    onSurfaceVariant = DarkGray,
    error = Color(0xFFB00020),
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    onPrimary = DarkBackground,
    primaryContainer = DarkBlue,
    onPrimaryContainer = LightBlue,
    secondary = DarkBeige,
    onSecondary = Black,
    secondaryContainer = DarkGray,
    onSecondaryContainer = Beige,
    tertiary = LightGray,
    onTertiary = DarkBackground,
    tertiaryContainer = Gray,
    onTertiaryContainer = LightGray,
    background = DarkBackground,
    onBackground = White,
    surface = DarkSurface,
    onSurface = White,
    surfaceVariant = DarkGray,
    onSurfaceVariant = LightGray,
    error = Color(0xFFCF6679),
    onError = Black
)

@Composable
fun HockeyShopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 