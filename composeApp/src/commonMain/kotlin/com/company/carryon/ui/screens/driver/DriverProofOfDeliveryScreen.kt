package com.company.carryon.ui.screens.driver

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.model.DeliveryJob
import com.company.carryon.data.network.DriverJobsApi
import com.company.carryon.data.network.DriverUploadApi
import com.company.carryon.ui.components.decodeImageBytes
import com.company.carryon.ui.components.rememberCameraCapture
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.TextPrimary
import com.company.carryon.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private const val RecipientOtpLength = 6

@Composable
fun DriverProofOfDeliveryScreen(
    jobId: String,
    onBack: () -> Unit,
    onSubmitted: (DeliveryJob) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var job by remember { mutableStateOf<DeliveryJob?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isRequestingOtp by remember { mutableStateOf(false) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var capturedBytes by remember { mutableStateOf<ByteArray?>(null) }
    var uploadedPhotoUrl by remember { mutableStateOf<String?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun showMessage(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    suspend fun loadJob() {
        isLoading = true
        loadError = null
        DriverJobsApi.getJob(jobId)
            .onSuccess { response ->
                job = response.data
                if (recipientName.isBlank()) {
                    recipientName = response.data?.dropoff?.contactName.orEmpty()
                }
                if (uploadedPhotoUrl.isNullOrBlank()) {
                    uploadedPhotoUrl = response.data?.proofOfDelivery?.photoUrl
                }
            }
            .onFailure { error ->
                loadError = error.message ?: "Failed to load delivery job"
            }
        isLoading = false
    }

    LaunchedEffect(jobId) {
        loadJob()
    }

    val launchCamera = rememberCameraCapture(
        onImageCaptured = { imageBytes ->
            capturedBytes = imageBytes
            scope.launch {
                isUploadingPhoto = true
                DriverUploadApi.uploadProofImage(imageBytes)
                    .onSuccess { url ->
                        uploadedPhotoUrl = url
                        showMessage("Photo uploaded")
                    }
                    .onFailure { error ->
                        uploadedPhotoUrl = null
                        showMessage(error.message ?: "Photo upload failed")
                    }
                isUploadingPhoto = false
            }
        },
        onDenied = {
            showMessage("Camera permission denied or capture cancelled")
        }
    )

    val previewBitmap = remember(capturedBytes) {
        capturedBytes?.let(::decodeImageBytes)
    }
    val displayOrderId = job?.displayOrderId?.ifBlank { job?.id } ?: jobId
    val earningsLabel = job?.estimatedEarnings?.takeIf { it > 0 }?.let { "EST. RM${it.toInt()}" } ?: ""
    val canSubmit = otpCode.length == RecipientOtpLength && uploadedPhotoUrl != null && !isUploadingPhoto && !isSubmitting

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = PrimaryBlue,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            loadError != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(loadError ?: "Failed to load", color = Color.Red, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { scope.launch { loadJob() } }) {
                        Text("Retry")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "< Back",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = onBack)
                        )
                        if (earningsLabel.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFDCE6FB))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = earningsLabel,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("ORDER ID", color = TextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("#$displayOrderId", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Proof of Drop-off", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 34.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(2.dp, PrimaryBlue, RoundedCornerShape(18.dp))
                            .clickable(enabled = !isUploadingPhoto, onClick = launchCamera),
                        contentAlignment = Alignment.Center
                    ) {
                        if (previewBitmap != null) {
                            Image(
                                bitmap = previewBitmap,
                                contentDescription = "Captured proof photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(78.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDCE6FB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("CAM", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    "Tap to Capture Photo",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Capture the package at the doorstep",
                                    fontSize = 16.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        if (isUploadingPhoto) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = Color.White)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Uploading photo...", color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (previewBitmap != null) "Tap the image to retake it." else "The captured photo replaces this card immediately.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                isRequestingOtp = true
                                DriverJobsApi.requestDeliveryOtp(jobId)
                                    .onSuccess {
                                        showMessage("Recipient OTP requested")
                                    }
                                    .onFailure { error ->
                                        showMessage(error.message ?: "Failed to request recipient OTP")
                                    }
                                isRequestingOtp = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text(if (isRequestingOtp) "Requesting OTP..." else "Request Recipient OTP")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { value ->
                            otpCode = value.filter(Char::isDigit).take(RecipientOtpLength)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Recipient OTP") },
                        supportingText = { Text("Enter the $RecipientOtpLength-digit OTP from the recipient") },
                        keyboardOptions = KeyboardOptions.Default
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Recipient name") }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                isSubmitting = true
                                DriverJobsApi.submitProof(
                                    jobId = jobId,
                                    otpCode = otpCode,
                                    photoUrl = uploadedPhotoUrl,
                                    recipientName = recipientName.ifBlank { null }
                                ).onSuccess { response ->
                                    val updatedJob = response.data
                                    if (updatedJob != null) {
                                        job = updatedJob
                                        onSubmitted(updatedJob)
                                    }
                                    showMessage("Proof of delivery submitted")
                                }.onFailure { error ->
                                    showMessage(error.message ?: "Failed to submit proof")
                                }
                                isSubmitting = false
                            }
                        },
                        enabled = canSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text(if (isSubmitting) "Submitting..." else "Complete Delivery")
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
