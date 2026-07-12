package com.ciyato.launcher.ui.screens

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.components.CiyatoButton
import com.ciyato.launcher.ui.components.CiyatoStepIndicator
import com.ciyato.launcher.ui.theme.CiyatoBg
import com.ciyato.launcher.ui.theme.CiyatoBgEl
import com.ciyato.launcher.ui.theme.CiyatoBgEl2
import com.ciyato.launcher.ui.theme.CiyatoBorder
import com.ciyato.launcher.ui.theme.CiyatoGold
import com.ciyato.launcher.ui.theme.CiyatoGoldSoft
import com.ciyato.launcher.ui.theme.CiyatoMuted
import com.ciyato.launcher.ui.theme.CiyatoSec
import com.ciyato.launcher.ui.theme.CiyatoStrongBorder
import com.ciyato.launcher.ui.theme.CiyatoSubtleBorder
import com.ciyato.launcher.ui.theme.CiyatoWhite

private data class OnboardingPanel(
    val icon: ImageVector,
    val title: String,
    val body: String,
)

private enum class OnboardingVisual {
    HOME,
    ORGANIZE,
    DRAWER,
    GESTURES,
    PERMISSIONS,
    HANDOFF,
}

private data class OnboardingPage(
    val icon: ImageVector,
    val eyebrow: String,
    val title: String,
    val subtitle: String,
    val body: String,
    val bullets: List<String>,
    val panels: List<OnboardingPanel>,
    /** If true, this page shows the mini phone home preview. Only page 0 should. */
    val showHomePreview: Boolean = false,
    val visual: OnboardingVisual = OnboardingVisual.HOME,
)

// ─── Page definitions ────────────────────────────────────────────────────────
// Page 3 (Screenshot 01): Product purpose — the ONLY slide with a home preview.
// Page 4 (Screenshot 02): Honest first-run state — no fake weather/agenda.
// Page 5 (Screenshot 03): "From chaos to clarity" transformation explanation.
// Page 6 (Screenshot 04): Smart App Library — swipe-up drawer, real categories.
// Page 7 (Screenshot 05): Real gestures — swipe up, long-press, tap category.
// Page 8 (Screenshot 06): Permission gates — SAF, Photo Picker, location.
// Page 9 (Screenshot 07): Final handoff — set as home, configure, or skip.

