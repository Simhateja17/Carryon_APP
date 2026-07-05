package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.runtime.*
import com.company.carryon.data.network.AuthStateManager
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.network.UserApi
import com.company.carryon.data.network.HttpClientFactory
import com.company.carryon.data.network.UploadApi
import com.company.carryon.i18n.LocalStrings
import com.company.carryon.ui.components.CarryOnHeader
import com.company.carryon.ui.components.decodeImageBytes
import com.company.carryon.ui.components.rememberImagePickerLauncher
import androidx.compose.ui.text.style.TextOverflow
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.TextPrimary
import com.company.carryon.ui.theme.TextSecondary
import com.company.carryon.ui.theme.carryOnWhiteCard
import io.ktor.client.call.body
import io.ktor.client.request.get

@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSavedAddresses: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToCalculate: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToTrackShipment: () -> Unit,
    onNavigateToDriverRating: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToWallet: () -> Unit = {},
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit = {},
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf(ProfileScreenMemoryCache.userName ?: "—") }
    var userPhone by remember { mutableStateOf(ProfileScreenMemoryCache.userPhone ?: "—") }
    var isLoading by remember { mutableStateOf(!ProfileScreenMemoryCache.hasProfile) }
    var profileError by remember { mutableStateOf<String?>(null) }
    var totalShipments by remember { mutableStateOf(ProfileScreenMemoryCache.totalShipments) }
    var userRating by remember { mutableStateOf(ProfileScreenMemoryCache.userRating) }
    var statsError by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeletingAccount by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    var profileImageBitmap by remember { mutableStateOf(ProfileImageMemoryCache.bitmap) }
    var isUploadingProfileImage by remember { mutableStateOf(false) }
    var profileImageError by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberImagePickerLauncher(
        onImagePickFailed = { message -> profileImageError = message },
        onImagePicked = { imageBytes ->
            profileImageBitmap = decodeImageBytes(imageBytes) ?: profileImageBitmap
            profileImageError = null
            scope.launch {
                isUploadingProfileImage = true
                UploadApi.uploadProfileImage(imageBytes)
                    .onSuccess { upload ->
                        profileImageError = null
                        ProfileImageMemoryCache.put(upload.profileImage, profileImageBitmap)
                        ProfileScreenMemoryCache.profileImage = upload.profileImage
                        loadProfileImageBitmap(upload.profileImageUrl)
                            .onSuccess { bitmap ->
                                val resolvedBitmap = bitmap ?: profileImageBitmap
                                profileImageBitmap = resolvedBitmap
                                ProfileImageMemoryCache.put(upload.profileImage, resolvedBitmap)
                            }
                    }
                    .onFailure { error ->
                        profileImageError = error.message ?: "Failed to upload profile image"
                    }
                isUploadingProfileImage = false
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!ProfileScreenMemoryCache.hasProfile) {
            UserApi.getProfile()
                .onSuccess { user ->
                    userName = user.name.ifBlank { "—" }
                    userPhone = user.phone.ifBlank { "—" }
                    ProfileScreenMemoryCache.storeProfile(
                        userName = userName,
                        userPhone = userPhone,
                        profileImage = user.profileImage
                    )
                    profileError = null
                    val storedProfileImage = user.profileImage?.takeIf { it.isNotBlank() }
                    if (storedProfileImage == null) {
                        ProfileImageMemoryCache.clear()
                        profileImageBitmap = null
                    } else if (ProfileImageMemoryCache.key == storedProfileImage && ProfileImageMemoryCache.bitmap != null) {
                        profileImageBitmap = ProfileImageMemoryCache.bitmap
                    } else {
                        user.profileImageUrl?.takeIf { it.isNotBlank() }?.let { imageUrl ->
                            loadProfileImageBitmap(imageUrl)
                                .onSuccess { bitmap ->
                                    profileImageBitmap = bitmap
                                    ProfileImageMemoryCache.put(storedProfileImage, bitmap)
                                }
                                .onFailure { profileImageError = it.message ?: "Failed to load profile image" }
                        }
                    }
                }
                .onFailure { profileError = it.message ?: "Failed to load profile" }
        } else if (ProfileImageMemoryCache.bitmap != null) {
            profileImageBitmap = ProfileImageMemoryCache.bitmap
        }

        if (!ProfileScreenMemoryCache.hasStats) {
            UserApi.getUserStats()
                .onSuccess { stats ->
                    totalShipments = stats.totalShipments
                    userRating = stats.userRating
                    ProfileScreenMemoryCache.storeStats(totalShipments, userRating)
                    statsError = null
                }
                .onFailure { statsError = it.message ?: "Failed to load stats" }
        }

        isLoading = false
    }

    val displayName = if (isLoading) strings.loading else userName

    Scaffold(
        containerColor = Color(0xFFF5F6F8),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F6F8))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            CarryOnHeader(
                title = strings.profile,
                titleColor = Color(0xFF1F2937),
                onBack = onBack,
                contentPadding = PaddingValues(horizontal = 2.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(130.dp)) {
                    val avatarModifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(34.dp))
                        .clickable(enabled = !isUploadingProfileImage) { imagePicker.launch() }
                    val selectedProfileImage = profileImageBitmap
                    if (selectedProfileImage != null) {
                        Image(
                            bitmap = selectedProfileImage,
                            contentDescription = "Profile image",
                            contentScale = ContentScale.Crop,
                            modifier = avatarModifier
                        )
                    } else {
                        Box(
                            modifier = avatarModifier.background(Color(0xFFEAF1FB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Profile image placeholder",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .align(Alignment.BottomEnd)
                            .clickable(enabled = !isUploadingProfileImage) { imagePicker.launch() }
                            .background(PrimaryBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUploadingProfileImage) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Add profile image",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = displayName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                color = Color(0xFF111827),
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (isLoading) strings.loading else userPhone,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF111827),
                fontSize = 16.sp
            )

            if (profileError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = profileError ?: "",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            if (profileImageError != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = profileImageError ?: "",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(142.dp),
                    icon = Icons.Outlined.LocalShipping,
                    value = if (statsError != null) "—" else totalShipments.toString(),
                    label = strings.totalShipments.uppercase()
                )
                StatCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(142.dp),
                    icon = Icons.Outlined.StarOutline,
                    value = if (statsError != null) "—" else "${userRating.toInt()}.0",
                    label = strings.userRating.uppercase()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            ProfileOptionCard(icon = Icons.Outlined.PersonOutline, title = strings.personalInfo, onClick = onNavigateToEditProfile)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(icon = Icons.Outlined.LocationOn, title = strings.savedAddressesMenu, onClick = onNavigateToSavedAddresses)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(icon = Icons.Outlined.AccountBalanceWallet, title = strings.paymentsAndWallet, subtitleBadge = strings.verified.uppercase(), onClick = onNavigateToWallet)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(icon = Icons.Outlined.Settings, title = strings.settings, onClick = onNavigateToSettings)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(icon = Icons.AutoMirrored.Outlined.HelpOutline, title = strings.helpAndSupport, onClick = onNavigateToHelp)

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .carryOnWhiteCard(RoundedCornerShape(18.dp))
                    .clickable {
                        ProfileScreenMemoryCache.clear()
                        ProfileImageMemoryCache.clear()
                        onLogout()
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = strings.logout,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.logout, color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x1AE53935), RoundedCornerShape(18.dp))
                    .clickable { showDeleteDialog = true }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = strings.deleteAccount,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.deleteAccount, color = Color(0xFFE53935), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { if (!isDeletingAccount) showDeleteDialog = false },
                    title = { Text(strings.deleteAccount) },
                    text = {
                        if (isDeletingAccount) {
                            CircularProgressIndicator()
                        } else {
                            Text(deleteError ?: strings.deleteAccountWarning)
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    isDeletingAccount = true
                                    deleteError = null
                                    UserApi.deleteAccount()
                                        .onSuccess {
                                            AuthStateManager.logout()
                                            ProfileScreenMemoryCache.clear()
                                            ProfileImageMemoryCache.clear()
                                            showDeleteDialog = false
                                            isDeletingAccount = false
                                            onDeleteAccount()
                                        }
                                        .onFailure { e ->
                                            isDeletingAccount = false
                                            deleteError = e.message ?: "Failed to delete account"
                                        }
                                }
                            },
                            enabled = !isDeletingAccount
                        ) {
                            Text(strings.deleteAccount, color = Color(0xFFE53935))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false },
                            enabled = !isDeletingAccount
                        ) {
                            Text(strings.cancel)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "CARRYON V.2.4.0",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF6B7280),
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

private suspend fun loadProfileImageBitmap(imageUrl: String): Result<ImageBitmap?> = runCatching {
    val bytes = HttpClientFactory.publicClient.get(imageUrl).body<ByteArray>()
    decodeImageBytes(bytes)
}

private object ProfileImageMemoryCache {
    var key: String? = null
        private set
    var bitmap: ImageBitmap? = null
        private set

    fun put(nextKey: String, nextBitmap: ImageBitmap?) {
        key = nextKey
        bitmap = nextBitmap
    }

    fun clear() {
        key = null
        bitmap = null
    }
}

private object ProfileScreenMemoryCache {
    var userName: String? = null
        private set
    var userPhone: String? = null
        private set
    var profileImage: String? = null
    var totalShipments: Int = 0
        private set
    var userRating: Double = 0.0
        private set
    var hasProfile: Boolean = false
        private set
    var hasStats: Boolean = false
        private set

    fun storeProfile(userName: String, userPhone: String, profileImage: String?) {
        this.userName = userName
        this.userPhone = userPhone
        this.profileImage = profileImage
        hasProfile = true
    }

    fun storeStats(totalShipments: Int, userRating: Double) {
        this.totalShipments = totalShipments
        this.userRating = userRating
        hasStats = true
    }

    fun clear() {
        userName = null
        userPhone = null
        profileImage = null
        totalShipments = 0
        userRating = 0.0
        hasProfile = false
        hasStats = false
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x26000000),
                spotColor = Color(0x26000000)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(value, color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Medium, lineHeight = 32.sp, maxLines = 1)
        Text(
            label,
            color = Color.Black,
            fontSize = 11.sp,
            letterSpacing = 0.3.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProfileOptionCard(
    icon: ImageVector,
    title: String,
    subtitleBadge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0x26000000),
                spotColor = Color(0x26000000)
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, color = TextPrimary)
            if (subtitleBadge != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .background(Color.White, RoundedCornerShape(999.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(subtitleBadge, color = PrimaryBlue, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Text(">", fontSize = 20.sp, color = Color(0xFF111827))
    }
}
