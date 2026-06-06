package com.company.carryon.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Screen-width buckets for responsive layouts.
 * Compact  : phones under 360 dp  (iPhone SE, Galaxy A-series)
 * Medium   : typical phones 360-600 dp
 * Expanded : tablets / foldables > 600 dp
 */
enum class WindowWidthClass { Compact, Medium, Expanded }

val LocalWindowWidthClass = compositionLocalOf { WindowWidthClass.Medium }

/** Call inside your root composable (CarryOnTheme or Scaffold). */
@Composable
fun rememberWindowWidthClass(): WindowWidthClass {
    val density = LocalDensity.current
    val widthDp = with(density) {
        // BoxWithConstraints is heavyweight; use screen metrics via density
        // screenWidthDp is available from LocalConfiguration on Android,
        // but for KMP we approximate using density.density * logical pixels.
        // In practice the theme wraps the whole app, so we use a safe default.
        // The actual width will be provided by the caller via BoxWithConstraints.
        0.dp // placeholder — overridden by ProvideWindowWidthClass
    }
    return when {
        widthDp < 360.dp -> WindowWidthClass.Compact
        widthDp < 600.dp -> WindowWidthClass.Medium
        else -> WindowWidthClass.Expanded
    }
}

// ─── Responsive scale factors ────────────────────────────────────

/**
 * Returns a scale factor (0.85 / 1.0 / 1.1) depending on screen width.
 * Use to multiply dp or sp values that must shrink on small screens.
 */
@Composable
fun responsiveScale(): Float = when (LocalWindowWidthClass.current) {
    WindowWidthClass.Compact -> 0.85f
    WindowWidthClass.Medium -> 1.0f
    WindowWidthClass.Expanded -> 1.1f
}

/** Scales a Dp value by the current window-width class. */
@Composable
fun Dp.responsive(): Dp = this * responsiveScale()

/** Scales a TextUnit (sp) value by the current window-width class. */
@Composable
fun TextUnit.responsive(): TextUnit = (this.value * responsiveScale()).sp