private val detailedPages = listOf(
    // ── Slide 0 (Page 3): Product purpose with ONE home preview ──
    OnboardingPage(
        icon = Icons.Default.AutoAwesome,
        eyebrow = "AI Phone Organizer",
        title = "From chaos to clarity.",
        subtitle = "Your phone, beautifully organized.",
        body = "Ciyato is a calm, premium Android home screen. It keeps your real apps, real icons, and settings close — while turning clutter into clean categories and faster daily access.",
        bullets = listOf(
            "A cleaner home screen with your real installed apps.",
            "A smart library that groups apps without changing them.",
            "A private organizer that stays entirely on your device.",
        ),
        panels = listOf(
            OnboardingPanel(Icons.Default.Home, "Home first", "Your launcher opens to search, honest setup cards, categories, shortcuts, and your dock."),
            OnboardingPanel(Icons.Default.Apps, "Organized apps", "Work, Social, Finance, Daily, Creativity, Utilities, Travel — easy to scan, easy to edit."),
            OnboardingPanel(Icons.Default.Search, "Fast access", "Search finds your installed apps instantly. Files and photos are available after you grant access."),
        ),
        showHomePreview = true, // Only this slide shows the preview
        visual = OnboardingVisual.HOME,
    ),

    // ── Slide 1 (Page 4/5): How the organization works ──
    OnboardingPage(
        icon = Icons.Default.ViewModule,
        eyebrow = "How it works",
        title = "Scan → Organize → You decide.",
        subtitle = "AI suggests, you stay in control.",
        body = "When you set Ciyato as Home, it reads your installed apps from Android and suggests smart categories. You can edit, move, remove, or create any category. Nothing is permanent — everything is reversible.",
        bullets = listOf(
            "Ciyato scans real installed apps — it never changes or uninstalls them.",
            "AI groups apps into categories like Work, Social, Finance, and more.",
            "You can override any suggestion: move apps, rename categories, or create your own.",
        ),
        panels = listOf(
            OnboardingPanel(Icons.Default.Edit, "Manual control", "Long-press the home screen to enter edit mode and manage your categories, apps, and dock."),
            OnboardingPanel(Icons.Default.Restore, "Fully reversible", "Hiding an app removes it from display — not from your phone. Restore anytime from Settings."),
            OnboardingPanel(Icons.Default.Settings, "Your layout, saved", "Every change you make persists across restarts. Your layout is always yours."),
        ),
        visual = OnboardingVisual.ORGANIZE,
    ),

    // ── Slide 2 (Page 6): Smart App Library — swipe-up drawer ──
    OnboardingPage(
        icon = Icons.Default.Apps,
        eyebrow = "Smart App Library",
        title = "Everything in its place.",
        subtitle = "Swipe up to open your app drawer.",
        body = "The App Library is your organized app drawer. It reads real installed apps through Android, keeps real icons, and respects hidden and removed states. Categories, search, and long-press actions are all built in.",
        bullets = listOf(
            "Swipe up from the home screen to open the app drawer.",
            "Real categories you can edit, create, rename, or remove.",
            "Long-press any app for actions: change category, hide, remove from display, or app info.",
        ),
        visual = OnboardingVisual.DRAWER,
        panels = listOf(
            OnboardingPanel(Icons.Default.Search, "Search apps", "Type a name and launch the matching installed app directly."),
            OnboardingPanel(Icons.Default.KeyboardArrowRight, "Open categories", "Tap a closed category card to expand it. App icons inside become launchable only when the category is open."),
            OnboardingPanel(Icons.Default.Palette, "Edit by long press", "Long-press the home screen to shape your layout. Deeper visual controls live inside the Ciyato app under Settings."),
        ),
    ),

    // ── Slide 3 (Page 7): Real gestures and interactions ──
    OnboardingPage(
        icon = Icons.Default.Home,
        eyebrow = "Gestures & Interactions",
        title = "Three gestures. That's it.",
        subtitle = "Simple, consistent, always available.",
        body = "Ciyato uses standard Android launcher gestures. No floating toolbars, no permanent edit buttons, no debug icons on your home screen. Just clean interactions.",
        bullets = listOf(
            "Swipe up → opens the App drawer with all your organized apps.",
            "Tap a category → expands it to show the apps inside.",
            "Long-press anywhere → enters edit mode for available layout actions.",
        ),
        panels = listOf(
            OnboardingPanel(Icons.Default.KeyboardArrowUp, "Swipe up for Apps", "The app drawer opens with a natural swipe-up gesture, just like other Android launchers."),
            OnboardingPanel(Icons.Default.Apps, "Tap to expand", "Category cards are containers. Tapping one expands it — preview icons on closed cards don't launch apps."),
            OnboardingPanel(Icons.Default.Edit, "Long-press to edit", "Use edit mode to manage category placement, app shortcuts, and dock apps without uninstalling anything."),
        ),
        visual = OnboardingVisual.GESTURES,
    ),

    // ── Slide 4 (Page 8): Permission gates ──
    OnboardingPage(
        icon = Icons.Default.FolderOpen,
        eyebrow = "Privacy & Permissions",
        title = "Real access, never fake claims.",
        subtitle = "You choose what Ciyato can see.",
        body = "Ciyato explains each permission before Android asks. Files use Android's folder picker. Photos use Android Photo Picker. Weather asks for approximate location only when you set up weather, and Agenda asks for calendar access only when you choose to connect it.",
        bullets = listOf(
            "Files: choose one folder before Ciyato can browse local files.",
            "Photos: select the media you want Ciyato to display.",
            "Weather: grant approximate location only while setting up weather.",
            "Calendar: connect only when you want to see real upcoming events.",
        ),
        visual = OnboardingVisual.PERMISSIONS,
        panels = listOf(
            OnboardingPanel(Icons.Default.FolderOpen, "Choose a folder", "Files starts with a clear prompt. Ciyato browses only the folder you pick."),
            OnboardingPanel(Icons.Default.Security, "Private by design", "File and photo access stays local. Ciyato does not upload your content."),
            OnboardingPanel(Icons.Default.CheckCircle, "Store-safe flow", "No broad storage, no full-gallery access, no fine-location, no background location, no permission spam."),
        ),
    ),

    // ── Slide 5 (Page 9): Final setup handoff ──
    OnboardingPage(
        icon = Icons.Default.Lock,
        eyebrow = "Ready to start",
        title = "You stay in control.",
        subtitle = "Set up now, or skip and explore later.",
        body = "Ciyato does not uninstall apps, does not modify APKs, does not upload analytics, and does not require file or photo permissions during setup. You can set it as Home now, skip, or switch back later from Android Settings.",
        bullets = listOf(
            "Set Ciyato as Home only when you are ready.",
            "Open Android Home settings from Ciyato Settings at any time.",
            "Reset layout, onboarding, selected folders, hidden apps, and removed apps from Settings.",
        ),
        panels = listOf(
            OnboardingPanel(Icons.Default.Lock, "On-device", "App lists, categories, preferences, hidden state, and removed state stay on this phone."),
            OnboardingPanel(Icons.Default.Restore, "Reversible", "Hidden and removed apps are display states, not uninstall actions."),
            OnboardingPanel(Icons.Default.Home, "Switch back", "Android's Home app picker is always available from Ciyato Settings."),
        ),
        visual = OnboardingVisual.HANDOFF,
    ),
)

// The full copy above remains as design reference. First-run onboarding is intentionally
// compressed into three visual screens so people can reach their home screen quickly.
private val pages = listOf(
    OnboardingPage(
        icon = Icons.Default.AutoAwesome,
        eyebrow = "CIYATO HOME",
        title = "A calm start.",
        subtitle = "Your apps, easier to reach.",
        body = "Ciyato gives your real Android apps a clearer home, a five-app dock, and an organized App Library.",
        bullets = listOf("Swipe up or tap Apps to open everything.", "Long-press Home to arrange it."),
        panels = emptyList(),
        showHomePreview = true,
        visual = OnboardingVisual.HOME,
    ),
    OnboardingPage(
        icon = Icons.Default.ViewModule,
        eyebrow = "MAKE IT YOURS",
        title = "Organize in seconds.",
        subtitle = "Suggestions first. Your choices always win.",
        body = "Create categories, move shortcuts, and keep only the sections that matter. Every display change is reversible.",
        bullets = listOf("Edit, hide, or remove from display.", "Your layout stays on this device."),
        panels = emptyList(),
        visual = OnboardingVisual.ORGANIZE,
    ),
    OnboardingPage(
        icon = Icons.Default.Lock,
        eyebrow = "READY WHEN YOU ARE",
        title = "Set up with care.",
        subtitle = "Permissions are asked only when a feature needs them.",
        body = "Files use Android's folder picker. Photos use the Photo Picker. Weather asks for foreground location and lets you choose precise or approximate access.",
        bullets = listOf("No background location.", "You can set Ciyato as Home now or skip."),
        panels = emptyList(),
        visual = OnboardingVisual.HANDOFF,
    ),
)

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    var page by remember { mutableIntStateOf(0) }

    val roleRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF050607),
                        CiyatoBg,
                        Color(0xFF111416),
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BrandHeader()
            Spacer(Modifier.height(14.dp))

            AnimatedContent(
                targetState = page,
                label = "onboarding_page",
                modifier = Modifier.weight(1f),
            ) { pageIndex ->
                OnboardingPageContent(page = pages[pageIndex], pageIndex = pageIndex)
            }

            Spacer(Modifier.height(14.dp))
            CiyatoStepIndicator(
                totalSteps = pages.size,
                currentStep = page,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(18.dp))

            if (page < pages.lastIndex) {
                CiyatoButton(
                    text = "Continue",
                    onClick = { page += 1 },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Final slide: primary action is Set as Home
                CiyatoButton(
                    text = "Set Ciyato as Home App",
                    onClick = { requestDefaultLauncher(context, roleRequestLauncher::launch, onDone) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = { if (page > 0) page -= 1 },
                    enabled = page > 0,
                ) {
                    Text("Back", color = if (page > 0) CiyatoSec else CiyatoMuted, fontSize = 13.sp)
                }
                TextButton(onClick = onDone) {
                    Text("Skip for now", color = CiyatoMuted, fontSize = 13.sp)
                }
            }
        }
    }
}

// ─── Brand Header ─────────────────────────────────────────────────────────────
// Clean wordmark only: "Ciyato" + subtitle. No Private beta badge, no C* icon.
@Composable
private fun BrandHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column {
                Text("Ciyato", color = CiyatoWhite, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Text("AI Phone Organizer for Android", color = CiyatoSec, fontSize = 11.sp)
            }
        }
        Text("Setup guide", color = CiyatoSec, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

// ─── Page content ─────────────────────────────────────────────────────────────
// Each page shows: HeroPanel + (MiniPhonePreview OR ProcessExplanation) + GuidancePanels
@Composable
private fun OnboardingPageContent(page: OnboardingPage, pageIndex: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HeroPanel(page = page, pageIndex = pageIndex)

        // Only the first slide (product introduction) shows the home preview.
        // All other slides show focused, unique educational content instead.
        if (page.showHomePreview) {
            MiniPhonePreview()
        } else {
            OnboardingVisualCard(visual = page.visual)
        }

        page.panels.forEach { panel ->
            GuidancePanel(panel = panel)
        }
    }
}

