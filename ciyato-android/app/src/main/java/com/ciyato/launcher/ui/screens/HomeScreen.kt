package com.ciyato.launcher.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.view.WindowManager
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.FocusSessionManager
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.components.*
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

// Automatic Home content is limited to the approved six. The remaining
// classifications are available in the App Library or through manual placement.
private val APPROVED_HOME_CATEGORIES = listOf(
    AppCategory.WORK,
    AppCategory.SOCIAL,
    AppCategory.FINANCE,
    AppCategory.CREATIVITY,
    AppCategory.UTILITIES,
    AppCategory.DAILY,
)

private val WORKSPACE_CATEGORY_CHOICES = listOf(
    AppCategory.WORK,
    AppCategory.SOCIAL,
    AppCategory.FINANCE,
    AppCategory.CREATIVITY,
    AppCategory.UTILITIES,
    AppCategory.DAILY,
    AppCategory.ENTERTAINMENT,
    AppCategory.PRODUCTIVITY,
    AppCategory.COMMUNICATION,
    AppCategory.GAMES,
    AppCategory.TRAVEL,
    AppCategory.SHOPPING,
    AppCategory.AI,
    AppCategory.VIDEO_EDITING,
    AppCategory.CONTACTS,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit = {},
    onOpenSystemWallpaper: () -> Unit = {},
    onOpenOrganizerSettings: () -> Unit = {},
    onCategoryTap: (AppCategory) -> Unit = {},
    onWeatherTap: () -> Unit = {},
    onAgendaTap: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val apps              by viewModel.apps.collectAsState()
    val isLoading         by viewModel.isLoading.collectAsState()
    val denseLayout       by viewModel.denseLayout.collectAsState()
    val showSmartCategories by viewModel.smartCategories.collectAsState()
    val goldAccentEnabled by viewModel.goldAccent.collectAsState()
    val homeTipDismissed by viewModel.homeTipDismissed.collectAsState()
    val dockPackages by viewModel.dockPackages.collectAsState()
    val page0Apps by viewModel.page0Apps.collectAsState()
    val page2Apps by viewModel.page2Apps.collectAsState()
    val workspaceCount by viewModel.workspaceCount.collectAsState()
    val workspaceApps by viewModel.workspaceApps.collectAsState()
    val workspaceCategories by viewModel.workspaceCategories.collectAsState()
    val toastEvent        by viewModel.toastEvent.collectAsState()
    val weatherState      by viewModel.weatherState.collectAsState()
    val timeAwareLayout   by viewModel.timeAwareLayout.collectAsState()
    val hapticEnabled     by viewModel.hapticFeedback.collectAsState()
    val showRecentLaunched by viewModel.showRecentlyLaunched.collectAsState()
    val showHomeGreeting by viewModel.showHomeGreeting.collectAsState()
    val showHomeSearch by viewModel.showHomeSearch.collectAsState()
    val showHomeWeather by viewModel.showHomeWeather.collectAsState()
    val showHomeAgenda by viewModel.showHomeAgenda.collectAsState()
    val showAppDrawer by viewModel.showAppDrawer.collectAsState()
    val workspaceTransition by viewModel.workspaceTransition.collectAsState()
    val hiddenHomeCategories by viewModel.hiddenHomeCategories.collectAsState()
    val privacyMode       by viewModel.privacyMode.collectAsState()
    val screenshotBlocked by viewModel.screenshotBlocked.collectAsState()
    val focusSession      by FocusSessionManager.activeSession.collectAsState()
    val activeAccent = if (goldAccentEnabled) CiyatoGold else CiyatoBlue

    val haptic = LocalHapticFeedback.current
    val view = LocalView.current

    var contextMenuApp by remember { mutableStateOf<InstalledApp?>(null) }
    var isEditMode by remember { mutableStateOf(false) }
    var showLauncherControls by remember { mutableStateOf(false) }
    var draggingCategory by remember { mutableStateOf<String?>(null) }
    var categoryDragOffset by remember { mutableStateOf(Offset.Zero) }
    var workspaceDraggingCategory by remember { mutableStateOf<String?>(null) }
    var workspaceCategoryDragOffset by remember { mutableStateOf(Offset.Zero) }
    var workspaceDraggingApp by remember { mutableStateOf<String?>(null) }
    var workspaceAppDragOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedCustomCategory by remember { mutableStateOf<String?>(null) }
    var categoryPendingDelete by remember { mutableStateOf<String?>(null) }
    var upwardDrag by remember { mutableFloatStateOf(0f) }

    BackHandler(enabled = isEditMode) { isEditMode = false }

    // Custom categories & order
    val customCats by viewModel.customCategories.collectAsState()
    val customCategoryIcons by viewModel.customCategoryIcons.collectAsState()
    val customCatsList = remember(customCats) {
        customCats.split(",").map(String::trim).filter(String::isNotEmpty)
    }

    val categoryOrderVal by viewModel.categoryOrder.collectAsState()
    val categoryTilesSizesVal by viewModel.categoryTilesSizes.collectAsState()

    // Dialog state for creating a custom category
    var showCreateCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryIcon by remember { mutableStateOf("folder") }

    // Dialog state for picking apps for custom pages
    var showPageAppPicker by remember { mutableStateOf(false) }
    var pickerPageIndex by remember { mutableStateOf(0) }
    var showWorkspaceCategoryPicker by remember { mutableStateOf(false) }
    var workspaceCategoryPickerIndex by remember { mutableStateOf(0) }
    var workspaceForNewCategory by remember { mutableStateOf<Int?>(null) }
    var showHomeCategoryPicker by remember { mutableStateOf(false) }

    // ── Live clock ─────────────────────────────────────────────────────────────
    var liveClock by remember { mutableStateOf(currentTimeString()) }
    LaunchedEffect(Unit) {
        while (true) {
            liveClock = currentTimeString()
            delay(1_000L)
        }
    }

    val dateStr = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }

    // ── Dock apps ─────────────────────────────────────────────────────────────
    val dockApps = remember(apps, dockPackages) {
        val byPkg = apps.associateBy { it.packageName }
        val pinned = dockPackages.split(",").map(String::trim).filter(String::isNotEmpty)
        val configured = pinned
            .mapNotNull { byPkg[it] }
            .distinctBy { it.packageName }
            .take(5)
        if (configured.isNotEmpty()) configured else apps.take(5)
    }

    // ── Smart & Custom Categories ─────────────────────────────────────────────
    val displayCategories = remember(apps, timeAwareLayout, focusSession, customCatsList, hiddenHomeCategories) {
        val timeCats = if (timeAwareLayout) {
            viewModel.timeAwareCategories().filter { it in APPROVED_HOME_CATEGORIES }
        } else {
            APPROVED_HOME_CATEGORIES
        }
        val bedtimeHide = viewModel.isBedtimeNow()
        val allVisible = (timeCats + APPROVED_HOME_CATEGORIES).distinct()

        val standard = allVisible.filter { cat ->
            val hasApps = viewModel.byCategory(cat).isNotEmpty()
            val notBlocked = !FocusSessionManager.isBlocked(cat)
            val notBedtime = !bedtimeHide || cat !in listOf(AppCategory.SOCIAL, AppCategory.ENTERTAINMENT, AppCategory.GAMES)
            hasApps && notBlocked && notBedtime && cat.name !in hiddenHomeCategories.split(",")
        }.map { it.name }

        val custom = customCatsList.filter { name ->
            isEditMode || viewModel.byCustomCategory(name).isNotEmpty()
        }

        (standard + custom)
    }

    val orderedCategories = remember(displayCategories, categoryOrderVal) {
        val order = categoryOrderVal.split(",").map(String::trim).filter(String::isNotEmpty)
        if (order.isEmpty()) {
            displayCategories
        } else {
            val sorted = displayCategories.filter { it in order }.sortedBy { order.indexOf(it) }
            val remaining = displayCategories.filter { it !in order }
            sorted + remaining
        }
    }

    // ── Recently launched ─────────────────────────────────────────────────────
    val recentApps = remember(apps) { viewModel.getRecentlyLaunchedApps() }

    // ── Duplicate apps ────────────────────────────────────────────────────────
    // ── Toast / snackbar ──────────────────────────────────────────────────────
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(toastEvent) {
        toastEvent?.consume()?.let { msg ->
            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            snackbarHostState.showSnackbar(msg)
        }
    }

    // ── Layout variables ───────────────────────────────────────────────────────
    val columns        = if (denseLayout) 3 else 2
    val cardHeight: Dp = if (denseLayout) 114.dp else 142.dp
    val categoryMoveThresholdPx = with(LocalDensity.current) { cardHeight.toPx() * 0.52f }
    val spacing        = if (denseLayout) 14.dp else 22.dp
    val topPad         = if (denseLayout) 20.dp else 36.dp
    val greetingSize   = if (denseLayout) 22.sp else 28.sp

    // ── Wallpaper & Background mode ───────────────────────────────────────────
    val useSystemWallpaper by viewModel.useSystemWallpaper.collectAsState()
    val backgroundModifier = if (useSystemWallpaper) {
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
    } else {
        Modifier
            .fillMaxSize()
            .background(CiyatoBg)
    }

    // ── Pager state for swiping screens ──────────────────────────────────────
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { workspaceCount.coerceIn(3, 10) },
    )
    val workspaceScope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.Transparent, // Let system wallpaper or custom background show
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { scaffoldPadding ->
        Box(
            modifier = backgroundModifier.pointerInput(isEditMode) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, amount ->
                        if (amount < 0f) upwardDrag += amount
                    },
                    onDragEnd = {
                        if (!isEditMode && showAppDrawer && upwardDrag < -72f) onOpenDrawer()
                        upwardDrag = 0f
                    },
                    onDragCancel = { upwardDrag = 0f },
                )
            },
        ) {

            // Swipable layout area
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val pageOffset = (pagerState.currentPage - page).toFloat()
                val workspaceTransitionModifier = when (workspaceTransition) {
                    "fade" -> Modifier.graphicsLayer {
                        alpha = 1f - abs(pageOffset).coerceIn(0f, 1f) * 0.55f
                    }
                    "scale" -> Modifier.graphicsLayer {
                        val scale = 1f - abs(pageOffset).coerceIn(0f, 1f) * 0.06f
                        scaleX = scale
                        scaleY = scale
                        alpha = 1f - abs(pageOffset).coerceIn(0f, 1f) * 0.25f
                    }
                    else -> Modifier
                }
                when (page) {
                    0, 2, 3, 4, 5, 6, 7, 8, 9 -> {
                        val pageIndex = page
                        val pageApps = remember(apps, pageIndex, page0Apps, page2Apps, workspaceApps) {
                            viewModel.getAppsForPage(pageIndex)
                        }
                        val pageCategoryKeys = remember(pageIndex, workspaceCategories) {
                            viewModel.getCategoriesForWorkspace(pageIndex)
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 20.dp, end = 20.dp,
                                top = scaffoldPadding.calculateTopPadding() + topPad + 20.dp,
                                bottom = 140.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .then(workspaceTransitionModifier)
                                .combinedClickable(
                                    onClick = {},
                                    onLongClick = {
                                        if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isEditMode = true
                                        showLauncherControls = true
                                    }
                                )
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Workspace ${if (pageIndex == 0) 1 else pageIndex}",
                                        color = CiyatoWhite,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp
                                    )
                                    if (isEditMode) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            if (pageIndex == workspaceCount - 1 && workspaceCount < 10) {
                                                TextButton(onClick = viewModel::addWorkspace) {
                                                    Text("+ Workspace", color = CiyatoSec, fontSize = 12.sp)
                                                }
                                            }
                                            if (pageIndex >= 3 && pageIndex == workspaceCount - 1) {
                                                TextButton(onClick = viewModel::removeLastWorkspace) {
                                                    Text("Remove", color = CiyatoRed, fontSize = 12.sp)
                                                }
                                            }
                                            TextButton(
                                                onClick = {
                                                    workspaceCategoryPickerIndex = pageIndex
                                                    showWorkspaceCategoryPicker = true
                                                }
                                            ) {
                                                Text("+ Category", color = CiyatoSec, fontSize = 12.sp)
                                            }
                                            TextButton(
                                                onClick = {
                                                    pickerPageIndex = pageIndex
                                                    showPageAppPicker = true
                                                }
                                            ) {
                                                Text("+ Add App", color = CiyatoGold, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            if (pageCategoryKeys.isNotEmpty()) {
                                item {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Categories", color = CiyatoSec, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            items(pageCategoryKeys, key = { it }) { categoryKey ->
                                                val standardCategory = runCatching { AppCategory.valueOf(categoryKey) }.getOrNull()
                                                val categoryApps = if (standardCategory != null) {
                                                    viewModel.byCategory(standardCategory)
                                                } else {
                                                    viewModel.byCustomCategory(categoryKey)
                                                }
                                                val isWorkspaceCategoryDragging = workspaceDraggingCategory == "$pageIndex:$categoryKey"
                                                Box(
                                                    modifier = Modifier
                                                        .width(132.dp)
                                                        .graphicsLayer {
                                                            if (isWorkspaceCategoryDragging) {
                                                                translationX = workspaceCategoryDragOffset.x
                                                                translationY = workspaceCategoryDragOffset.y
                                                                alpha = 0.9f
                                                                scaleX = 1.04f
                                                                scaleY = 1.04f
                                                            }
                                                        }
                                                        .pointerInput(pageIndex, categoryKey, isEditMode, workspaceCount) {
                                                            if (isEditMode) {
                                                                detectDragGesturesAfterLongPress(
                                                                    onDragStart = {
                                                                        workspaceDraggingCategory = "$pageIndex:$categoryKey"
                                                                        workspaceCategoryDragOffset = Offset.Zero
                                                                    },
                                                                    onDragCancel = {
                                                                        workspaceDraggingCategory = null
                                                                        workspaceCategoryDragOffset = Offset.Zero
                                                                    },
                                                                    onDragEnd = {
                                                                        workspaceDraggingCategory = null
                                                                        workspaceCategoryDragOffset = Offset.Zero
                                                                    },
                                                                    onDrag = { _, dragAmount ->
                                                                        workspaceCategoryDragOffset += dragAmount
                                                                        val destination = when {
                                                                            workspaceCategoryDragOffset.x > 132f && pageIndex >= 2 && pageIndex + 1 < workspaceCount -> pageIndex + 1
                                                                            workspaceCategoryDragOffset.x < -132f && pageIndex == 2 -> 0
                                                                            workspaceCategoryDragOffset.x < -132f && pageIndex > 2 -> pageIndex - 1
                                                                            workspaceCategoryDragOffset.x > 132f && pageIndex == 0 -> 2
                                                                            else -> null
                                                                        }
                                                                        if (destination != null) {
                                                                            viewModel.moveCategoryBetweenWorkspaces(pageIndex, destination, categoryKey)
                                                                            workspaceScope.launch { pagerState.animateScrollToPage(destination) }
                                                                            workspaceDraggingCategory = null
                                                                            workspaceCategoryDragOffset = Offset.Zero
                                                                        }
                                                                    },
                                                                )
                                                            }
                                                        },
                                                ) {
                                                    SmartCategoryCard(
                                                        category = standardCategory ?: AppCategory.CUSTOM,
                                                        displayName = standardCategory?.let(viewModel::getCategoryDisplayName) ?: categoryKey,
                                                        apps = categoryApps,
                                                        onTap = {
                                                            if (standardCategory != null) onCategoryTap(standardCategory)
                                                            else selectedCustomCategory = categoryKey
                                                        },
                                                        customIcon = if (standardCategory == null) {
                                                            viewModel.getCustomCategoryIcon(categoryKey)
                                                        } else {
                                                            "folder"
                                                        },
                                                        tileSize = "small",
                                                        isEditMode = isEditMode,
                                                        onMoveLeft = { viewModel.moveCategoryInWorkspace(pageIndex, categoryKey, -1) },
                                                        onMoveRight = { viewModel.moveCategoryInWorkspace(pageIndex, categoryKey, 1) },
                                                        onDelete = { viewModel.removeCategoryFromWorkspace(pageIndex, categoryKey) },
                                                        modifier = Modifier.fillMaxWidth(),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (pageApps.isEmpty() && pageCategoryKeys.isEmpty()) {
                                item {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 60.dp)
                                    ) {
                                        Text(
                                            text = if (isEditMode) "Tap '+ Add App' to pin shortcuts here." else "Long press to enter Edit Mode and add app shortcuts.",
                                            color = CiyatoMuted,
                                            textAlign = TextAlign.Center,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            } else {
                                val rows = pageApps.chunked(4)
                                items(rows) { rowApps ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        rowApps.forEach { app ->
                                            val workspaceAppKey = "$pageIndex:${app.packageName}"
                                            val isWorkspaceAppDragging = workspaceDraggingApp == workspaceAppKey
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .graphicsLayer {
                                                        if (isWorkspaceAppDragging) {
                                                            translationX = workspaceAppDragOffset.x
                                                            translationY = workspaceAppDragOffset.y
                                                            alpha = 0.9f
                                                            scaleX = 1.05f
                                                            scaleY = 1.05f
                                                        }
                                                    }
                                                    .pointerInput(pageIndex, app.packageName, isEditMode, workspaceCount) {
                                                        if (isEditMode) {
                                                            detectDragGesturesAfterLongPress(
                                                                onDragStart = {
                                                                    workspaceDraggingApp = workspaceAppKey
                                                                    workspaceAppDragOffset = Offset.Zero
                                                                },
                                                                onDragCancel = {
                                                                    workspaceDraggingApp = null
                                                                    workspaceAppDragOffset = Offset.Zero
                                                                },
                                                                onDragEnd = {
                                                                    workspaceDraggingApp = null
                                                                    workspaceAppDragOffset = Offset.Zero
                                                                },
                                                                onDrag = { _, dragAmount ->
                                                                    workspaceAppDragOffset += dragAmount
                                                                    val destination = when {
                                                                        workspaceAppDragOffset.x > 88f && pageIndex >= 2 && pageIndex + 1 < workspaceCount -> pageIndex + 1
                                                                        workspaceAppDragOffset.x < -88f && pageIndex == 2 -> 0
                                                                        workspaceAppDragOffset.x < -88f && pageIndex > 2 -> pageIndex - 1
                                                                        workspaceAppDragOffset.x > 88f && pageIndex == 0 -> 2
                                                                        else -> null
                                                                    }
                                                                    if (destination != null) {
                                                                        viewModel.moveAppBetweenWorkspaces(pageIndex, destination, app.packageName)
                                                                        workspaceScope.launch { pagerState.animateScrollToPage(destination) }
                                                                        workspaceDraggingApp = null
                                                                        workspaceAppDragOffset = Offset.Zero
                                                                    }
                                                                },
                                                            )
                                                        }
                                                    },
                                                contentAlignment = Alignment.TopEnd,
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .combinedClickable(
                                                            onClick = { viewModel.launchApp(app) },
                                                            onLongClick = { if (!isEditMode) contextMenuApp = app }
                                                        )
                                                        .padding(8.dp)
                                                ) {
                                                    RealAppIcon(
                                                        drawable = app.icon,
                                                        size = 52.dp,
                                                        cornerRadius = 14.dp,
                                                        scale = app.iconScale,
                                                        rotation = app.iconRotation,
                                                        accentHex = app.iconAccent,
                                                    )
                                                    Text(
                                                        text = app.label,
                                                        color = CiyatoWhite,
                                                        fontSize = 11.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                if (isEditMode) {
                                                    Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier
                                                            .size(20.dp)
                                                            .clip(CircleShape)
                                                            .background(CiyatoRed)
                                                            .clickable {
                                                                viewModel.removeAppFromPage(pageIndex, app.packageName)
                                                            }
                                                    ) {
                                                        Text("✕", color = CiyatoWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                        repeat(4 - rowApps.size) { Spacer(Modifier.weight(1f)) }
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // Main home screen
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start  = 16.dp, end = 16.dp,
                                top    = scaffoldPadding.calculateTopPadding() + topPad,
                                bottom = 140.dp,
                            ),
                            verticalArrangement = Arrangement.spacedBy(spacing),
                            modifier = Modifier
                                .fillMaxSize()
                                .then(workspaceTransitionModifier)
                                .combinedClickable(
                                    onClick = {},
                                    onLongClick = {
                                        if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isEditMode = true
                                        showLauncherControls = true
                                    }
                                ),
                        ) {

                            // 1. Clock + Greeting
                            if (showHomeGreeting) item {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            if (!privacyMode) viewModel.greeting else "Welcome back",
                                            color = CiyatoWhite,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = greetingSize,
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(dateStr, color = CiyatoSec, fontSize = if (denseLayout) 12.sp else 13.sp)
                                            AnimatedContent(
                                                targetState = liveClock,
                                                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                                                label = "clock",
                                            ) { time ->
                                                Text("· $time", color = CiyatoMuted, fontSize = if (denseLayout) 12.sp else 13.sp)
                                            }
                                        }
                                    }
                                    if (focusSession != null) {
                                        FocusBadge(focusSession!!)
                                    }
                                }
                                if (isEditMode) HomeSectionRemoveButton(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    onClick = { viewModel.setShowHomeGreeting(false) },
                                )
                                }
                            }

                            // 2. Search bar
                            if (showHomeSearch) item {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    HomeSearchBar(
                                        isDense = denseLayout,
                                        onClick = onOpenSearch,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    if (isEditMode) HomeSectionRemoveButton(
                                        modifier = Modifier.align(Alignment.TopEnd),
                                        onClick = { viewModel.setShowHomeSearch(false) },
                                    )
                                }
                            }

                            if (!homeTipDismissed && !isEditMode) {
                                item {
                                    CiyatoTipBanner(
                                        text = "Swipe up for Apps. Long-press empty space to enter Edit mode and arrange your layout.",
                                        onDismiss = viewModel::dismissHomeTip,
                                        actionLabel = "Got it",
                                        onAction = viewModel::dismissHomeTip,
                                        accentColor = activeAccent
                                    )
                                }
                            }

                            // 4. Weather + Agenda row
                            if (showHomeWeather || showHomeAgenda) item {
                                WeatherAgendaRow(
                                    isDense      = denseLayout,
                                    weatherState = if (privacyMode) null else weatherState,
                                    showWeather  = showHomeWeather,
                                    showAgenda   = showHomeAgenda,
                                    isEditMode   = isEditMode,
                                    onWeatherTap = {
                                        if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onWeatherTap()
                                    },
                                    onAgendaTap  = onAgendaTap,
                                    onRemoveWeather = { viewModel.setShowHomeWeather(false) },
                                    onRemoveAgenda = { viewModel.setShowHomeAgenda(false) },
                                    modifier     = Modifier.fillMaxWidth(),
                                )
                            }

                            // 5. Recently launched
                            if (showRecentLaunched && recentApps.isNotEmpty() && !privacyMode) {
                                item {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "Recent",
                                                color = CiyatoWhite,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = if (denseLayout) 14.sp else 16.sp,
                                            )
                                        }
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            items(recentApps.take(8), key = { it.packageName }) { app ->
                                                AppIconTile(app = app, iconSize = if (denseLayout) 44.dp else 50.dp,
                                                    onClick = {
                                                        if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        viewModel.launchApp(app)
                                                    },
                                                    onLongClick = {
                                                        if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        contextMenuApp = app
                                                    },
                                                    modifier = Modifier.width(if (denseLayout) 56.dp else 62.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            // 6. Smart categories header
                            if (showSmartCategories) {
                                item {
                                    Row(modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically) {
                                        Row(verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(
                                                if (timeAwareLayout) "Right now" else "Smart categories",
                                                color = CiyatoWhite, fontWeight = FontWeight.SemiBold,
                                                fontSize = if (denseLayout) 17.sp else 20.sp,
                                            )
                                            if (focusSession != null) {
                                                Box(Modifier.clip(RoundedCornerShape(6.dp))
                                                    .background(CiyatoGold.copy(0.15f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                                                    Text("Focus", color = CiyatoGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        // Edit controls only visible when in edit mode (entered via long-press)
                                        if (isEditMode) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                TextButton(
                                                    onClick = { showCreateCategoryDialog = true },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text("+ New Category", color = CiyatoGold, fontSize = 13.sp)
                                                }
                                                TextButton(
                                                    onClick = { showHomeCategoryPicker = true },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text("+ Category", color = CiyatoSec, fontSize = 13.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                // 7. Category grid
                                item {
                                    if (isLoading) {
                                        SkeletonCategoryGrid(columns = columns, rows = 2, cardHeight = cardHeight)
                                    } else if (orderedCategories.isEmpty()) {
                                        Text("No apps found", color = CiyatoMuted, modifier = Modifier.padding(16.dp))
                                    } else {
                                        val rows = (orderedCategories.size + columns - 1) / columns
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                        ) {
                                            orderedCategories.chunked(columns).forEach { rowCats ->
                                                Row(modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                    rowCats.forEach { catKey ->
                                                        // Resolve if standard enum or custom category
                                                        val standardCat = runCatching { AppCategory.valueOf(catKey) }.getOrNull()
                                                        val displayName = if (standardCat != null) {
                                                            viewModel.getCategoryDisplayName(standardCat)
                                                        } else {
                                                            catKey
                                                        }
                                                        val catApps = if (standardCat != null) {
                                                            viewModel.byCategory(standardCat)
                                                        } else {
                                                            viewModel.byCustomCategory(catKey)
                                                        }

                                                        val tileSize = viewModel.getCategoryTileSize(catKey)
                                                        val cardWeight = when (tileSize) {
                                                            "large" -> 2f
                                                            else -> 1f
                                                        }
                                                        var categoryCardWidth by remember(catKey) { mutableFloatStateOf(0f) }
                                                        val isCategoryDragging = draggingCategory == catKey

                                                        Box(
                                                            modifier = Modifier
                                                                .weight(cardWeight)
                                                                .onSizeChanged { categoryCardWidth = it.width.toFloat() }
                                                                .graphicsLayer {
                                                                    if (isCategoryDragging) {
                                                                        translationX = categoryDragOffset.x
                                                                        translationY = categoryDragOffset.y
                                                                        alpha = 0.9f
                                                                        scaleX = 1.03f
                                                                        scaleY = 1.03f
                                                                    }
                                                                }
                                                                .pointerInput(catKey, isEditMode, orderedCategories, columns, categoryCardWidth) {
                                                                    if (isEditMode) {
                                                                        detectDragGesturesAfterLongPress(
                                                                            onDragStart = {
                                                                                draggingCategory = catKey
                                                                                categoryDragOffset = Offset.Zero
                                                                            },
                                                                            onDragCancel = {
                                                                                draggingCategory = null
                                                                                categoryDragOffset = Offset.Zero
                                                                            },
                                                                            onDragEnd = {
                                                                                draggingCategory = null
                                                                                categoryDragOffset = Offset.Zero
                                                                            },
                                                                            onDrag = { _, dragAmount ->
                                                                                categoryDragOffset += dragAmount
                                                                                val horizontalThreshold = maxOf(categoryCardWidth * 0.45f, categoryMoveThresholdPx * 0.7f)
                                                                                val shift = when {
                                                                                    categoryDragOffset.x > horizontalThreshold -> 1
                                                                                    categoryDragOffset.x < -horizontalThreshold -> -1
                                                                                    categoryDragOffset.y > categoryMoveThresholdPx -> columns
                                                                                    categoryDragOffset.y < -categoryMoveThresholdPx -> -columns
                                                                                    else -> 0
                                                                                }
                                                                                if (shift != 0) {
                                                                                    val from = orderedCategories.indexOf(catKey)
                                                                                    val to = (from + shift).coerceIn(0, orderedCategories.lastIndex)
                                                                                    if (from != to) {
                                                                                        val updated = orderedCategories.toMutableList()
                                                                                        updated.removeAt(from)
                                                                                        updated.add(to, catKey)
                                                                                        viewModel.setCategoryOrder(updated.joinToString(","))
                                                                                    }
                                                                                    categoryDragOffset = Offset.Zero
                                                                                }
                                                                            },
                                                                        )
                                                                    }
                                                                },
                                                        ) {
                                                        SmartCategoryCard(
                                                            category = standardCat ?: AppCategory.CUSTOM,
                                                            displayName = displayName,
                                                            apps     = catApps,
                                                            onTap    = {
                                                                if (standardCat != null) {
                                                                    if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                    onCategoryTap(standardCat)
                                                                } else {
                                                                    selectedCustomCategory = catKey
                                                                }
                                                            },
                                                            customIcon = if (standardCat == null) {
                                                                remember(catKey, customCategoryIcons) {
                                                                    viewModel.getCustomCategoryIcon(catKey)
                                                                }
                                                            } else {
                                                                "folder"
                                                            },
                                                            tileSize = tileSize,
                                                            isEditMode = isEditMode,
                                                            onMoveLeft = {
                                                                val idx = orderedCategories.indexOf(catKey)
                                                                if (idx > 0) {
                                                                    val mutable = orderedCategories.toMutableList()
                                                                    val temp = mutable[idx]
                                                                    mutable[idx] = mutable[idx - 1]
                                                                    mutable[idx - 1] = temp
                                                                    viewModel.setCategoryOrder(mutable.joinToString(","))
                                                                }
                                                            },
                                                            onMoveRight = {
                                                                val idx = orderedCategories.indexOf(catKey)
                                                                if (idx < orderedCategories.size - 1) {
                                                                    val mutable = orderedCategories.toMutableList()
                                                                    val temp = mutable[idx]
                                                                    mutable[idx] = mutable[idx + 1]
                                                                    mutable[idx + 1] = temp
                                                                    viewModel.setCategoryOrder(mutable.joinToString(","))
                                                                }
                                                            },
                                                            onToggleSize = {
                                                                val nextSize = when (tileSize) {
                                                                    "small" -> "medium"
                                                                    "medium" -> "large"
                                                                    else -> "small"
                                                                }
                                                                viewModel.setCategoryTileSize(catKey, nextSize)
                                                            },
                                                            onDelete = if (standardCat == null) {
                                                                { categoryPendingDelete = catKey }
                                                            } else {
                                                                { viewModel.removeCategoryFromHome(catKey) }
                                                            },
                                                            modifier = Modifier.fillMaxWidth(),
                                                        )
                                                        }
                                                    }
                                                    repeat(columns - rowCats.size) { Spacer(Modifier.weight(1f)) }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item { Spacer(Modifier.height(16.dp)) }
                        }
                    }
                }
            }

            if (dockApps.isNotEmpty()) {
                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .padding(bottom = scaffoldPadding.calculateBottomPadding() + 20.dp),
                    contentAlignment = Alignment.Center) {
                    BottomDock(dockApps = dockApps, onAppTap = {
                        if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.launchApp(it)
                    })
                }
            }
        }

        if (contextMenuApp != null) {
            AppContextMenu(
                app = contextMenuApp!!,
                viewModel = viewModel,
                onDismiss = { contextMenuApp = null }
            )
        }

        selectedCustomCategory?.let { categoryName ->
            val customApps = viewModel.byCustomCategory(categoryName)
            AlertDialog(
                onDismissRequest = { selectedCustomCategory = null },
                containerColor = CiyatoBgEl,
                title = { Text(categoryName, color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (customApps.isEmpty()) {
                            Text("No apps in this custom category yet.", color = CiyatoMuted, fontSize = 13.sp)
                        } else {
                            customApps.forEach { app ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(CiyatoBgEl2)
                                        .clickable {
                                            selectedCustomCategory = null
                                            viewModel.launchApp(app)
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    RealAppIcon(app.icon, size = 38.dp, cornerRadius = 10.dp, scale = app.iconScale, rotation = app.iconRotation, accentHex = app.iconAccent)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(app.label, color = CiyatoWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        Text(app.packageName, color = CiyatoMuted, fontSize = 10.sp, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedCustomCategory = null }) {
                        Text("Done", color = CiyatoGold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            categoryPendingDelete = categoryName
                            selectedCustomCategory = null
                        }
                    ) {
                        Text("Remove category", color = CiyatoRed)
                    }
                }
            )
        }

        categoryPendingDelete?.let { categoryName ->
            AlertDialog(
                onDismissRequest = { categoryPendingDelete = null },
                containerColor = CiyatoBgEl,
                title = { Text("Delete category?", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Delete $categoryName from this layout? Its apps stay installed and can be assigned elsewhere.",
                        color = CiyatoSec,
                    )
                },
                dismissButton = {
                    TextButton(onClick = { categoryPendingDelete = null }) {
                        Text("Cancel", color = CiyatoSec)
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.removeCustomCategory(categoryName)
                        categoryPendingDelete = null
                    }) {
                        Text("Delete", color = CiyatoRed)
                    }
                },
            )
        }

        // Custom Category Dialog
        if (showCreateCategoryDialog) {
            AlertDialog(
                onDismissRequest = { showCreateCategoryDialog = false },
                containerColor = CiyatoBgEl,
                title = { Text("New Custom Category", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it.take(24) },
                            singleLine = true,
                            label = { Text("Category name") }
                        )
                        Text("Category icon", color = CiyatoSec, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                "folder" to Icons.Default.Folder,
                                "bookmark" to Icons.Default.Bookmark,
                                "star" to Icons.Default.Star,
                            ).forEach { (key, icon) ->
                                IconButton(
                                    onClick = { newCategoryIcon = key },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (newCategoryIcon == key) CiyatoGold.copy(alpha = 0.16f) else CiyatoBgEl2)
                                        .border(1.dp, if (newCategoryIcon == key) CiyatoGold else CiyatoSubtleBorder, CircleShape),
                                ) {
                                    Icon(icon, contentDescription = key, tint = if (newCategoryIcon == key) CiyatoGold else CiyatoSec)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val name = newCategoryName.trim()
                            if (name.isNotBlank()) {
                                viewModel.addCustomCategory(name)
                                viewModel.setCustomCategoryIcon(name, newCategoryIcon)
                                workspaceForNewCategory?.let { workspaceIndex ->
                                    viewModel.addCategoryToWorkspace(workspaceIndex, name)
                                }
                            }
                            showCreateCategoryDialog = false
                            newCategoryName = ""
                            newCategoryIcon = "folder"
                            workspaceForNewCategory = null
                        },
                        enabled = newCategoryName.isNotBlank()
                    ) {
                        Text("Create", color = CiyatoGold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showCreateCategoryDialog = false
                        workspaceForNewCategory = null
                    }) {
                        Text("Cancel", color = CiyatoSec)
                    }
                }
            )
        }

        if (showHomeCategoryPicker) {
            val currentKeys = orderedCategories.toSet()
            val categoryChoices = (APPROVED_HOME_CATEGORIES.map { it.name } + customCatsList)
                .distinct()
                .filterNot { it in currentKeys }
            AlertDialog(
                onDismissRequest = { showHomeCategoryPicker = false },
                containerColor = CiyatoBgEl,
                title = { Text("Add category to Home", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 340.dp)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        if (categoryChoices.isEmpty()) {
                            Text("Every available category is already on Home.", color = CiyatoMuted)
                        } else {
                            categoryChoices.forEach { categoryKey ->
                                val category = runCatching { AppCategory.valueOf(categoryKey) }.getOrNull()
                                TextButton(
                                    onClick = {
                                        viewModel.restoreCategoryToHome(categoryKey)
                                        showHomeCategoryPicker = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(category?.let(viewModel::getCategoryDisplayName) ?: categoryKey, color = CiyatoWhite, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showHomeCategoryPicker = false
                        showCreateCategoryDialog = true
                    }) {
                        Text("New custom category", color = CiyatoGold)
                    }
                },
            )
        }

        if (showWorkspaceCategoryPicker) {
            val categoryChoices = (WORKSPACE_CATEGORY_CHOICES.map { it.name } + customCatsList).distinct()
            AlertDialog(
                onDismissRequest = { showWorkspaceCategoryPicker = false },
                containerColor = CiyatoBgEl,
                title = { Text("Add category to workspace", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        categoryChoices.forEach { categoryKey ->
                            val standardCategory = runCatching { AppCategory.valueOf(categoryKey) }.getOrNull()
                            TextButton(
                                onClick = {
                                    viewModel.addCategoryToWorkspace(workspaceCategoryPickerIndex, categoryKey)
                                    showWorkspaceCategoryPicker = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    standardCategory?.let(viewModel::getCategoryDisplayName) ?: categoryKey,
                                    color = CiyatoSec,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showWorkspaceCategoryPicker = false
                        workspaceForNewCategory = workspaceCategoryPickerIndex
                        showCreateCategoryDialog = true
                    }) {
                        Text("New custom category", color = CiyatoGold)
                    }
                },
            )
        }

        // Custom Page App Picker
        if (showPageAppPicker) {
            val allInstalledApps by viewModel.allApps.collectAsState()
            AlertDialog(
                onDismissRequest = { showPageAppPicker = false },
                containerColor = CiyatoBgEl,
                title = { Text("Add Shortcut", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allInstalledApps.forEach { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.addAppToPage(pickerPageIndex, app.packageName)
                                        showPageAppPicker = false
                                    }
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RealAppIcon(app.icon, size = 36.dp, cornerRadius = 8.dp, scale = app.iconScale, rotation = app.iconRotation, accentHex = app.iconAccent)
                                Text(app.label, color = CiyatoWhite, fontSize = 14.sp)
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }

        if (showLauncherControls) {
            LauncherControlSheet(
                isEditMode = isEditMode,
                showGreeting = showHomeGreeting,
                showSearch = showHomeSearch,
                showWeather = showHomeWeather,
                showAgenda = showHomeAgenda,
                showAppDrawer = showAppDrawer,
                showRecent = showRecentLaunched,
                showCategories = showSmartCategories,
                workspaceTransition = workspaceTransition,
                onDismiss = { showLauncherControls = false },
                onEditLayout = {
                    isEditMode = true
                    showLauncherControls = false
                },
                onOpenWallpaper = {
                    showLauncherControls = false
                    onOpenSystemWallpaper()
                },
                onOpenSettings = {
                    showLauncherControls = false
                    onOpenOrganizerSettings()
                },
                onShowGreetingChanged = viewModel::setShowHomeGreeting,
                onShowSearchChanged = viewModel::setShowHomeSearch,
                onShowWeatherChanged = viewModel::setShowHomeWeather,
                onShowAgendaChanged = viewModel::setShowHomeAgenda,
                onShowAppDrawerChanged = viewModel::setShowAppDrawer,
                onShowRecentChanged = viewModel::setShowRecentlyLaunched,
                onShowCategoriesChanged = viewModel::setSmartCategories,
                onTransitionChanged = viewModel::setWorkspaceTransition,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LauncherControlSheet(
    isEditMode: Boolean,
    showGreeting: Boolean,
    showSearch: Boolean,
    showWeather: Boolean,
    showAgenda: Boolean,
    showAppDrawer: Boolean,
    showRecent: Boolean,
    showCategories: Boolean,
    workspaceTransition: String,
    onDismiss: () -> Unit,
    onEditLayout: () -> Unit,
    onOpenWallpaper: () -> Unit,
    onOpenSettings: () -> Unit,
    onShowGreetingChanged: (Boolean) -> Unit,
    onShowSearchChanged: (Boolean) -> Unit,
    onShowWeatherChanged: (Boolean) -> Unit,
    onShowAgendaChanged: (Boolean) -> Unit,
    onShowAppDrawerChanged: (Boolean) -> Unit,
    onShowRecentChanged: (Boolean) -> Unit,
    onShowCategoriesChanged: (Boolean) -> Unit,
    onTransitionChanged: (String) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CiyatoBgEl,
        contentColor = CiyatoWhite,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Home controls", color = CiyatoWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Changes are saved to this launcher. System wallpaper is managed by Android.", color = CiyatoMuted, fontSize = 12.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                HomeControlAction(
                    icon = Icons.Default.Edit,
                    label = if (isEditMode) "Edit layout" else "Arrange home",
                    onClick = onEditLayout,
                    modifier = Modifier.weight(1f),
                )
                HomeControlAction(
                    icon = Icons.Default.Wallpaper,
                    label = "Wallpaper",
                    onClick = onOpenWallpaper,
                    modifier = Modifier.weight(1f),
                )
                HomeControlAction(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f),
                )
            }

            Text("Home sections", color = CiyatoSec, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            HomeControlToggle("Greeting and clock", showGreeting, onShowGreetingChanged)
            HomeControlToggle("Search", showSearch, onShowSearchChanged)
            HomeControlToggle("Weather", showWeather, onShowWeatherChanged)
            HomeControlToggle("Agenda", showAgenda, onShowAgendaChanged)
            HomeControlToggle("App Library", showAppDrawer, onShowAppDrawerChanged)
            HomeControlToggle("Recently used", showRecent, onShowRecentChanged)
            HomeControlToggle("Categories", showCategories, onShowCategoriesChanged)

            Text("Workspace transition", color = CiyatoSec, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("slide" to "Slide", "fade" to "Fade", "scale" to "Scale").forEach { (value, label) ->
                    val selected = workspaceTransition == value
                    TextButton(
                        onClick = { onTransitionChanged(value) },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) CiyatoGold.copy(alpha = 0.16f) else CiyatoBgEl2),
                    ) {
                        Text(label, color = if (selected) CiyatoGold else CiyatoSec, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeControlAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CiyatoBgEl2)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon, contentDescription = label, tint = CiyatoSec, modifier = Modifier.size(20.dp))
        Text(label, color = CiyatoWhite, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
private fun HomeControlToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = CiyatoWhite, fontSize = 14.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun HomeSectionRemoveButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(28.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(CiyatoBg.copy(alpha = 0.84f)),
    ) {
        Icon(Icons.Default.Close, contentDescription = "Remove from Home", tint = CiyatoRed, modifier = Modifier.size(16.dp))
    }
}

private fun currentTimeString(): String =
    SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())

@Composable
private fun HomeSearchBar(isDense: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Box(modifier = modifier.height(if (isDense) 50.dp else 56.dp)
        .clip(RoundedCornerShape(999.dp)).background(CiyatoBgEl)
        .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(999.dp))
        .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart) {
        Row(modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.Search, null, tint = CiyatoMuted, modifier = Modifier.size(18.dp))
            Text("Search apps...", color = CiyatoMuted, fontSize = if (isDense) 14.sp else 15.sp)
        }
    }
}

@Composable
private fun FocusBadge(session: FocusSessionManager.FocusSession) {
    val pulse by rememberInfiniteTransition(label = "focus_pulse").animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse",
    )
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.scale(pulse).clip(RoundedCornerShape(10.dp))
            .background(CiyatoGold.copy(0.15f)).border(1.dp, CiyatoGold.copy(0.35f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Timer, null, tint = CiyatoGold, modifier = Modifier.size(14.dp))
            Text("%02d:%02d".format(session.remainingMin, session.remainingSec),
                color = CiyatoGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
