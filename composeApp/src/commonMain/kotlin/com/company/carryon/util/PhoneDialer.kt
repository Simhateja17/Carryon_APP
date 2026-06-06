package com.company.carryon.util

fun telUriFor(rawPhone: String?): String? {
    val raw = rawPhone?.trim().orEmpty()
    if (raw.isBlank()) return null

    val normalized = buildString {
        raw.forEachIndexed { index, char ->
            when {
                char.isDigit() -> append(char)
                char == '+' && index == 0 -> append(char)
            }
        }
    }
    val digitCount = normalized.count { it.isDigit() }
    if (digitCount < 7) return null
    return "tel:$normalized"
}

