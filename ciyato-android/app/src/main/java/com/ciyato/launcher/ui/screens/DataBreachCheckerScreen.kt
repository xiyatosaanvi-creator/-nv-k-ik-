package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * DataBreachCheckerScreen — Suggestion #85
 * Checks if a password has appeared in known data breaches
 * using the HaveIBeenPwned k-anonymity API (only first 5 chars of SHA-1 sent).
 * Zero privacy risk — the full hash never leaves the device.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataBreachCheckerScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<BreachResult?>(null) }
    var error by remember { mutableStateOf("") }

    sealed class BreachResult {
        data class Found(val count: Int) : BreachResult()
        object NotFound : BreachResult()
    }

    suspend fun checkPassword(pw: String): BreachResult? {
        if (pw.isBlank()) return null
        return withContext(Dispatchers.IO) {
            try {
                val sha1 = sha1(pw).uppercase()
                val prefix = sha1.take(5)
                val suffix = sha1.drop(5)
                val url = URL("https://api.pwnedpasswords.com/range/$prefix")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("User-Agent", "Ciyato-Launcher")
                conn.connectTimeout = 10_000
                conn.readTimeout = 10_000
                val responseCode = conn.responseCode
                if (responseCode != 200) return@withContext null
                val lines = BufferedReader(InputStreamReader(conn.inputStream)).readLines()
                val match = lines.firstOrNull { it.startsWith(suffix, ignoreCase = true) }
                if (match != null) {
                    val count = match.substringAfter(":").trim().toIntOrNull() ?: 0
                    BreachResult.Found(count)
                } else {
                    BreachResult.NotFound
                }
            } catch (_: Exception) { null }
        }
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = { Text("Breach Checker", color = CiyatoWhite, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = CiyatoWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = CiyatoBgEl),
                shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, null, tint = CiyatoGold, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Privacy-safe check", color = CiyatoGold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text(
                        "Only the first 5 characters of a SHA-1 hash are sent. Your password never leaves your device.",
                        color = CiyatoMuted, fontSize = 12.sp,
                    )
                }
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; result = null; error = "" },
                label = { Text("Password to check") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null, tint = CiyatoMuted,
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CiyatoGold,
                    focusedLabelColor = CiyatoGold,
                    cursorColor = CiyatoGold,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    isChecking = true
                    result = null
                    error = ""
                    kotlinx.coroutines.GlobalScope.kotlinx.coroutines.launch {
                        val r = checkPassword(password)
                        result = r
                        if (r == null) error = "Could not reach server. Check your connection."
                        isChecking = false
                    }
                },
                enabled = password.isNotBlank() && !isChecking,
                colors = ButtonDefaults.buttonColors(containerColor = CiyatoGold),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isChecking) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Checking…", color = Color.Black)
                } else {
                    Icon(Icons.Default.Search, null, tint = Color.Black)
                    Spacer(Modifier.width(6.dp))
                    Text("Check Password", color = Color.Black, fontWeight = FontWeight.SemiBold)
                }
            }

            result?.let { r ->
                val (bg, icon, title, msg) = when (r) {
                    is BreachResult.Found -> listOf(
                        Color(0xFFF44336).copy(alpha = 0.15f),
                        Icons.Default.Warning,
                        "⚠️ Password Compromised",
                        "This password appeared in ${r.count.toIntFormatted()} known breaches. Change it immediately.",
                    )
                    BreachResult.NotFound -> listOf(
                        Color(0xFF4CAF50).copy(alpha = 0.15f),
                        Icons.Default.CheckCircle,
                        "✅ Password Safe",
                        "Not found in any known breach database.",
                    )
                }
                Card(colors = CardDefaults.cardColors(containerColor = bg as Color),
                    shape = RoundedCornerShape(14.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Icon(icon as androidx.compose.ui.graphics.vector.ImageVector, null,
                            tint = if (r is BreachResult.Found) Color(0xFFF44336) else Color(0xFF4CAF50))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(title as String, color = CiyatoWhite, fontWeight = FontWeight.SemiBold)
                            Text(msg as String, color = CiyatoMuted, fontSize = 13.sp)
                        }
                    }
                }
            }

            if (error.isNotBlank()) {
                Text(error, color = Color(0xFFFF6B6B), fontSize = 13.sp)
            }
        }
    }
}

private fun sha1(text: String): String {
    val md = MessageDigest.getInstance("SHA-1")
    val bytes = md.digest(text.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}

private fun Int.toIntFormatted(): String = "%,d".format(this)
