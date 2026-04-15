package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.settings_auto_apply_coupons_icon
import carryon.composeapp.generated.resources.settings_clear_cache_icon
import carryon.composeapp.generated.resources.settings_dark_mode_icon
import carryon.composeapp.generated.resources.settings_data_saver_mode_icon
import carryon.composeapp.generated.resources.settings_default_vehicle_icon
import carryon.composeapp.generated.resources.settings_language_icon
import carryon.composeapp.generated.resources.settings_logout_icon
import carryon.composeapp.generated.resources.settings_save_last_address_icon
import com.company.carryon.ui.theme.*
import com.company.carryon.ui.components.getLanguageDisplayName
import com.company.carryon.data.network.UserApi
import com.company.carryon.data.network.getLanguage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val SettingsCardBackground = Color(0x33A6D2F3)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToSavedAddresses: () -> Unit,
    onNavigateToDefaultVehicle: () -> Unit,
    onNavigateToClearCache: () -> Unit,
    onLanguageChanged: (String) -> Unit = {}
) {
    var userName by remember { mutableStateOf("Alex Johnston") }
    var currentLanguage by remember { mutableStateOf(getLanguage() ?: "en") }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var autoApplyCoupons by remember { mutableStateOf(true) }
    var saveLastAddress by remember { mutableStateOf(false) }
    var dataSaverMode by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        UserApi.getProfile().onSuccess { profile ->
            if (profile.name.isNotBlank()) {
                userName = profile.name
            }
        }
        currentLanguage = getLanguage() ?: "en"
        onLanguageChanged(currentLanguage)
    }

    Scaffold(
        containerColor = Color(0xFFF3F4F6)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF3F4F6))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    color = PrimaryBlue,
                    fontSize = 22.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    color = Color(0xFF282B51),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACTIVE PROFILE",
                            color = Color.White,
                            fontSize = 12.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = userName,
                            color = Color(0xFF0B1220),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Logistics Manager",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("◉", color = PrimaryBlue, fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            SettingsGroupCard {
                SettingsToggleRow(
                    iconRes = Res.drawable.settings_dark_mode_icon,
                    title = "Dark Mode",
                    titleColor = PrimaryBlue,
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )
                SettingsNavRow(
                    iconRes = Res.drawable.settings_language_icon,
                    title = "Language",
                    titleColor = PrimaryBlue,
                    trailingText = getLanguageDisplayName(currentLanguage),
                    onClick = { onNavigateToLanguage() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SettingsGroupCard(
                heading = "DELIVERY PREFERENCES"
            ) {
                SettingsNavRow(
                    iconRes = Res.drawable.settings_save_last_address_icon,
                    title = "Saved address",
                    onClick = { onNavigateToSavedAddresses() }
                )
                SettingsNavRow(
                    iconRes = Res.drawable.settings_default_vehicle_icon,
                    title = "Default Vehicle",
                    onClick = { onNavigateToDefaultVehicle() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SettingsGroupCard(
                heading = "APP AUTOMATION"
            ) {
                SettingsToggleRow(
                    iconRes = Res.drawable.settings_auto_apply_coupons_icon,
                    title = "Auto-apply coupons",
                    checked = autoApplyCoupons,
                    onCheckedChange = { autoApplyCoupons = it }
                )
                SettingsToggleRow(
                    iconRes = Res.drawable.settings_save_last_address_icon,
                    title = "Save last address",
                    checked = saveLastAddress,
                    onCheckedChange = { saveLastAddress = it }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SettingsGroupCard(
                heading = "DATA & SECURITY"
            ) {
                SettingsNavRow(
                    iconRes = Res.drawable.settings_clear_cache_icon,
                    title = "Clear cache",
                    onClick = { onNavigateToClearCache() }
                )
                SettingsToggleRow(
                    iconRes = Res.drawable.settings_data_saver_mode_icon,
                    title = "Data saver mode",
                    checked = dataSaverMode,
                    onCheckedChange = { dataSaverMode = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(SettingsCardBackground, RoundedCornerShape(24.dp))
                    .clickable { }
                    .border(1.dp, Color(0xFFD5E1EF), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(Res.drawable.settings_logout_icon),
                        contentDescription = "Log Out",
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Out",
                        color = PrimaryBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "CARRYON LOGISTICS",
                color = Color.Black,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.1.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Version 2.5.0 (Build 902)",
                color = Color.Black,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SettingsGroupCard(
    heading: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SettingsCardBackground, RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFFD5E1EF), RoundedCornerShape(24.dp))
            .padding(8.dp)
    ) {
        if (heading != null) {
            Text(
                text = heading,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        content()
    }
}

@Composable
private fun SettingsNavRow(
    iconRes: DrawableResource,
    title: String,
    titleColor: Color = Color.Black,
    trailingText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )

        if (trailingText != null) {
            Text(
                text = trailingText,
                fontSize = 14.sp,
                color = PrimaryBlue,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Text(text = "›", fontSize = 18.sp, color = PrimaryBlue)
    }
}

@Composable
private fun SettingsToggleRow(
    iconRes: DrawableResource,
    title: String,
    titleColor: Color = Color.Black,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .width(44.dp)
                .height(24.dp)
                .background(
                    if (checked) Color(0xFF2F80ED) else Color(0xFFA6D2F3),
                    CircleShape
                )
                .clickable { onCheckedChange(!checked) }
                .padding(horizontal = 2.dp),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )
        }
    }
}
