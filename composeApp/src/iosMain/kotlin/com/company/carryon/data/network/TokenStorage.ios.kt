package com.company.carryon.data.network

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.posix.time

private const val SERVICE_NAME = "com.company.carryon"
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

// Keychain helpers
@OptIn(ExperimentalForeignApi::class)
private fun keychainSave(key: String, value: String) {
    val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return

    // Delete existing item first
    keychainDelete(key)

    val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 5, null, null)
    CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
    CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(SERVICE_NAME))
    CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(key))
    CFDictionaryAddValue(query, kSecValueData, CFBridgingRetain(data))
    CFDictionaryAddValue(query, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlock)

    SecItemAdd(query, null)
}

@OptIn(ExperimentalForeignApi::class)
private fun keychainRead(key: String): String? {
    return memScoped {
        val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 5, null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(SERVICE_NAME))
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(key))
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)

        val result = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query, result.ptr)
        if (status != errSecSuccess) return@memScoped null

        val data = CFBridgingRelease(result.value) as? NSData ?: return@memScoped null
        NSString.create(data = data, encoding = NSUTF8StringEncoding) as? String
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun keychainDelete(key: String) {
    val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 3, null, null)
    CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
    CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(SERVICE_NAME))
    CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(key))
    SecItemDelete(query)
}

// Migrate from NSUserDefaults to Keychain
private var migrationDone = false
private fun migrateFromUserDefaults() {
    if (migrationDone) return
    migrationDone = true
    val defaults = NSUserDefaults.standardUserDefaults
    val oldToken = defaults.stringForKey(KEY_TOKEN)
    if (oldToken != null) {
        keychainSave(KEY_TOKEN, oldToken)
        defaults.removeObjectForKey(KEY_TOKEN)
        val oldRefresh = defaults.stringForKey(KEY_REFRESH_TOKEN)
        if (oldRefresh != null) {
            keychainSave(KEY_REFRESH_TOKEN, oldRefresh)
            defaults.removeObjectForKey(KEY_REFRESH_TOKEN)
        }
        val oldExpiry = defaults.stringForKey(KEY_TOKEN_EXPIRY_MS)
        if (oldExpiry != null) {
            keychainSave(KEY_TOKEN_EXPIRY_MS, oldExpiry)
            defaults.removeObjectForKey(KEY_TOKEN_EXPIRY_MS)
        }
    }
}

actual fun saveToken(token: String) {
    migrateFromUserDefaults()
    keychainSave(KEY_TOKEN, token)
}

actual fun getToken(): String? {
    migrateFromUserDefaults()
    return keychainRead(KEY_TOKEN)
}

actual fun clearToken() {
    keychainDelete(KEY_TOKEN)
}

actual fun saveRefreshToken(token: String) {
    migrateFromUserDefaults()
    keychainSave(KEY_REFRESH_TOKEN, token)
}

actual fun getRefreshToken(): String? {
    migrateFromUserDefaults()
    return keychainRead(KEY_REFRESH_TOKEN)
}

actual fun clearRefreshToken() {
    keychainDelete(KEY_REFRESH_TOKEN)
}

actual fun saveTokenExpiryEpochMs(expiryEpochMs: Long) {
    keychainSave(KEY_TOKEN_EXPIRY_MS, expiryEpochMs.toString())
}

actual fun getTokenExpiryEpochMs(): Long? {
    return keychainRead(KEY_TOKEN_EXPIRY_MS)?.toLongOrNull()
}

actual fun clearTokenExpiryEpochMs() {
    keychainDelete(KEY_TOKEN_EXPIRY_MS)
}

actual fun saveAuthMode(mode: AuthMode) {
    keychainSave(KEY_AUTH_MODE, mode.name)
}

actual fun getAuthMode(): AuthMode? {
    val rawMode = keychainRead(KEY_AUTH_MODE) ?: return null
    return rawMode.toAuthModeOrNull()
}

actual fun clearAuthMode() {
    keychainDelete(KEY_AUTH_MODE)
}

actual fun saveLanguage(language: String) {
    NSUserDefaults.standardUserDefaults.setObject(language, forKey = KEY_LANGUAGE)
}

actual fun getLanguage(): String? {
    return NSUserDefaults.standardUserDefaults.stringForKey(KEY_LANGUAGE)
}

actual fun savePushToken(token: String) {
    keychainSave(KEY_PUSH_TOKEN, token)
}

actual fun getPushToken(): String? {
    return keychainRead(KEY_PUSH_TOKEN)
}

actual fun clearPushToken() {
    keychainDelete(KEY_PUSH_TOKEN)
}

actual fun getOrCreateDeviceId(): String {
    val existing = NSUserDefaults.standardUserDefaults.stringForKey(KEY_PUSH_DEVICE_ID)
    if (!existing.isNullOrBlank()) return existing

    val created = platform.Foundation.NSUUID().UUIDString()
    NSUserDefaults.standardUserDefaults.setObject(created, forKey = KEY_PUSH_DEVICE_ID)
    return created
}

actual fun newUuid(): String = platform.Foundation.NSUUID().UUIDString()

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
