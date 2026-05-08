package com.company.carryon.i18n

import androidx.compose.runtime.staticCompositionLocalOf

object EnStrings : AppStrings

val LocalStrings = staticCompositionLocalOf<AppStrings> { EnStrings }

fun getStringsForLanguage(code: String): AppStrings = when (SupportedLanguages.normalize(code)) {
    "ms" -> MsStrings
    "ta" -> TaStrings
    "zh" -> ZhStrings
    else -> EnStrings
}
