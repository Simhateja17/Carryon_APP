package com.company.carryon.data.network

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val PREFS_NAME = "carryon_secure_prefs"
private const val KEY_TOKEN = "jwt_token"
private const val KEY_REFRESH_TOKEN = "refresh_token"
private const val KEY_TOKEN_EXPIRY_MS = "token_expiry_ms"
private const val KEY_AUTH_MODE = "auth_mode"
private const val KEY_PUSH_TOKEN = "push_token"
private const val KEY_PUSH_DEVICE_ID = "push_device_id"
private const val KEY_PUSH_TYPE = "push_type"
private const val KEY_PUSH_BOOKING_ID = "push_booking_id"
private const val KEY_PUSH_TARGET_SCREEN = "push_target_screen"

// Non-sensitive prefs (language, device id) use plain SharedPreferences
private const val PLAIN_PREFS_NAME = "carryon_prefs"
private const val KEY_LANGUAGE = "user_language"

private var securePrefs: SharedPreferences? = null
private var plainPrefs: SharedPreferences? = null

fun initTokenStorage(context: Context) {
    if (securePrefs == null) {
        val masterKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        securePrefs = EncryptedSharedPreferences.create(
            context.applicationContext,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    if (plainPrefs == null) {
        plainPrefs = context.applicationContext.getSharedPreferences(PLAIN_PREFS_NAME, Context.MODE_PRIVATE)
    }
    // Migrate tokens from old plain prefs to encrypted prefs
    migrateFromPlainPrefs(context)
}

private fun migrateFromPlainPrefs(context: Context) {
    val oldPrefs = context.applicationContext.getSharedPreferences("carryon_prefs", Context.MODE_PRIVATE)
    val oldToken = oldPrefs.getString(KEY_TOKEN, null)
    if (oldToken != null) {
        securePrefs?.edit()?.putString(KEY_TOKEN, oldToken)?.commit()
        val oldRefresh = oldPrefs.getString(KEY_REFRESH_TOKEN, null)
        if (oldRefresh != null) {
            securePrefs?.edit()?.putString(KEY_REFRESH_TOKEN, oldRefresh)?.commit()
        }
        if (oldPrefs.contains(KEY_TOKEN_EXPIRY_MS)) {
            securePrefs?.edit()?.putLong(KEY_TOKEN_EXPIRY_MS, oldPrefs.getLong(KEY_TOKEN_EXPIRY_MS, 0L))?.commit()
        }
        // Clear tokens from old unencrypted storage
        oldPrefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_TOKEN_EXPIRY_MS)
            .commit()
    }
}

actual fun saveToken(token: String) {
    securePrefs?.edit()?.putString(KEY_TOKEN, token)?.commit()
}

actual fun getToken(): String? {
    return securePrefs?.getString(KEY_TOKEN, null)
}

actual fun clearToken() {
    securePrefs?.edit()?.remove(KEY_TOKEN)?.commit()
}

actual fun saveRefreshToken(token: String) {
    securePrefs?.edit()?.putString(KEY_REFRESH_TOKEN, token)?.commit()
}

actual fun getRefreshToken(): String? {
    return securePrefs?.getString(KEY_REFRESH_TOKEN, null)
}

actual fun clearRefreshToken() {
    securePrefs?.edit()?.remove(KEY_REFRESH_TOKEN)?.commit()
}

actual fun saveTokenExpiryEpochMs(expiryEpochMs: Long) {
    securePrefs?.edit()?.putLong(KEY_TOKEN_EXPIRY_MS, expiryEpochMs)?.commit()
}

actual fun getTokenExpiryEpochMs(): Long? {
    val prefs = securePrefs ?: return null
    return if (prefs.contains(KEY_TOKEN_EXPIRY_MS)) {
        prefs.getLong(KEY_TOKEN_EXPIRY_MS, 0L)
    } else {
        null
    }
}

actual fun clearTokenExpiryEpochMs() {
    securePrefs?.edit()?.remove(KEY_TOKEN_EXPIRY_MS)?.commit()
}

actual fun saveAuthMode(mode: AuthMode) {
    securePrefs?.edit()?.putString(KEY_AUTH_MODE, mode.name)?.commit()
}

actual fun getAuthMode(): AuthMode? {
    val rawMode = securePrefs?.getString(KEY_AUTH_MODE, null) ?: return null
    return rawMode.toAuthModeOrNull()
}

actual fun clearAuthMode() {
    securePrefs?.edit()?.remove(KEY_AUTH_MODE)?.commit()
}

actual fun saveLanguage(language: String) {
    plainPrefs?.edit()?.putString(KEY_LANGUAGE, language)?.apply()
}

actual fun getLanguage(): String? {
    return plainPrefs?.getString(KEY_LANGUAGE, null)
}

actual fun savePushToken(token: String) {
    securePrefs?.edit()?.putString(KEY_PUSH_TOKEN, token)?.commit()
}

actual fun getPushToken(): String? {
    return securePrefs?.getString(KEY_PUSH_TOKEN, null)
}

actual fun clearPushToken() {
    securePrefs?.edit()?.remove(KEY_PUSH_TOKEN)?.commit()
}

actual fun getOrCreateDeviceId(): String {
    val prefs = plainPrefs ?: return java.util.UUID.randomUUID().toString()
    val existing = prefs.getString(KEY_PUSH_DEVICE_ID, null)
    if (!existing.isNullOrBlank()) return existing

    val created = java.util.UUID.randomUUID().toString()
    prefs.edit().putString(KEY_PUSH_DEVICE_ID, created).commit()
    return created
}

actual fun newUuid(): String = java.util.UUID.randomUUID().toString()

actual fun savePendingPushNavigation(type: String, bookingId: String?, targetScreen: String?) {
    plainPrefs?.edit()
        ?.putString(KEY_PUSH_TYPE, type)
        ?.putString(KEY_PUSH_BOOKING_ID, bookingId)
        ?.putString(KEY_PUSH_TARGET_SCREEN, targetScreen)
        ?.commit()
}

actual fun consumePendingPushNavigation(): PendingPushNavigation? {
    val prefs = plainPrefs ?: return null
    val type = prefs.getString(KEY_PUSH_TYPE, null) ?: return null
    val bookingId = prefs.getString(KEY_PUSH_BOOKING_ID, null)
    val targetScreen = prefs.getString(KEY_PUSH_TARGET_SCREEN, null)
    prefs.edit()
        .remove(KEY_PUSH_TYPE)
        .remove(KEY_PUSH_BOOKING_ID)
        .remove(KEY_PUSH_TARGET_SCREEN)
        .commit()
    return PendingPushNavigation(type = type, bookingId = bookingId, targetScreen = targetScreen)
}

private fun String.toAuthModeOrNull(): AuthMode? {
    return AuthMode.entries.firstOrNull { it.name == this }
}

actual fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
}
