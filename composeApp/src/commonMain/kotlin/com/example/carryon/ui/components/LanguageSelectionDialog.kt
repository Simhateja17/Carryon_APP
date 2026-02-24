package com.example.carryon.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

data class LanguageOption(
    val code: String,
    val englishName: String,
    val nativeName: String
)

val supportedLanguages = listOf(
    LanguageOption("en", "English", "English"),
    LanguageOption("ms", "Malay", "Bahasa Melayu"),
    LanguageOption("ta", "Tamil", "\u0BA4\u0BAE\u0BBF\u0BB4\u0BCD"),
    LanguageOption("zh", "Chinese", "\u4E2D\u6587")
)

fun getLanguageDisplayName(code: String): String {
    return supportedLanguages.find { it.code == code }?.englishName ?: "English"
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String = "",
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val strings = LocalStrings.current
    var selectedCode by remember { mutableStateOf(currentLanguage.ifEmpty { "en" }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = strings.selectYourLanguage,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                supportedLanguages.forEach { lang ->
                    val isSelected = selectedCode == lang.code
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCode = lang.code },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PrimaryBlueSurface else Color(0xFFF8F8F8)
                        ),
                        border = if (isSelected)
                            androidx.compose.foundation.BorderStroke(2.dp, PrimaryBlue)
                        else
                            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lang.englishName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                if (lang.nativeName != lang.englishName) {
                                    Text(
                                        text = lang.nativeName,
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            if (isSelected) {
                                RadioButton(
                                    selected = true,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                                )
                            } else {
                                RadioButton(
                                    selected = false,
                                    onClick = { selectedCode = lang.code },
                                    colors = RadioButtonDefaults.colors(unselectedColor = Color(0xFFBDBDBD))
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onLanguageSelected(selectedCode) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(strings.continueText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}
