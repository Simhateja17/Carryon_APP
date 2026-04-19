package com.company.carryon.data.network

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSUserDefaults
import platform.posix.time

private const val KEY_TOKEN = "jwt_token"
private const val KEY_REFRESH_TOKEN = "refresh_token"
private const val KEY_TOKEN_EXPIRY_MS = "token_expiry_ms"
private const val KEY_AUTH_MODE = "auth_mode"
private const val KEY_LANGUAGE = "user_language"

actual fun saveToken(token: String) {
    NSUserDefaults.standardUserDefaults.setObject(token, forKey = KEY_TOKEN)
}

actual fun getToken(): String? {
    return NSUserDefaults.standardUserDefaults.stringForKey(KEY_TOKEN)
}

actual fun clearToken() {
    NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY_TOKEN)
}

actual fun saveRefreshToken(token: String) {
    NSUserDefaults.standardUserDefaults.setObject(token, forKey = KEY_REFRESH_TOKEN)
}

actual fun getRefreshToken(): String? {
    return NSUserDefaults.standardUserDefaults.stringForKey(KEY_REFRESH_TOKEN)
}

actual fun clearRefreshToken() {
    NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY_REFRESH_TOKEN)
}

actual fun saveTokenExpiryEpochMs(expiryEpochMs: Long) {
    NSUserDefaults.standardUserDefaults.setObject(expiryEpochMs.toString(), forKey = KEY_TOKEN_EXPIRY_MS)
}

actual fun getTokenExpiryEpochMs(): Long? {
    return NSUserDefaults.standardUserDefaults.stringForKey(KEY_TOKEN_EXPIRY_MS)?.toLongOrNull()
}

actual fun clearTokenExpiryEpochMs() {
    NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY_TOKEN_EXPIRY_MS)
}

actual fun saveAuthMode(mode: AuthMode) {
    NSUserDefaults.standardUserDefaults.setObject(mode.name, forKey = KEY_AUTH_MODE)
}

actual fun getAuthMode(): AuthMode? {
    val rawMode = NSUserDefaults.standardUserDefaults.stringForKey(KEY_AUTH_MODE) ?: return null
    return rawMode.toAuthModeOrNull()
}

actual fun clearAuthMode() {
    NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY_AUTH_MODE)
}

actual fun saveLanguage(language: String) {
    NSUserDefaults.standardUserDefaults.setObject(language, forKey = KEY_LANGUAGE)
}

actual fun getLanguage(): String? {
    return NSUserDefaults.standardUserDefaults.stringForKey(KEY_LANGUAGE)
}

private fun String.toAuthModeOrNull(): AuthMode? {
    return AuthMode.entries.firstOrNull { it.name == this }
}

@OptIn(ExperimentalForeignApi::class)
actual fun currentTimeMillis(): Long {
    return time(null).toLong() * 1000L
}
