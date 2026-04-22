package com.company.carryon.data.network

actual fun currentPushToken(): String? = getPushToken()

actual fun currentPushPlatform(): String = "ANDROID"
