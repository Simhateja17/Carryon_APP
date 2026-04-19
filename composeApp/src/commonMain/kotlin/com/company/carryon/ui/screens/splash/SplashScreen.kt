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
import com.company.carryon.data.network.AuthStateManager
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
                AuthStateManager.ensureFreshToken()
            }
            // Minimum splash display time
            delay(minSplashMs)
            authDeferred.await()
        }

        if (hasValidSession) {
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
