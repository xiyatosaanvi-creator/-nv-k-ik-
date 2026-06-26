package com.ciyato.launcher.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ciyato.launcher.data.WeatherRepository
import com.ciyato.launcher.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
        )
    }
    var weatherState by remember {
        mutableStateOf<WeatherRepository.WeatherState>(
            if (hasLocationPermission) WeatherRepository.WeatherState.Loading
            else WeatherRepository.WeatherState.NoPermission
        )
    }
    var showPermissionRationale by remember { mutableStateOf(!hasLocationPermission) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            showPermissionRationale = false
            weatherState = WeatherRepository.WeatherState.Loading
            scope.launch {
                weatherState = WeatherRepository.fetchWeather(0.0, 0.0)
            }
        } else {
            weatherState = WeatherRepository.WeatherState.NoPermission
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            weatherState = WeatherRepository.WeatherState.Loading
            weatherState = WeatherRepository.fetchWeather(0.0, 0.0)
        }
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = { Text("Weather", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CiyatoSec)
                    }
                },
                actions = {
                    if (hasLocationPermission) {
                        IconButton(onClick = {
                            scope.launch {
                                weatherState = WeatherRepository.WeatherState.Loading
                                weatherState = WeatherRepository.fetchWeather(0.0, 0.0)
                            }
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = CiyatoSec)
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
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to CiyatoBgEl2,
                            0.20f to CiyatoBg,
                            1f to CiyatoBg,
                        )
                    )
                )
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = 32.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                when {
                    showPermissionRationale -> {
                        item { WeatherPermissionCard(onEnable = {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }) }
                    }

                    weatherState is WeatherRepository.WeatherState.NoPermission -> {
                        item { WeatherPermissionCard(onEnable = {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }) }
                    }

                    weatherState is WeatherRepository.WeatherState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(300.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    CircularProgressIndicator(color = CiyatoGold)
                                    Text("Fetching local weather…", color = CiyatoSec)
                                }
                            }
                        }
                    }

                    weatherState is WeatherRepository.WeatherState.NotConfigured -> {
                        item { WeatherHeroCard() }
                        item { WeatherNotConfiguredCard() }
                    }

                    weatherState is WeatherRepository.WeatherState.Success -> {
                        val s = weatherState as WeatherRepository.WeatherState.Success
                        item { WeatherSuccessCard(state = s) }
                    }

                    weatherState is WeatherRepository.WeatherState.Error -> {
                        item {
                            Text(
                                "Error: ${(weatherState as WeatherRepository.WeatherState.Error).message}",
                                color = CiyatoMuted,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherPermissionCard(onEnable: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoGold.copy(alpha = 0.08f))
            .border(1.dp, CiyatoGold.copy(alpha = 0.22f), RoundedCornerShape(22.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CiyatoGold.copy(alpha = 0.18f)),
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(22.dp))
            }
            Text("Enable Local Weather", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        }

        Text(
            "Ciyato uses your approximate location only to show local weather on your home screen. Your location is never uploaded or shared.",
            color = CiyatoSec,
            fontSize = 13.sp,
            lineHeight = 20.sp,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onEnable,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CiyatoGold),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Enable Local Weather", color = CiyatoBg, fontWeight = FontWeight.Bold)
            }
        }

        Text(
            "Foreground location only · No background tracking",
            color = CiyatoMuted,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun WeatherHeroCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Outlined.WbSunny, contentDescription = null, tint = CiyatoGoldSoft, modifier = Modifier.size(56.dp))
        Text("—°", color = CiyatoWhite, fontSize = 56.sp, fontWeight = FontWeight.Bold)
        Text("Location enabled", color = CiyatoGold, fontSize = 13.sp)
    }
}

@Composable
private fun WeatherNotConfiguredCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(18.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Weather API not configured yet.", color = CiyatoWhite, fontWeight = FontWeight.SemiBold)
        Text(
            "Location permission has been granted. To show live weather data, a weather API provider needs to be connected. This will be available in an upcoming Ciyato update.",
            color = CiyatoMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun WeatherSuccessCard(state: WeatherRepository.WeatherState.Success) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Outlined.WbSunny, contentDescription = null, tint = CiyatoGoldSoft, modifier = Modifier.size(36.dp).padding(top = 4.dp))
            Column {
                Text("${state.tempC}°", color = CiyatoWhite, fontSize = 56.sp, fontWeight = FontWeight.Bold)
                Text(state.condition, color = CiyatoSec, fontSize = 16.sp)
            }
        }
        HorizontalDivider(color = CiyatoSubtleBorder)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Feels like", color = CiyatoMuted, fontSize = 11.sp)
                Text("${state.feelsLikeC}°", color = CiyatoWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("High", color = CiyatoMuted, fontSize = 11.sp)
                Text("${state.highC}°", color = CiyatoWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Low", color = CiyatoMuted, fontSize = 11.sp)
                Text("${state.lowC}°", color = CiyatoWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = CiyatoMuted, modifier = Modifier.size(14.dp))
            Text(state.locationName, color = CiyatoMuted, fontSize = 12.sp)
        }
    }
}
