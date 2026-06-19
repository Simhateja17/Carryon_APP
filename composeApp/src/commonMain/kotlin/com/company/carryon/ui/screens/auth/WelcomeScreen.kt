package com.company.carryon.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.welcome_hero
import org.jetbrains.compose.resources.painterResource
import com.company.carryon.ui.theme.*
import com.company.carryon.i18n.LocalStrings

@Composable
fun WelcomeScreen(
    onCreateAccount: () -> Unit,
    onLogin: () -> Unit
) {
    val strings = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.62f)
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.welcome_hero),
                contentDescription = "Carry On delivery van",
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .weight(0.38f)
                .padding(top = 36.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = strings.welcome,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.welcomeSubtitle,
                    fontSize = 16.sp,
                    color = TextSecondary
                )
                com.company.carryon.ui.components.CarryOnWordmark(fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onCreateAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    strings.createAnAccount,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
            ) {
                Text(
                    strings.logIn,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
            }
        }
    }
}
