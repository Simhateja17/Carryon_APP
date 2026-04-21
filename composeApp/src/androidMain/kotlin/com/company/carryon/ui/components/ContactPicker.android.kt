package com.company.carryon.ui.components

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager

@Composable
actual fun ContactPickerButton(
    onContactSelected: (ContactInfo) -> Unit
) {
    val context = LocalContext.current
    var showPermissionRationale by remember { mutableStateOf(false) }

    val contactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        uri?.let {
            val contact = getContactFromUri(context, it)
            contact?.let { c -> onContactSelected(c) }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            contactLauncher.launch(null)
        } else {
            showPermissionRationale = true
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2F80ED), RoundedCornerShape(12.dp))
            .clickable {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    contactLauncher.launch(null)
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Pick from Contacts",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

    if (showPermissionRationale) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Contacts permission is needed to pick a contact. Please enable it in Settings.",
            color = Color.Red,
            fontSize = 12.sp
        )
    }
}

private fun getContactFromUri(context: Context, uri: android.net.Uri): ContactInfo? {
    val resolver: ContentResolver = context.contentResolver
    val contactProjection = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME
    )

    val (contactId, contactName) = resolver.query(uri, contactProjection, null, null, null)?.use { cursor ->
        if (!cursor.moveToFirst()) return null

        val idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
        val nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
        cursor.getString(idIndex) to cursor.getString(nameIndex).orEmpty()
    } ?: return null

    val phoneProjection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
    val phoneSelection = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
    val phoneSelectionArgs = arrayOf(contactId)

    val phone = resolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        phoneProjection,
        phoneSelection,
        phoneSelectionArgs,
        null
    )?.use { cursor ->
        if (!cursor.moveToFirst()) return null

        val numberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
        cursor.getString(numberIndex)
    } ?: return null

    return ContactInfo(
        name = contactName,
        phone = phone.replace(Regex("[^\\d+]"), "")
    )
}
