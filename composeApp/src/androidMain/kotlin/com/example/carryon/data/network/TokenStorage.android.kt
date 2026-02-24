package com.example.carryon.data.network

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "carryon_prefs"
private const val KEY_TOKEN = "jwt_token"
private const val KEY_LANGUAGE = "user_language"

private lateinit var prefs: SharedPreferences

fun initTokenStorage(context: Context) {
    prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}

actual fun saveToken(token: String) {
    prefs.edit().putString(KEY_TOKEN, token).apply()
}

actual fun getToken(): String? {
    return prefs.getString(KEY_TOKEN, null)
}

actual fun clearToken() {
    prefs.edit().remove(KEY_TOKEN).apply()
}

actual fun saveLanguage(language: String) {
    prefs.edit().putString(KEY_LANGUAGE, language).apply()
}

actual fun getLanguage(): String? {
    return prefs.getString(KEY_LANGUAGE, null)
}
