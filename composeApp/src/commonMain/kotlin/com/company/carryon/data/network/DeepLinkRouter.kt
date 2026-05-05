package com.company.carryon.data.network

sealed class DeepLinkTarget {
    data class TrackBooking(val bookingId: String) : DeepLinkTarget()
    data class Referral(val code: String) : DeepLinkTarget()
}

object DeepLinkRouter {
    fun parse(rawUrl: String?): DeepLinkTarget? {
        val value = rawUrl?.trim()?.takeIf { it.isNotBlank() } ?: return null
        val withoutScheme = when {
            value.startsWith("carryon-customer://") -> value.removePrefix("carryon-customer://")
            value.startsWith("https://carryon.app/") -> value.removePrefix("https://carryon.app/")
            value.startsWith("http://carryon.app/") -> value.removePrefix("http://carryon.app/")
            else -> return null
        }
        val parts = withoutScheme.trim('/').split('/').filter { it.isNotBlank() }
        if (parts.size < 2) return null
        val payload = parts[1].trim()
        if (payload.isBlank()) return null
        return when (parts[0].lowercase()) {
            "track" -> DeepLinkTarget.TrackBooking(payload)
            "ref" -> DeepLinkTarget.Referral(payload.uppercase())
            else -> null
        }
    }
}
