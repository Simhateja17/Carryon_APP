package com.company.carryon.ui.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class ResponsiveTest {
    @Test
    fun widthClassesCoverSmallAndLargePhones() {
        assertEquals(WindowWidthClass.Compact, windowWidthClass(320.dp))
        assertEquals(WindowWidthClass.Medium, windowWidthClass(360.dp))
        assertEquals(WindowWidthClass.Medium, windowWidthClass(599.dp))
        assertEquals(WindowWidthClass.Expanded, windowWidthClass(600.dp))
    }

    @Test
    fun heightClassesCoverShortLandscapeAndTallPhones() {
        assertEquals(WindowHeightClass.Compact, windowHeightClass(360.dp))
        assertEquals(WindowHeightClass.Medium, windowHeightClass(480.dp))
        assertEquals(WindowHeightClass.Expanded, windowHeightClass(720.dp))
    }
}
