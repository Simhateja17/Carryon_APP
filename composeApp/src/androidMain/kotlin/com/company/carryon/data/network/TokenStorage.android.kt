package com.company.carryon.data.network

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "carryon_prefs"
private const val KEY_TOKEN = "jwt_token"
private const val KEY_REFRESH_TOKEN = "refresh_token"
private const val KEY_TOKEN_EXPIRY_MS = "token_expiry_ms"
private const val KEY_AUTH_MODE = "auth_mode"
private const val KEY_LANGUAGE = "user_language"

private var prefs: SharedPreferences? = null

private fun prefsOrNull(): SharedPreferences? = prefs

fun initTokenStorage(context: Context) {
    if (prefs == null) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}

actual fun saveToken(token: String) {
    prefsOrNull()?.edit()?.putString(KEY_TOKEN, token)?.commit()
}

actual fun getToken(): String? {
    return prefsOrNull()?.getString(KEY_TOKEN, null)
}

actual fun clearToken() {
    prefsOrNull()?.edit()?.remove(KEY_TOKEN)?.commit()
}

actual fun saveRefreshToken(token: String) {
    prefsOrNull()?.edit()?.putString(KEY_REFRESH_TOKEN, token)?.commit()
}

actual fun getRefreshToken(): String? {
    return prefsOrNull()?.getString(KEY_REFRESH_TOKEN, null)
}

actual fun clearRefreshToken() {
    prefsOrNull()?.edit()?.remove(KEY_REFRESH_TOKEN)?.commit()
}

actual fun saveTokenExpiryEpochMs(expiryEpochMs: Long) {
    prefsOrNull()?.edit()?.putLong(KEY_TOKEN_EXPIRY_MS, expiryEpochMs)?.commit()
}

actual fun getTokenExpiryEpochMs(): Long? {
    val currentPrefs = prefsOrNull() ?: return null
    return if (currentPrefs.contains(KEY_TOKEN_EXPIRY_MS)) {
        currentPrefs.getLong(KEY_TOKEN_EXPIRY_MS, 0L)
    } else {
        null
    }
}

actual fun clearTokenExpiryEpochMs() {
    prefsOrNull()?.edit()?.remove(KEY_TOKEN_EXPIRY_MS)?.commit()
}

actual fun saveAuthMode(mode: AuthMode) {
    prefsOrNull()?.edit()?.putString(KEY_AUTH_MODE, mode.name)?.commit()
}

actual fun getAuthMode(): AuthMode? {
    val rawMode = prefsOrNull()?.getString(KEY_AUTH_MODE, null) ?: return null
    return rawMode.toAuthModeOrNull()
}

actual fun clearAuthMode() {
    prefsOrNull()?.edit()?.remove(KEY_AUTH_MODE)?.commit()
}

actual fun saveLanguage(language: String) {
    prefsOrNull()?.edit()?.putString(KEY_LANGUAGE, language)?.apply()
}

actual fun getLanguage(): String? {
    return prefsOrNull()?.getString(KEY_LANGUAGE, null)
}

private fun String.toAuthModeOrNull(): AuthMode? {
    return AuthMode.entries.firstOrNull { it.name == this }
}

actual fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
}
