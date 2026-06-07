package com.company.carryon.ui.theme

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val CardDropShadowColor = Color(0x26000000)

fun Modifier.carryOnWhiteCard(
    shape: Shape,
    elevation: Dp = 8.dp
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = CardDropShadowColor,
        spotColor = CardDropShadowColor
    )
    .background(Color.White, shape)
