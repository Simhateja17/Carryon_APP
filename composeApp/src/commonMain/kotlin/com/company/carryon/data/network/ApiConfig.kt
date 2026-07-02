package com.company.carryon.data.network

import io.ktor.client.*

expect fun apiBaseUrl(): String

expect fun createPlatformHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient
