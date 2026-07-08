package com.ciyato.launcher.ui.screens

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.ui.components.*

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val body: String,
)

private val pages = listOf(
    OnboardingPage(
        icon = Icons.Default.Star,
        title = "Welcome to Ciyato",
        subtitle = "Your Android, organized beautifully.",
        body = "One place for everything. Smarter categories. Faster access. More you.",
    ),
    OnboardingPage(
        icon = Icons.Default.Apps,
        title = "Smart App Library",
        subtitle = "Ciyato organizes your apps automatically.",
        body = "Your installed apps are grouped into smart categories — Work, Social, Finance, and more. Your apps stay on your phone, unchanged. Ciyato only changes how you see them.",
    ),
    OnboardingPage(
        icon = Icons.Default.Lock,
        title = "Private by Design",
        subtitle = "Your data stays on your device.",
        body = "Ciyato reads your installed app list only to build your launcher. Nothing is uploaded. No analytics. No ads. 100% on-device.",
    ),
)

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    var page by remember { mutableIntStateOf(0) }

    val roleRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Whether or not user selected Ciyato, proceed to home
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(CiyatoBg, CiyatoBgEl2))
            )
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))

            // Ciyato Logo
            Text(
                "C✦",
                color = CiyatoGold,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
            )
            Text("Ciyoto", color = CiyatoWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("AI Phone Organizer for Android", color = CiyatoSec, fontSize = 13.sp)

            Spacer(Modifier.height(48.dp))

            // Page content
            AnimatedContent(targetState = page, label = "onboarding_page") { p ->
                val pg = pages[p]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(CiyatoGold.copy(alpha = 0.15f))
                    ) {
                        Icon(pg.icon, contentDescription = null,
                            tint = CiyatoGold, modifier = Modifier.size(40.dp))
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(pg.title, color = CiyatoWhite, fontSize = 22.sp,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(pg.subtitle, color = CiyatoGoldSoft, fontSize = 14.sp,
                        textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Text(pg.body, color = CiyatoSec, fontSize = 14.sp,
                        textAlign = TextAlign.Center, lineHeight = 22.sp)
                }
            }

            Spacer(Modifier.weight(1f))

            // Page dots
            CiyatoStepIndicator(
                totalSteps = pages.size,
                currentStep = page,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(32.dp))

            // Action button
            if (page < pages.size - 1) {
                CiyatoButton(
                    text = "Next",
                    onClick = { page++ },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Final page — trigger launcher selection
                CiyatoButton(
                    text = "Set Ciyato as Home App",
                    onClick = { requestDefaultLauncher(context, roleRequestLauncher::launch, onDone) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onDone) {
                    Text("Skip for now", color = CiyatoMuted, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Request HOME role via RoleManager (API 29+) or open Default Apps settings. */
fun requestDefaultLauncher(
    context: Context,
    launchIntent: (Intent) -> Unit,
    fallback: () -> Unit,
) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
            if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                launchIntent(roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME))
                return
            }
        }
        // Fallback: open Default Apps settings
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        fallback()
    } catch (e: Exception) {
        fallback()
    }
}
