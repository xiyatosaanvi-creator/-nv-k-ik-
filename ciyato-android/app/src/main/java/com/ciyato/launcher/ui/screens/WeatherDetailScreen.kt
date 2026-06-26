package com.ciyato.launcher.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.WbSunny
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
import com.ciyato.launcher.data.LocationHelper
import com.ciyato.launcher.data.WeatherRepository
import com.ciyato.launcher.data.WeatherRepository.WeatherState
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

/**
 * WeatherDetailScreen — Live weather powered by Open-Meteo (no API key).
 *
 * Permission flow:
 *   NoPermission → user taps "Enable" → system dialog
 *   If granted:  → ViewModel.fetchWeather(context) → Loading → Success/Error
 *   If denied:   → stays on NoPermission card
 *
 * All weather data flows through LauncherViewModel.weatherState so it's
 * shared with the home-screen WeatherCard widget.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(
    viewModel: LauncherViewModel? = null,  // null = standalone usage (backwards-compat)
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    // Support both standalone and ViewModel-driven usage
    var localState by remember {
        mutableStateOf<WeatherState>(
            if (LocationHelper.hasPermission(context)) WeatherState.Loading
            else WeatherState.NoPermission
        )
    }
    val vmState by (viewModel?.weatherState ?: kotlinx.coroutines.flow.MutableStateFlow(localState))
        .collectAsState()

    val weatherState = if (viewModel != null) vmState else localState

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (viewModel != null) {
                viewModel.fetchWeather(context)
            } else {
                localState = WeatherState.Loading
                // standalone coroutine fetch
            }
        } else {
            if (viewModel == null) localState = WeatherState.NoPermission
        }
    }

    // Auto-fetch when screen opens and permission is already granted
    LaunchedEffect(Unit) {
        if (LocationHelper.hasPermission(context)) {
            if (viewModel != null) {
                viewModel.fetchWeather(context)
            } else {
                localState = WeatherState.Loading
                val loc = LocationHelper.getLocation(context)
                localState = if (loc != null)
                    WeatherRepository.fetchWeather(loc.lat, loc.lon)
                else
                    WeatherState.NoLocation
            }
        }
    }

    val onRefresh = {
        if (LocationHelper.hasPermission(context)) {
            if (viewModel != null) {
                viewModel.fetchWeather(context)
            }
        }
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = {
                    Text("Weather", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CiyatoSec)
                    }
                },
                actions = {
                    if (weatherState is WeatherState.Success || weatherState is WeatherState.Loading) {
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Default.Refresh, "Refresh", tint = CiyatoSec)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(CiyatoBgEl2, CiyatoBg, CiyatoBg)))
        ) {
            AnimatedContent(
                targetState = weatherState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "weather_state",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) { state ->
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when (state) {

                        is WeatherState.NoPermission -> item {
                            WeatherPermissionCard {
                                permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                            }
                        }

                        is WeatherState.Loading -> item {
                            WeatherLoadingCard()
                        }

                        is WeatherState.NoLocation -> item {
                            WeatherInfoCard(
                                icon = Icons.Default.LocationOff,
                                title = "Location unavailable",
                                body = "Could not determine your device location. " +
                                        "Please ensure location services are enabled in Android Settings.",
                                actionLabel = "Retry",
                                onAction = onRefresh,
                            )
                        }

                        is WeatherState.Error -> item {
                            WeatherInfoCard(
                                icon = Icons.Default.SignalWifiOff,
                                title = "Connection error",
                                body = state.message,
                                actionLabel = "Retry",
                                onAction = onRefresh,
                            )
                        }

                        is WeatherState.Success -> {
                            item { WeatherHeroCard(state) }
                            item { WeatherDetailsRow(state) }
                            item { WeatherWindHumidityCard(state) }
                            item { WeatherAttribution() }
                        }

                        else -> item {
                            WeatherInfoCard(
                                icon = Icons.Default.Cloud,
                                title = "Fetching weather…",
                                body = "Setting up your local weather.",
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Permission card ────────────────────────────────────────────────────────

@Composable
private fun WeatherPermissionCard(onEnable: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CiyatoGold.copy(alpha = 0.08f))
            .border(1.dp, CiyatoGold.copy(alpha = 0.22f), RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                    .background(CiyatoGold.copy(alpha = 0.18f)),
            ) {
                Icon(Icons.Default.LocationOn, null, tint = CiyatoGold, modifier = Modifier.size(24.dp))
            }
            Column {
                Text("Enable Local Weather", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Approximate location only", color = CiyatoGold, fontSize = 12.sp)
            }
        }
        Text(
            "Ciyato uses Open-Meteo — a 100% free weather service — to show your local conditions. " +
                    "Your location is never uploaded or stored. Only approximate coordinates are used.",
            color = CiyatoSec, fontSize = 13.sp, lineHeight = 20.sp,
        )
        Button(
            onClick = onEnable,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CiyatoGold),
            shape = RoundedCornerShape(14.dp),
        ) {
            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Enable Local Weather", color = CiyatoBg, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Text(
            "Foreground location only · No background tracking · Open-Meteo (no API key)",
            color = CiyatoMuted, fontSize = 11.sp, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ─── Loading card ─────────────────────────────────────────────────────────────

@Composable
private fun WeatherLoadingCard() {
    Box(
        modifier = Modifier.fillMaxWidth().height(280.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator(color = CiyatoGold, strokeWidth = 3.dp, modifier = Modifier.size(48.dp))
            Text("Fetching local weather…", color = CiyatoSec, fontSize = 14.sp)
            Text("Powered by Open-Meteo", color = CiyatoMuted, fontSize = 11.sp)
        }
    }
}

// ─── Success: hero card (big temp + condition) ────────────────────────────────

@Composable
private fun WeatherHeroCard(state: WeatherState.Success) {
    val conditionIcon: ImageVector = when {
        state.weatherCode == 0 && state.isDay    -> Icons.Outlined.WbSunny
        state.weatherCode == 0 && !state.isDay  -> Icons.Default.DarkMode
        state.weatherCode in 1..2               -> Icons.Default.Cloud
        state.weatherCode == 3                   -> Icons.Default.Cloud
        state.weatherCode in 45..48             -> Icons.Default.BlurOn
        state.weatherCode in 51..67             -> Icons.Default.Grain
        state.weatherCode in 71..77             -> Icons.Default.AcUnit
        state.weatherCode in 80..86             -> Icons.Default.Umbrella
        state.weatherCode in 95..99             -> Icons.Default.Bolt
        else                                     -> Icons.Default.Cloud
    }
    val conditionColor: Color = when {
        state.weatherCode == 0 && state.isDay    -> CiyatoGoldSoft
        state.weatherCode in 1..2               -> CiyatoGoldSoft.copy(alpha = 0.7f)
        state.weatherCode in 51..99             -> CiyatoBlue
        else                                     -> CiyatoSec
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(listOf(CiyatoBgEl.copy(alpha = 0.95f), CiyatoBgEl2))
            )
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(28.dp))
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Location row
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.LocationOn, null, tint = CiyatoGold, modifier = Modifier.size(16.dp))
            Text(state.locationName, color = CiyatoGold, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        // Temperature row
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(conditionIcon, null, tint = conditionColor, modifier = Modifier.size(52.dp).padding(top = 6.dp))
            Column {
                Text("${state.tempC}°", color = CiyatoWhite, fontSize = 72.sp, fontWeight = FontWeight.ExtraBold,
                    lineHeight = 72.sp)
                Text(state.condition, color = CiyatoSec, fontSize = 17.sp)
            }
        }

        HorizontalDivider(color = CiyatoSubtleBorder)

        // H/L/FeelsLike row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            WeatherStatChip("Feels like", "${state.feelsLikeC}°")
            WeatherStatChip("High", "${state.highC}°", color = Color(0xFFFF6B6B))
            WeatherStatChip("Low",  "${state.lowC}°",  color = CiyatoBlue)
        }
    }
}

@Composable
private fun WeatherStatChip(label: String, value: String, color: Color = CiyatoWhite) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, color = CiyatoMuted, fontSize = 11.sp)
        Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// ─── Details row (wind + humidity) ───────────────────────────────────────────

@Composable
private fun WeatherDetailsRow(state: WeatherState.Success) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        WeatherMetricCard(
            icon  = Icons.Default.Air,
            label = "Wind",
            value = "${state.windKmh.toInt()} km/h",
            modifier = Modifier.weight(1f),
        )
        WeatherMetricCard(
            icon  = Icons.Default.WaterDrop,
            label = "Humidity",
            value = "${state.humidity}%",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun WeatherMetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                .background(CiyatoGold.copy(alpha = 0.12f)),
        ) {
            Icon(icon, null, tint = CiyatoGold, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(label, color = CiyatoMuted, fontSize = 11.sp)
            Text(value, color = CiyatoWhite, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── Wind + humidity detail card ─────────────────────────────────────────────

@Composable
private fun WeatherWindHumidityCard(state: WeatherState.Success) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Conditions", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        HorizontalDivider(color = CiyatoSubtleBorder)
        WeatherConditionRow("Weather Code", "WMO ${state.weatherCode}")
        WeatherConditionRow("Condition",    state.condition)
        WeatherConditionRow("Time of day",  if (state.isDay) "Daytime" else "Night-time")
        WeatherConditionRow("Also in °F",   "${WeatherRepository.cToF(state.tempC)}°F")
    }
}

@Composable
private fun WeatherConditionRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = CiyatoMuted, fontSize = 13.sp)
        Text(value, color = CiyatoSec, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

// ─── Error/info card ──────────────────────────────────────────────────────────

@Composable
private fun WeatherInfoCard(
    icon: ImageVector,
    title: String,
    body: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, null, tint = CiyatoMuted, modifier = Modifier.size(36.dp))
        Text(title, color = CiyatoWhite, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Text(body, color = CiyatoSec, fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) {
            OutlinedButton(
                onClick = onAction,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CiyatoGold),
                border = androidx.compose.foundation.BorderStroke(1.dp, CiyatoGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().height(44.dp),
            ) {
                Text(actionLabel, fontSize = 14.sp)
            }
        }
    }
}

// ─── Attribution footer ───────────────────────────────────────────────────────

@Composable
private fun WeatherAttribution() {
    Text(
        "Weather data by Open-Meteo.com · Location by Nominatim / OpenStreetMap",
        color = CiyatoMuted,
        fontSize = 11.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    )
}
