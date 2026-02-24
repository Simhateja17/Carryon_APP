package com.example.carryon.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.carryon.i18n.LocalStrings
import com.example.carryon.i18n.getStringsForLanguage

// Primary Blue Theme (matching Carry On design)
val PrimaryBlue = Color(0xFF1E88E5)
val PrimaryBlueLight = Color(0xFF42A5F5)
val PrimaryBlueDark = Color(0xFF1565C0)
val PrimaryBlueSurface = Color(0xFFE3F2FD)

// Secondary Colors
val SecondaryBlue = Color(0xFF2196F3)
val SecondaryBlueLight = Color(0xFF64B5F6)
val SecondaryBlueDark = Color(0xFF1976D2)

// Accent Colors
val AccentYellow = Color(0xFFFFD700)
val AccentOrange = Color(0xFFFF9800)

// Background Colors
val BackgroundLight = Color(0xFFF8F9FA)
val BackgroundDark = Color(0xFF121212)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1E1E1E)

// Text Colors
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)
val TextOnPrimary = Color(0xFFFFFFFF)
val TextBlue = Color(0xFF1E88E5)

// Status Colors
val SuccessGreen = Color(0xFF4CAF50)
val WarningYellow = Color(0xFFFFC107)
val ErrorRed = Color(0xFFF44336)
val InfoBlue = Color(0xFF2196F3)

// Card Colors
val CardBackground = Color(0xFFFFFFFF)
val CardBorder = Color(0xFFE0E0E0)

// Rating Colors
val StarYellow = Color(0xFFFFB800)

// Legacy alias for compatibility
val PrimaryOrange = PrimaryBlue

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = TextPrimary,
    secondary = SecondaryBlue,
    onSecondary = TextOnPrimary,
    secondaryContainer = SecondaryBlueLight,
    onSecondaryContainer = TextPrimary,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextOnPrimary,
    outline = CardBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = TextOnPrimary,
    secondary = SecondaryBlue,
    onSecondary = TextOnPrimary,
    secondaryContainer = SecondaryBlueDark,
    onSecondaryContainer = TextOnPrimary,
    background = BackgroundDark,
    onBackground = TextOnPrimary,
    surface = SurfaceDark,
    onSurface = TextOnPrimary,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextOnPrimary,
    outline = CardBorder
)

@Composable
fun CarryOnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    language: String = "en",
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalStrings provides getStringsForLanguage(language)) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography(),
            content = content
        )
    }
}
