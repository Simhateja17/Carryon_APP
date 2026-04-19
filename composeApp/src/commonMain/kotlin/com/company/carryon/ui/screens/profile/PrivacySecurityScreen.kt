package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.privacy_change_password_icon
import carryon.composeapp.generated.resources.privacy_delete_account_icon
import carryon.composeapp.generated.resources.privacy_download_my_data_icon
import carryon.composeapp.generated.resources.privacy_location_access_icon
import carryon.composeapp.generated.resources.privacy_logged_in_devices_icon
import carryon.composeapp.generated.resources.privacy_notifications_icon
import com.company.carryon.ui.theme.PrimaryBlue
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun PrivacySecurityScreen(
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    onLoggedInDevices: () -> Unit
) {
    var locationEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
    ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    color = PrimaryBlue,
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Privacy & Security",
                    color = Color(0xFF282B51),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp)
            ) {
                SectionTitle("ACCOUNT SECURITY")
                Spacer(modifier = Modifier.height(12.dp))
                ActionCard(iconRes = Res.drawable.privacy_change_password_icon, title = "Change Password", onClick = onChangePassword)

                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("LOGIN & ACCESS")
                Spacer(modifier = Modifier.height(12.dp))
                ActionCard(iconRes = Res.drawable.privacy_logged_in_devices_icon, title = "Logged in devices", onClick = onLoggedInDevices)

                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("PERMISSIONS")
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33A6D2F3), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x33A6D2F3), RoundedCornerShape(12.dp))
                ) {
                    ToggleRow(
                        iconRes = Res.drawable.privacy_location_access_icon,
                        title = "Location Access",
                        checked = locationEnabled,
                        onToggle = { locationEnabled = !locationEnabled }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE7E6FF))
                    )
                    ToggleRow(
                        iconRes = Res.drawable.privacy_notifications_icon,
                        title = "Notifications",
                        checked = notificationsEnabled,
                        onToggle = { notificationsEnabled = !notificationsEnabled }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("DATA & PRIVACY")
                Spacer(modifier = Modifier.height(12.dp))
                ActionCard(iconRes = Res.drawable.privacy_download_my_data_icon, title = "Download my data")

                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "DANGER ZONE",
                    color = PrimaryBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33A6D2F3), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x33A6D2F3), RoundedCornerShape(12.dp))
                        .padding(vertical = 17.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.privacy_delete_account_icon),
                        contentDescription = "Delete Account",
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Account", color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Deleting your account is permanent. All your\ndelivery history, addresses, and saved data will be\nremoved.",
                    color = Color.Black,
                    fontSize = 12.sp,
                    lineHeight = 19.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun ActionCard(iconRes: DrawableResource, title: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33A6D2F3), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Text("›", color = Color.Black, fontSize = 28.sp)
    }
}

@Composable
private fun ToggleRow(
    iconRes: DrawableResource,
    title: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Box(
            modifier = Modifier
                .width(44.dp)
                .height(24.dp)
                .background(if (checked) Color(0xFF2F80ED) else Color(0xFFA6D2F3), RoundedCornerShape(999.dp))
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                    .padding(2.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )
        }
    }
}