@Composable
private fun OnboardingVisualCard(visual: OnboardingVisual) {
    when (visual) {
        OnboardingVisual.HOME -> Unit
        OnboardingVisual.ORGANIZE -> OrganizationVisual()
        OnboardingVisual.DRAWER -> AppLibraryVisual()
        OnboardingVisual.GESTURES -> GestureVisual()
        OnboardingVisual.PERMISSIONS -> PermissionVisual()
        OnboardingVisual.HANDOFF -> SetupHandoffVisual()
    }
}

@Composable
private fun OnboardingVisualFrame(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoStrongBorder, RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, color = CiyatoWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = CiyatoMuted, fontSize = 12.sp, lineHeight = 18.sp)
        }
        content()
    }
}

@Composable
private fun OrganizationVisual() {
    OnboardingVisualFrame(
        title = "From installed apps to your layout",
        subtitle = "Ciyato suggests a starting point. You decide what stays.",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VisualStep(Icons.Default.Apps, "Installed", "Real apps", Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(20.dp))
            VisualStep(Icons.Default.AutoAwesome, "Suggested", "Categories", Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(20.dp))
            VisualStep(Icons.Default.Edit, "Yours", "Edit anytime", Modifier.weight(1f))
        }
    }
}

@Composable
private fun VisualStep(icon: ImageVector, title: String, detail: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl2)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(icon, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(18.dp))
        Column {
            Text(title, color = CiyatoWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(detail, color = CiyatoMuted, fontSize = 9.sp, maxLines = 1)
        }
    }
}

@Composable
private fun AppLibraryVisual() {
    OnboardingVisualFrame(
        title = "Your neutral app drawer",
        subtitle = "It opens from Home and stays focused on the apps you actually have.",
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(17.dp))
                .background(CiyatoBg)
                .border(1.dp, CiyatoBorder, RoundedCornerShape(17.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CiyatoGold.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Swipe up", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Apps opens with real categories, search, and long-press actions.", color = CiyatoSec, fontSize = 12.sp, lineHeight = 17.sp)
            }
            Text("Apps", color = CiyatoGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GestureVisual() {
    OnboardingVisualFrame(
        title = "The launcher stays out of your way",
        subtitle = "The same three actions work from your everyday Home screen.",
    ) {
        GestureRow(Icons.Default.KeyboardArrowUp, "Swipe up", "Open Apps")
        GestureRow(Icons.Default.Apps, "Tap a category", "Open its contents")
        GestureRow(Icons.Default.Edit, "Long-press", "Enter edit mode")
    }
}

@Composable
private fun GestureRow(icon: ImageVector, gesture: String, outcome: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CiyatoBgEl2)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(19.dp))
        Text(gesture, color = CiyatoWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(outcome, color = CiyatoSec, fontSize = 12.sp)
    }
}

@Composable
private fun PermissionVisual() {
    OnboardingVisualFrame(
        title = "Empty until you choose access",
        subtitle = "Every feature begins with a clear action instead of invented data.",
    ) {
        PermissionRow(Icons.Default.FolderOpen, "Files", "Choose a folder")
        PermissionRow(Icons.Default.AutoAwesome, "Photos", "Select media")
        PermissionRow(Icons.Default.Home, "Weather", "Allow approximate location")
        PermissionRow(Icons.Default.CalendarToday, "Agenda", "Connect calendar")
    }
}

@Composable
private fun PermissionRow(icon: ImageVector, feature: String, action: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(18.dp))
        Text(feature, color = CiyatoWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(action, color = CiyatoSec, fontSize = 12.sp)
    }
}

@Composable
private fun SetupHandoffVisual() {
    OnboardingVisualFrame(
        title = "What happens next",
        subtitle = "You can start with the launcher now and set up optional features later.",
    ) {
        SetupStep("1", "Set Ciyato as Home", "Android will ask you to confirm your preferred Home app.")
        SetupStep("2", "Explore a clean first run", "Weather, files, photos, and agenda stay in honest empty states until you choose access.")
        SetupStep("3", "Personalize when ready", "Long-press Home to edit, or open Ciyato Settings for launcher controls.")
    }
}

