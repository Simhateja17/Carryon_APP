package com.company.carryon.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    val categories = listOf("Delivery delay", "Damaged package", "Wrong item", "Payment issue", "Other")
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var description by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFFF7F7F8),
        topBar = {
            TopAppBar(
                title = {
                    Text("Report an Issue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF282B51))
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("←", color = PrimaryBlue, fontSize = 22.sp)
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                        Text("On", color = Color(0xFF282B51), fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED))
                ) {
                    Text("Submit Report  ▷", color = Color(0xFFF1F2FF), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Our support team typically responds within 2 hours.",
                    fontSize = 10.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionLabel("ORDER REFERENCE")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33A6D2F3), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("◈", color = PrimaryBlue, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tracking Number", fontSize = 12.sp, color = Color.Black)
                    Text("#CR-2094", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text("DELIVERED", color = Color(0xFF2F80ED), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            SectionLabel("SELECT ISSUE CATEGORY")
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33A6D2F3), RoundedCornerShape(12.dp))
                        .clickable { expanded = true }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedCategory, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color.Black)
                    Text("⌄", fontSize = 20.sp, color = Color(0xFF6A738B))
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            SectionLabel("DESCRIBE YOUR ISSUE")
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                placeholder = {
                    Text(
                        "Provide as much detail as possible to help us resolve this quickly.",
                        color = Color.Black,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0x33A6D2F3),
                    unfocusedContainerColor = Color(0x33A6D2F3),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel("ATTACH PHOTOS/FILES")
                Text("Max 5MB per file", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AttachmentBox(
                    modifier = Modifier.weight(1f),
                    content = {
                        Text("", color = PrimaryBlue, fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ADD", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                )
                AttachmentBox(
                    modifier = Modifier.weight(1f),
                    background = Color(0x66D9DAFF),
                    content = {
                        Text("Preview", color = Color(0xFF6B7280), fontSize = 12.sp)
                    }
                )
                AttachmentBox(
                    modifier = Modifier.weight(1f),
                    content = {
                        Text("", color = PrimaryBlue, fontSize = 24.sp)
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.2.sp,
        color = Color.Black,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun AttachmentBox(
    modifier: Modifier = Modifier,
    background: Color = Color(0x33A6D2F3),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .height(106.dp)
            .background(background, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x33A6D2F3), RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}
