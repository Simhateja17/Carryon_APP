package com.company.carryon.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.welcome_hero
import org.jetbrains.compose.resources.painterResource
import com.company.carryon.ui.theme.*
import com.company.carryon.i18n.LocalStrings

@Composable
fun WelcomeScreen(
    onCreateAccount: () -> Unit,
    onLogin: () -> Unit
) {
    val strings = LocalStrings.current
    val adaptiveInfo = LocalWindowAdaptiveInfo.current
    val heroWeight = when (adaptiveInfo.heightClass) {
        WindowHeightClass.Compact -> 0.42f
        WindowHeightClass.Medium -> 0.46f
        WindowHeightClass.Expanded -> 0.52f
    }
    if (adaptiveInfo.isLandscape && adaptiveInfo.heightClass == WindowHeightClass.Compact) {
        Row(
            modifier = Modifier.fillMaxSize().background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WelcomeHero(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp),
            )
            WelcomeActions(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp),
                onCreateAccount = onCreateAccount,
                onLogin = onLogin,
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(heroWeight),
            contentAlignment = Alignment.Center
        ) {
            WelcomeHero(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = adaptiveInfo.horizontalPadding,
                        top = if (adaptiveInfo.heightClass == WindowHeightClass.Compact) 8.dp else 24.dp,
                        end = adaptiveInfo.horizontalPadding,
                    ),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(if (adaptiveInfo.widthClass == WindowWidthClass.Compact) 0.86f else 0.75f)
                .weight(1f - heroWeight)
                .padding(top = 20.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            WelcomeActions(
                modifier = Modifier.fillMaxSize(),
                onCreateAccount = onCreateAccount,
                onLogin = onLogin,
            )
        }
    }
}

@Composable
private fun WelcomeHero(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.welcome_hero),
        contentDescription = "Carry On delivery van",
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun WelcomeActions(
    modifier: Modifier,
    onCreateAccount: () -> Unit,
    onLogin: () -> Unit,
) {
    val strings = LocalStrings.current
    val wordmarkFont = MontserratItalicFontFamily()
    val subtitle = strings.welcomeSubtitle.trim()
    val connector = when {
        subtitle.endsWith(" with", ignoreCase = true) -> "with"
        subtitle.endsWith(" dengan", ignoreCase = true) -> "dengan"
        else -> ""
    }
    val firstSubtitleLine = if (connector.isNotEmpty()) {
        subtitle.dropLast(connector.length).trimEnd()
    } else {
        subtitle
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Text(
            text = strings.welcome,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = buildAnnotatedString {
                append(firstSubtitleLine)
                append("\n")
                if (connector.isNotEmpty()) {
                    append(connector)
                    append(" ")
                }
                withStyle(
                    SpanStyle(
                        color = Color(0xFF2F80ED),
                        fontFamily = wordmarkFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-0.02).em,
                    )
                ) { append("CARRY") }
                append("\u2009")
                withStyle(
                    SpanStyle(
                        color = PrimaryBlue,
                        fontFamily = wordmarkFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-0.02).em,
                    )
                ) { append("ON") }
            },
            modifier = Modifier.fillMaxWidth(),
            fontSize = 16.sp,
            color = TextSecondary,
            maxLines = 2,
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onCreateAccount,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        ) {
            Text(
                strings.createAnAccount,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue),
        ) {
            Text(
                strings.logIn,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBlue,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
