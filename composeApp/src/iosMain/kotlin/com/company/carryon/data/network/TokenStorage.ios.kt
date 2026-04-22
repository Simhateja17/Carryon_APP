package com.company.carryon.data.network

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSUserDefaults
import platform.posix.time

private const val KEY_TOKEN = "jwt_token"
private const val KEY_REFRESH_TOKEN = "refresh_token"
private const val KEY_TOKEN_EXPIRY_MS = "token_expiry_ms"
private const val KEY_AUTH_MODE = "auth_mode"
private const val KEY_LANGUAGE = "user_language"
private const val KEY_PUSH_TOKEN = "push_token"
private const val KEY_PUSH_DEVICE_ID = "push_device_id"
private const val KEY_PUSH_TYPE = "push_type"
private const val KEY_PUSH_BOOKING_ID = "push_booking_id"
private const val KEY_PUSH_TARGET_SCREEN = "push_target_screen"

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

actual fun savePushToken(token: String) {
    NSUserDefaults.standardUserDefaults.setObject(token, forKey = KEY_PUSH_TOKEN)
}

actual fun getPushToken(): String? {
    return NSUserDefaults.standardUserDefaults.stringForKey(KEY_PUSH_TOKEN)
}

actual fun clearPushToken() {
    NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY_PUSH_TOKEN)
}

actual fun getOrCreateDeviceId(): String {
    val existing = NSUserDefaults.standardUserDefaults.stringForKey(KEY_PUSH_DEVICE_ID)
    if (!existing.isNullOrBlank()) return existing

    val created = platform.Foundation.NSUUID().UUIDString()
    NSUserDefaults.standardUserDefaults.setObject(created, forKey = KEY_PUSH_DEVICE_ID)
    return created
}

actual fun savePendingPushNavigation(type: String, bookingId: String?, targetScreen: String?) {
    NSUserDefaults.standardUserDefaults.setObject(type, forKey = KEY_PUSH_TYPE)
    if (bookingId == null) {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY_PUSH_BOOKING_ID)
    } else {
        NSUserDefaults.standardUserDefaults.setObject(bookingId, forKey = KEY_PUSH_BOOKING_ID)
    }
    if (targetScreen == null) {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY_PUSH_TARGET_SCREEN)
    } else {
        NSUserDefaults.standardUserDefaults.setObject(targetScreen, forKey = KEY_PUSH_TARGET_SCREEN)
    }
}

actual fun consumePendingPushNavigation(): PendingPushNavigation? {
    val defaults = NSUserDefaults.standardUserDefaults
    val type = defaults.stringForKey(KEY_PUSH_TYPE) ?: return null
    val bookingId = defaults.stringForKey(KEY_PUSH_BOOKING_ID)
    val targetScreen = defaults.stringForKey(KEY_PUSH_TARGET_SCREEN)
    defaults.removeObjectForKey(KEY_PUSH_TYPE)
    defaults.removeObjectForKey(KEY_PUSH_BOOKING_ID)
    defaults.removeObjectForKey(KEY_PUSH_TARGET_SCREEN)
    return PendingPushNavigation(type = type, bookingId = bookingId, targetScreen = targetScreen)
}

private fun String.toAuthModeOrNull(): AuthMode? {
    return AuthMode.entries.firstOrNull { it.name == this }
}

@OptIn(ExperimentalForeignApi::class)
actual fun currentTimeMillis(): Long {
    return time(null).toLong() * 1000L
}
