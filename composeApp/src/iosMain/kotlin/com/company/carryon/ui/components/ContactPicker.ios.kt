package com.company.carryon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import platform.Contacts.CNContact
import platform.ContactsUI.CNContactPickerDelegateProtocol
import platform.ContactsUI.CNContactPickerViewController
import platform.UIKit.UIApplication
import platform.darwin.NSObject

@Composable
actual fun ContactPickerButton(
    onContactSelected: (ContactInfo) -> Unit
) {
    val contactPickerDelegate = remember { ContactPickerDelegate(onContactSelected) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2F80ED), RoundedCornerShape(12.dp))
            .clickable {
                val picker = CNContactPickerViewController()
                picker.delegate = contactPickerDelegate
                val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
                rootViewController?.presentViewController(picker, animated = true, completion = null)
            }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text("📇", fontSize = 18.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Pick from Contacts",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

class ContactPickerDelegate(
    private val onContactSelected: (ContactInfo) -> Unit
) : NSObject(), CNContactPickerDelegateProtocol {

    override fun contactPicker(picker: CNContactPickerViewController, didSelectContact: CNContact) {
        val name = (didSelectContact.givenName + " " + didSelectContact.familyName).trim()
        val phoneNumbers = didSelectContact.phoneNumbers
        val cleanedPhone = if (phoneNumbers.isNotEmpty()) {
            val firstPhone = phoneNumbers.first() as? platform.Contacts.CNLabeledValue
            val phoneNumber = firstPhone?.value as? platform.Contacts.CNPhoneNumber
            phoneNumber?.stringValue?.filter { it.isDigit() || it == '+' } ?: ""
        } else ""

        onContactSelected(ContactInfo(name = name, phone = cleanedPhone))
    }

    override fun contactPickerDidCancel(picker: CNContactPickerViewController) {
        // User cancelled
    }
}
