package com.company.carryon.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.carryon_logo
import org.jetbrains.compose.resources.painterResource
import com.company.carryon.data.network.SupabaseConfig
import com.company.carryon.data.network.getToken
import com.company.carryon.data.network.saveToken
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onLoggedIn: () -> Unit,
    onNotLoggedIn: () -> Unit
) {
    LaunchedEffect(Unit) {
        // Check auth and show splash for at least 800ms — run both in parallel
        val minSplashMs = 800L
        val hasValidSession = coroutineScope {
            val authDeferred = async {
                try {
                    val session = SupabaseConfig.client.auth.currentSessionOrNull()
                    if (session != null) {
                        saveToken(session.accessToken)
                        true
                    } else {
                        try {
                            SupabaseConfig.client.auth.refreshCurrentSession()
                            val refreshed = SupabaseConfig.client.auth.currentSessionOrNull()
                            if (refreshed != null) {
                                saveToken(refreshed.accessToken)
                                true
                            } else {
                                false
                            }
                        } catch (_: Exception) {
                            false
                        }
                    }
                } catch (_: Exception) {
                    false
                }
            }
            // Minimum splash display time
            delay(minSplashMs)
            authDeferred.await()
        }

        if (hasValidSession) {
            onLoggedIn()
        } else if (getToken() != null) {
            // Fallback: we have a stored token but no Supabase session.
            // The token might still be valid (within its expiry window).
            // Navigate to home — API calls will fail with 401 if truly expired,
            // and the app can handle that by redirecting to login.
            onLoggedIn()
        } else {
            onNotLoggedIn()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.carryon_logo),
            contentDescription = "CarryOn Logo",
            modifier = Modifier.size(360.dp),
            contentScale = ContentScale.Fit
        )
    }
}
