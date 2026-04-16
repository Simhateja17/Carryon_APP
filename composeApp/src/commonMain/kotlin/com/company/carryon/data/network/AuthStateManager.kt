package com.company.carryon.data.network

import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthStateManager {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    suspend fun checkAuth(): Boolean {
        return try {
            val session = SupabaseConfig.client.auth.currentSessionOrNull()
            if (session != null) {
                saveToken(session.accessToken)
                _isLoggedIn.value = true
                true
            } else {
                try {
                    SupabaseConfig.client.auth.refreshCurrentSession()
                    val refreshed = SupabaseConfig.client.auth.currentSessionOrNull()
                    if (refreshed != null) {
                        saveToken(refreshed.accessToken)
                        _isLoggedIn.value = true
                        true
                    } else {
                        _isLoggedIn.value = false
                        false
                    }
                } catch (_: Exception) {
                    _isLoggedIn.value = false
                    false
                }
            }
        } catch (_: Exception) {
            _isLoggedIn.value = false
            false
        }
    }

    fun onAuthExpired() {
        _isLoggedIn.value = false
    }

    suspend fun logout() {
        clearToken()
        try { SupabaseConfig.client.auth.signOut() } catch (_: Exception) { }
        _isLoggedIn.value = false
    }

    fun setLoggedIn(value: Boolean) {
        _isLoggedIn.value = value
    }
}
