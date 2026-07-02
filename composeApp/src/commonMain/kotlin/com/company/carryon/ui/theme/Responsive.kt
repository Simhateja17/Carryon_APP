package com.company.carryon.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Width classes are based on the available app window, not the physical device. */
enum class WindowWidthClass { Compact, Medium, Expanded }

enum class WindowHeightClass { Compact, Medium, Expanded }

@Immutable
data class WindowAdaptiveInfo(
    val width: Dp,
    val height: Dp,
    val widthClass: WindowWidthClass,
    val heightClass: WindowHeightClass,
) {
    val isLandscape: Boolean get() = width > height
    val horizontalPadding: Dp
        get() = when {
            width < 360.dp -> 12.dp
            width < 600.dp -> 16.dp
            else -> 24.dp
        }
}

fun windowWidthClass(width: Dp): WindowWidthClass = when {
    width < 360.dp -> WindowWidthClass.Compact
    width < 600.dp -> WindowWidthClass.Medium
    else -> WindowWidthClass.Expanded
}

fun windowHeightClass(height: Dp): WindowHeightClass = when {
    height < 480.dp -> WindowHeightClass.Compact
    height < 720.dp -> WindowHeightClass.Medium
    else -> WindowHeightClass.Expanded
}

val LocalWindowAdaptiveInfo = compositionLocalOf {
    WindowAdaptiveInfo(
        width = 360.dp,
        height = 640.dp,
        widthClass = WindowWidthClass.Medium,
        heightClass = WindowHeightClass.Medium,
    )
}

val LocalWindowWidthClass = compositionLocalOf { WindowWidthClass.Medium }

/**
 * Keeps phone UI readable in wide landscape/foldable windows. Background still fills the window,
 * while screen content is centered and capped at a comfortable single-pane width.
 */
@Composable
fun ResponsiveContentHost(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Box(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize(),
            content = content,
        )
    }
}

/** Only decorative dimensions scale. Interactive targets must remain at least 48 dp. */
@Composable
fun responsiveScale(): Float = when (LocalWindowWidthClass.current) {
    WindowWidthClass.Compact -> 0.9f
    WindowWidthClass.Medium -> 1f
    WindowWidthClass.Expanded -> 1.1f
}

@Composable
fun Dp.responsive(): Dp = this * responsiveScale()

@Composable
fun TextUnit.responsive(): TextUnit = (value * responsiveScale()).sp
