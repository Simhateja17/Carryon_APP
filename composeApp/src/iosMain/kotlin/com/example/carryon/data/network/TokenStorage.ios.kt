package com.example.carryon.data.network

import platform.Foundation.NSUserDefaults

private const val KEY_TOKEN = "jwt_token"
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

actual fun saveLanguage(language: String) {
    NSUserDefaults.standardUserDefaults.setObject(language, forKey = KEY_LANGUAGE)
}

actual fun getLanguage(): String? {
    return NSUserDefaults.standardUserDefaults.stringForKey(KEY_LANGUAGE)
}
