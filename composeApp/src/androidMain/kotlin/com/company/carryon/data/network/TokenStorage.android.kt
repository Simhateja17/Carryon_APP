package com.company.carryon.data.network

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "carryon_prefs"
private const val KEY_TOKEN = "jwt_token"
private const val KEY_LANGUAGE = "user_language"

private var prefs: SharedPreferences? = null

private fun prefsOrNull(): SharedPreferences? = prefs

fun initTokenStorage(context: Context) {
    if (prefs == null) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}

actual fun saveToken(token: String) {
    prefsOrNull()?.edit()?.putString(KEY_TOKEN, token)?.apply()
}

actual fun getToken(): String? {
    return prefsOrNull()?.getString(KEY_TOKEN, null)
}

actual fun clearToken() {
    prefsOrNull()?.edit()?.remove(KEY_TOKEN)?.apply()
}

actual fun saveLanguage(language: String) {
    prefsOrNull()?.edit()?.putString(KEY_LANGUAGE, language)?.apply()
}

actual fun getLanguage(): String? {
    return prefsOrNull()?.getString(KEY_LANGUAGE, null)
}
