package com.company.carryon.data.network

import io.ktor.client.*

actual fun apiBaseUrl(): String = "https://api.carryon.my"

actual fun createPlatformHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient { block() }
}
