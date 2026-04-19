package com.company.carryon.ui.components

import androidx.compose.runtime.Composable

data class ContactInfo(
    val name: String,
    val phone: String
)

@Composable
expect fun ContactPickerButton(
    onContactSelected: (ContactInfo) -> Unit
)
