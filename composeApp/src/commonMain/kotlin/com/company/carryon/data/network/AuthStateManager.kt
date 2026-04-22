package com.company.carryon.data.network

import com.company.carryon.data.model.AuthResponse
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthStateManager {

    private const val TOKEN_REFRESH_SAFETY_WINDOW_MS = 60_000L

    private val _isLoggedIn = MutableStateFlow(false)
    private val refreshMutex = Mutex()
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    suspend fun checkAuth(): Boolean {
        return ensureFreshToken()
    }

    suspend fun ensureFreshToken(): Boolean {
        return when (resolveAuthMode()) {
            AuthMode.SUPABASE -> ensureSupabaseSession()
            AuthMode.OTP -> ensureOtpToken()
            null -> ensureSupabaseSession() || ensureLegacyOrOtpToken()
        }
    }

    suspend fun getValidAccessToken(): String? {
        if (!ensureFreshToken()) return null
        return when (resolveAuthMode()) {
            AuthMode.SUPABASE -> {
                try {
                    SupabaseConfig.client.auth.currentSessionOrNull()?.accessToken ?: getToken()
                } catch (_: Exception) {
                    getToken()
                }
            }
            AuthMode.OTP, null -> getToken()
        }
    }

    fun onOtpAuthenticated(authResponse: AuthResponse) {
        persistOtpTokens(
            accessToken = authResponse.token,
            refreshToken = authResponse.refreshToken,
            expiresInSeconds = authResponse.expiresIn
        )
    }

    fun onSupabaseAuthenticated(accessToken: String) {
        saveToken(accessToken)
        clearRefreshToken()
        clearTokenExpiryEpochMs()
        saveAuthMode(AuthMode.SUPABASE)
        _isLoggedIn.value = true
    }

    fun onAuthExpired() {
        clearStoredAuthState()
    }

    suspend fun logout() {
        runCatching { PushRegistrar.unregisterUserPushToken() }
        clearStoredAuthState()
        try { SupabaseConfig.client.auth.signOut() } catch (_: Exception) { }
    }

    fun setLoggedIn(value: Boolean) {
        _isLoggedIn.value = value
    }

    private suspend fun ensureSupabaseSession(): Boolean {
        return try {
            SupabaseConfig.client.auth.awaitInitialization()
            val session = SupabaseConfig.client.auth.currentSessionOrNull()
            if (session != null) {
                onSupabaseAuthenticated(session.accessToken)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun ensureLegacyOrOtpToken(): Boolean {
        val token = getToken()
        if (token.isNullOrBlank()) {
            _isLoggedIn.value = false
            return false
        }

        return if (getRefreshToken() != null || getTokenExpiryEpochMs() != null) {
            ensureOtpToken()
        } else {
            _isLoggedIn.value = true
            true
        }
    }

    private suspend fun ensureOtpToken(): Boolean {
        val token = getToken()
        if (token.isNullOrBlank()) {
            clearStoredAuthState()
            return false
        }

        val expiryEpochMs = getTokenExpiryEpochMs()
        if (expiryEpochMs == null) {
            saveAuthMode(AuthMode.OTP)
            _isLoggedIn.value = true
            return true
        }

        if (currentEpochMs() < expiryEpochMs) {
            saveAuthMode(AuthMode.OTP)
            _isLoggedIn.value = true
            return true
        }

        val refreshToken = getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            clearStoredAuthState()
            return false
        }

        return refreshMutex.withLock {
            val latestExpiry = getTokenExpiryEpochMs()
            if (latestExpiry != null && currentEpochMs() < latestExpiry) {
                _isLoggedIn.value = true
                true
            } else {
                AuthApi.refresh(refreshToken).fold(
                    onSuccess = { response ->
                        persistOtpTokens(
                            accessToken = response.token,
                            refreshToken = response.refreshToken ?: refreshToken,
                            expiresInSeconds = response.expiresIn
                        )
                        true
                    },
                    onFailure = {
                        clearStoredAuthState()
                        false
                    }
                )
            }
        }
    }

    private fun persistOtpTokens(
        accessToken: String,
        refreshToken: String?,
        expiresInSeconds: Long?
    ) {
        saveToken(accessToken)
        if (refreshToken.isNullOrBlank()) {
            clearRefreshToken()
        } else {
            saveRefreshToken(refreshToken)
        }
        if (expiresInSeconds != null) {
            val effectiveExpiry = currentEpochMs() +
                maxOf((expiresInSeconds * 1000L) - TOKEN_REFRESH_SAFETY_WINDOW_MS, 0L)
            saveTokenExpiryEpochMs(effectiveExpiry)
        } else {
            clearTokenExpiryEpochMs()
        }
        saveAuthMode(AuthMode.OTP)
        _isLoggedIn.value = true
    }

    private fun clearStoredAuthState() {
        clearToken()
        clearRefreshToken()
        clearTokenExpiryEpochMs()
        clearAuthMode()
        _isLoggedIn.value = false
    }

    private fun resolveAuthMode(): AuthMode? {
        val storedMode = getAuthMode()
        if (storedMode != null) return storedMode
        return if (!getRefreshToken().isNullOrBlank() || getTokenExpiryEpochMs() != null) {
            AuthMode.OTP
        } else {
            null
        }
    }

    private fun currentEpochMs(): Long {
        return currentTimeMillis()
    }
}
