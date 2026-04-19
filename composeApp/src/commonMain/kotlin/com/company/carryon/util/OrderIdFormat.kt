package com.company.carryon.util

private val orderCodeRegex = Regex("^ORD[-_\\s]*(\\d+)$", RegexOption.IGNORE_CASE)

fun formatOrderDisplayId(bookingId: String, orderCode: String? = null): String {
    val normalizedOrderCode = orderCode
        ?.trim()
        ?.uppercase()
        ?.let { code ->
            val match = orderCodeRegex.matchEntire(code)
            val digits = match?.groupValues?.getOrNull(1)
            if (!digits.isNullOrBlank()) digits else null
        }

    if (!normalizedOrderCode.isNullOrBlank()) {
        return "ord ${normalizedOrderCode.takeLast(4).padStart(4, '0')}"
    }

    return "ord ${bookingId.filter { it.isDigit() }.takeLast(4).padStart(4, '0')}"
}
