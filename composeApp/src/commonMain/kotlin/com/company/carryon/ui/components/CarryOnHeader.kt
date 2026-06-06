package com.company.carryon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import com.company.carryon.ui.theme.MontserratItalicFontFamily

@Composable
fun CarryOnWordmark(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 21.sp
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Color(0xFF2F80ED))) {
                append("CARRY")
            }
            append("\u2009")
            withStyle(SpanStyle(color = Color(0xFF034094))) {
                append("ON")
            }
        },
        modifier = modifier,
        fontFamily = MontserratItalicFontFamily(),
        fontWeight = FontWeight.ExtraBold,
        fontStyle = FontStyle.Italic,
        fontSize = fontSize,
        letterSpacing = (-0.02).em,
        maxLines = 1
    )
}

@Composable
fun CarryOnHeader(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = Color(0xFF1D254B),
    titleWeight: FontWeight = FontWeight.Medium,
    backgroundColor: Color = Color.Transparent,
    showBack: Boolean = true,
    onBack: (() -> Unit)? = null,
    dividerColor: Color? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack && onBack != null) {
                Text(
                    text = "‹",
                    color = titleColor,
                    fontSize = 22.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = title,
                color = titleColor,
                fontSize = 21.sp,
                fontWeight = titleWeight
            )

            Spacer(modifier = Modifier.weight(1f))

            CarryOnWordmark()
        }

        dividerColor?.let { HorizontalDivider(color = it) }
    }
}
