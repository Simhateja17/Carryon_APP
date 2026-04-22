package com.company.carryon.data.network

enum class AuthMode {
    OTP,
    SUPABASE
}

expect fun saveToken(token: String)
expect fun getToken(): String?
expect fun clearToken()

expect fun saveRefreshToken(token: String)
expect fun getRefreshToken(): String?
expect fun clearRefreshToken()

expect fun saveTokenExpiryEpochMs(expiryEpochMs: Long)
expect fun getTokenExpiryEpochMs(): Long?
expect fun clearTokenExpiryEpochMs()

expect fun saveAuthMode(mode: AuthMode)
expect fun getAuthMode(): AuthMode?
expect fun clearAuthMode()

expect fun saveLanguage(language: String)
expect fun getLanguage(): String?

expect fun savePushToken(token: String)
expect fun getPushToken(): String?
expect fun clearPushToken()

expect fun getOrCreateDeviceId(): String

expect fun savePendingPushNavigation(type: String, bookingId: String?, targetScreen: String?)
expect fun consumePendingPushNavigation(): PendingPushNavigation?

expect fun currentTimeMillis(): Long
