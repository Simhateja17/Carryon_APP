package com.company.carryon.ui.screens.wallet

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue

@Composable
fun SendMoneyScreen(
    onBack: () -> Unit
) {
    var recipient by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Logistics Payment") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                color = PrimaryBlue,
                fontSize = 20.sp,
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Financial Hub",
                color = PrimaryBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("TRANSACTION PORTAL", color = Color(0xFF2F5EA8), fontSize = 10.sp, letterSpacing = 1.sp)
        Text("Send money to", color = Color(0xFF0F172A), fontSize = 50.sp, fontWeight = FontWeight.SemiBold)
        Text("anywhere.", color = PrimaryBlue, fontSize = 50.sp, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(12.dp))

        CardBlock {
            Text("Wallet Balance", color = Color(0xFF111827), fontSize = 22.sp)
            Text("RM 12,450.00", color = Color(0xFF111827), fontWeight = FontWeight.Bold, fontSize = 44.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFFE7F0FD), RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("🛡 Verified Account", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        CardBlock {
            Text("Select Recipient", color = Color(0xFF111827), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Field(value = recipient, onValueChange = { recipient = it }, hint = "Name, @username, or bank account", leading = "⌕")

            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Recent Recipients", color = Color(0xFF334155), fontSize = 14.sp)
                Text("View All", color = PrimaryBlue, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                RecipientChip("+", "New")
                RecipientChip("👩", "Sarah L.")
                RecipientChip("👨", "Mark R.")
                RecipientChip("👩", "Elena K.")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        CardBlock {
            Text("Amount", color = Color(0xFF111827), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Field(value = amount, onValueChange = { amount = it }, hint = "RM 0.00")

            Spacer(modifier = Modifier.height(10.dp))
            Text("Category", color = Color(0xFF111827), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF2F7), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFDEE5EF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(category, color = Color(0xFF1F2937), fontSize = 15.sp)
                    Text("⌄", color = Color(0xFF111827))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Note (Optional)", color = Color(0xFF111827), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("What is this for?") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEFF2F7),
                    unfocusedContainerColor = Color(0xFFEFF2F7),
                    focusedBorderColor = Color(0xFFDEE5EF),
                    unfocusedBorderColor = Color(0xFFDEE5EF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF2E74D8), Color(0xFF3E87ED))),
                    RoundedCornerShape(18.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Text("🛡", color = Color.White, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Secure Transfer", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Your funds are protected by 256-bit encryption and real-time fraud monitoring.",
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        CardBlock {
            Text("Transfer Summary", color = Color(0xFF111827), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow("Transaction Fee", "RM 0.00")
            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow("Estimated Arrival", "Instant", rightColor = PrimaryBlue)
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFD5DEE9)))
            Spacer(modifier = Modifier.height(10.dp))
            SummaryRow("Total Amount", "RM ${if (amount.isBlank()) "0.00" else amount}", bold = true, large = true)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Send Payment  >", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "By tapping Send, you agree to our Terms of Service and Privacy Policy.",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                lineHeight = 15.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFF2F7), RoundedCornerShape(16.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomMiniTab("◷", "Payments", true)
            BottomMiniTab("▭", "Methods", false)
            BottomMiniTab("▤", "Invoices", false)
            BottomMiniTab("⚙", "Settings", false)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "← Back",
            color = PrimaryBlue,
            modifier = Modifier.clickable { onBack() }.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun CardBlock(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCE6F1), RoundedCornerShape(22.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
private fun Field(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    leading: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint) },
        leadingIcon = if (leading.isNotBlank()) ({ Text(leading) }) else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEFF2F7),
            unfocusedContainerColor = Color(0xFFEFF2F7),
            focusedBorderColor = Color(0xFFDEE5EF),
            unfocusedBorderColor = Color(0xFFDEE5EF)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun RecipientChip(icon: String, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFEFF2F7), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = PrimaryBlue)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(name, color = Color(0xFF334155), fontSize = 11.sp)
    }
}

@Composable
private fun SummaryRow(
    left: String,
    right: String,
    rightColor: Color = Color(0xFF111827),
    bold: Boolean = false,
    large: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(left, color = Color(0xFF334155), fontSize = if (large) 18.sp else 14.sp)
        Text(
            right,
            color = rightColor,
            fontSize = if (large) 34.sp else 14.sp,
            fontWeight = if (bold || large) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun BottomMiniTab(icon: String, label: String, selected: Boolean) {
    Column(
        modifier = Modifier
            .background(if (selected) Color(0xFFDCE9FF) else Color.Transparent, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, color = if (selected) PrimaryBlue else Color(0xFF94A3B8), fontSize = 16.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, color = if (selected) PrimaryBlue else Color(0xFF94A3B8), fontSize = 12.sp)
    }
}
