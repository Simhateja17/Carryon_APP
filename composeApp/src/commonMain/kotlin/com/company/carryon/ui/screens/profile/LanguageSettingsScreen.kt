package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.network.UserApi
import com.company.carryon.data.network.getLanguage
import com.company.carryon.data.network.saveLanguage
import com.company.carryon.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

private data class LanguageOption(
    val code: String,
    val title: String,
    val subtitle: String,
    val icon: String
)

@Composable
fun LanguageSettingsScreen(
    onBack: () -> Unit,
    onLanguageChanged: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var selectedLanguage by remember { mutableStateOf(getLanguage() ?: "en") }

    val options = listOf(
        LanguageOption("en", "English", "Default System Language", "文A"),
        LanguageOption("hi", "Hindi", "हिन्दी", "ग"),
        LanguageOption("es", "Spanish", "Español", "ñ"),
        LanguageOption("fr", "French", "Français", "ç"),
        LanguageOption("de", "German", "Deutsch", "ä")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .verticalScroll(rememberScrollState())
    ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "←",
                        color = PrimaryBlue,
                        fontSize = 22.sp,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Settings",
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    Text("On", color = Color(0xFF282B51), fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Language",
                    color = Color.Black,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Select your preferred language for the\napplication interface.",
                    color = Color.Black,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                options.forEach { option ->
                    LanguageItemCard(
                        title = option.title,
                        subtitle = option.subtitle,
                        icon = option.icon,
                        selected = selectedLanguage == option.code,
                        onClick = {
                            selectedLanguage = option.code
                            saveLanguage(option.code)
                            onLanguageChanged(option.code)
                            scope.launch {
                                UserApi.updateLanguage(option.code)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBlue, RoundedCornerShape(32.dp))
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Global Connectivity",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Experience our services in your native\nlanguage for a more intuitive and\npersonalized delivery journey.",
                            color = Color(0xCCFFFFFF),
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
    }
}

@Composable
private fun LanguageItemCard(
    title: String,
    subtitle: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) Color(0xFFA6D2F3) else Color(0x33A6D2F3),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = PrimaryBlue, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = Color.Black, fontSize = 14.sp)
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    if (selected) PrimaryBlue else Color.Transparent,
                    CircleShape
                )
                .border(2.dp, PrimaryBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Text("", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