@Composable
private fun SetupStep(number: String, title: String, body: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(CiyatoGold.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(number, color = CiyatoGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, color = CiyatoWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(body, color = CiyatoSec, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

// ─── Hero Panel ───────────────────────────────────────────────────────────────
@Composable
private fun HeroPanel(page: OnboardingPage, pageIndex: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        CiyatoBgEl2.copy(alpha = 0.98f),
                        CiyatoBgEl.copy(alpha = 0.95f),
                    )
                )
            )
            .border(1.dp, CiyatoBorder, RoundedCornerShape(26.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(CiyatoGold.copy(alpha = 0.10f))
                    .border(1.dp, CiyatoGold.copy(alpha = 0.18f), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(page.icon, contentDescription = null, tint = CiyatoWhite, modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(page.eyebrow, color = CiyatoSec, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("${pageIndex + 1} of ${pages.size}", color = CiyatoMuted, fontSize = 11.sp)
            }
        }

        Text(
            page.title,
            color = CiyatoWhite,
            fontSize = 29.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(page.subtitle, color = CiyatoGoldSoft, fontSize = 15.sp, lineHeight = 21.sp)
        Text(page.body, color = CiyatoSec, fontSize = 13.sp, lineHeight = 20.sp)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            page.bullets.forEach { bullet ->
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                    Text(bullet, color = CiyatoSec, fontSize = 12.sp, lineHeight = 18.sp)
                }
            }
        }
    }
}

// ─── Mini Phone Preview ───────────────────────────────────────────────────────
// Shown ONLY on the first onboarding slide. Uses honest first-run states:
// Weather says "Allow location or set city", Today says "Connect calendar or add item".
// No fake temperature, no fake meetings, no fake data of any kind.
@Composable
private fun MiniPhonePreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(CiyatoBg)
            .border(1.dp, CiyatoStrongBorder, RoundedCornerShape(26.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Status bar
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("9:30", color = CiyatoSec, fontSize = 11.sp)
            Text("100%", color = CiyatoSec, fontSize = 11.sp)
        }
        // Header — clean, no magic wand or settings icons
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text("Ciyato Home", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("A clearer home screen", color = CiyatoMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(CiyatoBgEl2)
                .border(1.dp, CiyatoBorder, RoundedCornerShape(15.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Search apps...", color = CiyatoMuted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Icon(Icons.Default.Search, contentDescription = null, tint = CiyatoSec, modifier = Modifier.size(18.dp))
            }
        }

        // Honest empty-state widgets: Weather and Today
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            PreviewWidget("Weather", "Allow location or set city", Modifier.weight(1f))
            PreviewWidget("Today", "Connect calendar or add item", Modifier.weight(1f))
        }

        // Category preview
        Text("Smart categories", color = CiyatoWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    repeat(3) { col ->
                        PreviewCategory(index = row * 3 + col, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        PreviewDock()
    }
}

@Composable
private fun PreviewWidget(title: String, body: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(92.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF26313A), CiyatoBgEl)))
            .border(1.dp, CiyatoBorder, RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, color = CiyatoWhite, fontSize = 21.sp, fontWeight = FontWeight.Medium)
        Text(body, color = CiyatoSec, fontSize = 10.sp, lineHeight = 14.sp)
    }
}

@Composable
private fun PreviewCategory(index: Int, modifier: Modifier = Modifier) {
    val labels = listOf("Work", "Social", "Finance", "Creative", "Utilities", "Daily")
    Column(
        modifier = modifier
            .height(78.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl2.copy(alpha = 0.9f))
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(16.dp))
            .padding(9.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(labels[index], color = CiyatoWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text("Organized apps", color = CiyatoMuted, fontSize = 9.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(3) { dot ->
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (dot == 0) CiyatoGold.copy(0.95f) else CiyatoSec.copy(0.35f))
                )
            }
        }
    }
}

@Composable
private fun PreviewDock() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CiyatoBgEl2)
            .border(1.dp, CiyatoBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (index == 0) CiyatoGold else CiyatoWhite.copy(alpha = 0.82f))
            )
        }
    }
}

// ─── Guidance Panel ───────────────────────────────────────────────────────────
@Composable
private fun GuidancePanel(panel: OnboardingPanel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CiyatoBgEl.copy(alpha = 0.92f))
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(18.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CiyatoGold.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(panel.icon, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(panel.title, color = CiyatoWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(panel.body, color = CiyatoSec, fontSize = 12.sp, lineHeight = 18.sp)
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
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        fallback()
    } catch (e: Exception) {
        fallback()
    }
}
