package com.example.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.ui.theme.*
import com.example.carryon.ui.components.LanguageSelectionDialog
import com.example.carryon.ui.components.getLanguageDisplayName
import com.example.carryon.i18n.LocalStrings
import com.example.carryon.data.network.UserApi
import com.example.carryon.data.network.getLanguage
import com.example.carryon.data.network.saveLanguage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLanguageChanged: (String) -> Unit = {}
) {
    val strings = LocalStrings.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(getLanguage() ?: "en") }
    val scope = rememberCoroutineScope()

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { langCode ->
                currentLanguage = langCode
                saveLanguage(langCode)
                showLanguageDialog = false
                onLanguageChanged(langCode)
                scope.launch {
                    UserApi.updateLanguage(langCode)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.settings,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 22.sp, color = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8))
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            SettingsSection(title = strings.account) {
                SettingsItem(title = strings.editProfile, onClick = {})
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsItem(title = strings.changePassword, onClick = {})
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsItem(title = strings.savedAddressesMenu, onClick = {})
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSection(title = strings.notifications) {
                SettingsToggleItem(title = strings.pushNotifications, initialValue = true)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsToggleItem(title = strings.emailNotifications, initialValue = false)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsToggleItem(title = strings.smsNotifications, initialValue = true)
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSection(title = strings.preferences) {
                SettingsItem(title = strings.language, subtitle = getLanguageDisplayName(currentLanguage), onClick = { showLanguageDialog = true })
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsItem(title = strings.currency, subtitle = "MYR", onClick = {})
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSection(title = strings.about) {
                SettingsItem(title = strings.appVersion, subtitle = "1.0.0", onClick = {})
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsItem(title = strings.privacyPolicy, onClick = {})
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsItem(title = strings.termsOfService, onClick = {})
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextSecondary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsItem(title: String, subtitle: String = "", onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Text(text = ">", fontSize = 16.sp, color = Color(0xFFBDBDBD))
    }
}

@Composable
private fun SettingsToggleItem(title: String, initialValue: Boolean) {
    var checked by remember { mutableStateOf(initialValue) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
        )
    }
}
