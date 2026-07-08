package com.ciyato.launcher.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ciyato.launcher.ui.theme.CiyatoBg
import com.ciyato.launcher.ui.theme.CiyatoBgEl
import com.ciyato.launcher.ui.theme.CiyatoBgEl2
import com.ciyato.launcher.ui.theme.CiyatoGold
import com.ciyato.launcher.ui.theme.CiyatoMuted
import com.ciyato.launcher.ui.theme.CiyatoSec
import com.ciyato.launcher.ui.theme.CiyatoSubtleBorder
import com.ciyato.launcher.ui.theme.CiyatoWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(onBack: () -> Unit) {
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedChip by remember { mutableStateOf("Selected") }
    val filterChips = listOf("Selected", "Collections", "Privacy")

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 50),
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedUris = (selectedUris + uris).distinct()
            selectedChip = "Selected"
        }
    }

    val openPicker = {
        photoPicker.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
        )
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ciyato Photos", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Selected-media organization · on device", color = CiyatoGold, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CiyatoSec)
                    }
                },
                actions = {
                    IconButton(onClick = openPicker) {
                        Icon(Icons.Default.AddPhotoAlternate, "Select photos", tint = CiyatoGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterChips) { chip ->
                        val selected = selectedChip == chip
                        Text(
                            text = chip,
                            color = if (selected) CiyatoBg else CiyatoSec,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) CiyatoGold else CiyatoBgEl)
                                .clickable { selectedChip = chip }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            when (selectedChip) {
                "Privacy" -> item { PhotoPrivacyCard() }
                "Collections" -> {
                    item {
                        StagedCollectionsCard()
                    }
                }
                else -> {
                    if (selectedUris.isEmpty()) {
                        item {
                            PhotoPickerEducationCard(onSelect = openPicker)
                        }
                        item {
                            Text(
                                "Ciyato asks Android to share only the photos you choose. It does not request full-gallery access.",
                                color = CiyatoMuted,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                            )
                        }
                    } else {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text("Selected media", color = CiyatoWhite, fontWeight = FontWeight.Bold)
                                    Text("${selectedUris.size} items available to Ciyato", color = CiyatoMuted, fontSize = 12.sp)
                                }
                                Button(
                                    onClick = openPicker,
                                    colors = ButtonDefaults.buttonColors(containerColor = CiyatoBgEl2),
                                    shape = RoundedCornerShape(10.dp),
                                ) {
                                    Text("Add more", color = CiyatoGold)
                                }
                            }
                        }
                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier.height(
                                    (((selectedUris.size + 2) / 3).coerceAtMost(6) * 118).dp,
                                ),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                userScrollEnabled = false,
                            ) {
                                items(selectedUris.take(18), key = { it.toString() }) { uri ->
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Selected photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(CiyatoBgEl),
                                    )
                                }
                            }
                        }
                        item {
                            Text(
                                "Automatic moment grouping is staged. This beta displays only media explicitly selected through Android Photo Picker.",
                                color = CiyatoMuted,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoPickerEducationCard(onSelect: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CiyatoGold.copy(alpha = 0.08f))
            .border(1.dp, CiyatoGold.copy(alpha = 0.22f), RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(28.dp))
        Text("Choose photos to organize", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text(
            "Android Photo Picker lets you select specific photos without granting Ciyato access to your whole gallery.",
            color = CiyatoSec,
            fontSize = 13.sp,
            lineHeight = 20.sp,
        )
        Button(
            onClick = onSelect,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CiyatoGold),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Select Photos", color = CiyatoBg, fontWeight = FontWeight.Bold)
        }
        Text("Not now is always okay. No photo access is requested at onboarding.", color = CiyatoMuted, fontSize = 11.sp)
    }
}

@Composable
private fun StagedCollectionsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(22.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Default.Collections, contentDescription = null, tint = CiyatoGold)
        Text("Smart Collections are staged", color = CiyatoWhite, fontWeight = FontWeight.Bold)
        Text(
            "Custom collections and on-device grouping are the next implementation phase. No AI grouping is running in this build.",
            color = CiyatoSec,
            fontSize = 13.sp,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun PhotoPrivacyCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(22.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, tint = CiyatoGold)
        Text("Private by design", color = CiyatoWhite, fontWeight = FontWeight.Bold)
        Text(
            "Selected photos are displayed locally. Ciyato does not upload them and does not run cloud analysis.",
            color = CiyatoSec,
            fontSize = 13.sp,
            lineHeight = 20.sp,
        )
    }
}
