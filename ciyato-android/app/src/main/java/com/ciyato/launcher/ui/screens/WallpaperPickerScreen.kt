package com.ciyato.launcher.ui.screens

import android.app.WallpaperManager
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * WallpaperPickerScreen - Suggestion #93
 * Browse gradient wallpapers or open system wallpaper picker.
 */

data class GradientWallpaper(
    val id: String,
    val name: String,
    val colors: List<Color>,
)

private val GRADIENT_PRESETS = listOf(
    GradientWallpaper("midnight", "Midnight", listOf(Color(0xFF0B0F12), Color(0xFF1A1F2E), Color(0xFF0B0F12))),
    GradientWallpaper("ocean", "Ocean Deep", listOf(Color(0xFF0A1628), Color(0xFF0D3B6E), Color(0xFF0A1628))),
    GradientWallpaper("aurora", "Aurora", listOf(Color(0xFF0D1B2A), Color(0xFF1B4332), Color(0xFF7C3AED))),
    GradientWallpaper("smoke", "Smoke Glass", listOf(Color(0xFF050607), Color(0xFF171A1D), Color(0xFF3D4247))),
    GradientWallpaper("graphite", "Soft Graphite", listOf(Color(0xFF08090A), Color(0xFF23272B), Color(0xFF5F666C))),
    GradientWallpaper("forest", "Forest", listOf(Color(0xFF0A1B0D), Color(0xFF14532D), Color(0xFF15803D))),
    GradientWallpaper("galaxy", "Galaxy", listOf(Color(0xFF0C0A1E), Color(0xFF3B0764), Color(0xFF6D28D9))),
    GradientWallpaper("silver", "Silver Trace", listOf(Color(0xFF050607), Color(0xFF2A2D30), Color(0xFFE8E8E4))),
    GradientWallpaper("ice", "Ice Blue", listOf(Color(0xFF0C1A2E), Color(0xFF1E3A5F), Color(0xFF7DB7FF))),
    GradientWallpaper("mono", "Monochrome", listOf(Color(0xFF000000), Color(0xFF1A1A1A), Color(0xFF2D2D2D))),
    GradientWallpaper("neon", "Neon Nights", listOf(Color(0xFF0A0014), Color(0xFF1A004F), Color(0xFF5E0082))),
    GradientWallpaper("stone", "Stone", listOf(Color(0xFF0D0D0D), Color(0xFF303235), Color(0xFFBFC1C1))),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperPickerScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selected by remember { mutableStateOf<String?>(null) }
    var imageStatus by remember { mutableStateOf<String?>(null) }
    val personalImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                imageStatus = "Applying image..."
                imageStatus = if (applyImageWallpaper(context, uri)) {
                    viewModel.setCiyatoVideoWallpaper("")
                    viewModel.setUseSystemWallpaper(true)
                    "Personal image applied."
                } else {
                    "Ciyato could not apply that image."
                }
            }
        }
    }
    val personalVideoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                imageStatus = "Checking video..."
                val result = withContext(Dispatchers.IO) { validateCiyatoVideo(context, uri) }
                if (!result.isValid) {
                    imageStatus = result.message
                    return@launch
                }
                imageStatus = "Preparing Ciyato video background..."
                val localUri = importCiyatoVideo(context, uri)
                imageStatus = if (localUri != null) {
                    viewModel.setCiyatoVideoWallpaper(localUri.toString())
                    viewModel.setUseSystemWallpaper(false)
                    "Ciyato-only video background applied. It pauses when Ciyato is hidden, the screen is off, or Battery Saver is on."
                } else {
                    "Ciyato could not prepare that video. Choose another clip."
                }
            }
        }
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = { Text("Wallpaper", color = CiyatoWhite, fontWeight = FontWeight.SemiBold) },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CiyatoBgEl),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().clickable {
                    personalImagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Wallpaper, null, tint = CiyatoGold, modifier = Modifier.size(24.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Choose personal image", color = CiyatoWhite, fontWeight = FontWeight.SemiBold)
                        Text("Uses Android Photo Picker - no broad photo access", color = CiyatoMuted, fontSize = 12.sp)
                    }
                    Text("→", color = CiyatoSec, fontSize = 18.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CiyatoBgEl),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().clickable {
                    personalVideoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly),
                    )
                },
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Wallpaper, null, tint = CiyatoGold, modifier = Modifier.size(24.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Choose short video", color = CiyatoWhite, fontWeight = FontWeight.SemiBold)
                        Text("Ciyato-only background, silent, up to 15 seconds", color = CiyatoMuted, fontSize = 12.sp)
                    }
                    Text("->", color = CiyatoSec, fontSize = 18.sp)
                }
            }

            TextButton(
                onClick = {
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_SET_WALLPAPER))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Open system wallpaper picker", color = CiyatoGold)
            }

            imageStatus?.let { status ->
                Text(
                    status,
                    color = CiyatoSec,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Text("Gradient Presets", color = CiyatoWhite, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(GRADIENT_PRESETS, key = { it.id }) { wp ->
                    GradientWallpaperCard(
                        wp = wp,
                        isSelected = selected == wp.id,
                        onClick = {
                            selected = wp.id
                            applyGradientWallpaper(context, wp)
                            viewModel.setCiyatoVideoWallpaper("")
                            viewModel.setUseSystemWallpaper(true)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GradientWallpaperCard(
    wp: GradientWallpaper,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.56f)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(wp.colors))
            .then(
                if (isSelected)
                    Modifier.border(2.dp, CiyatoGold, RoundedCornerShape(12.dp))
                else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomStart,
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Check, null, tint = CiyatoGold, modifier = Modifier.size(28.dp))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(6.dp),
        ) {
            Text(wp.name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun applyGradientWallpaper(context: android.content.Context, wp: GradientWallpaper) {
    try {
        val wm = WallpaperManager.getInstance(context)
        val dm = context.resources.displayMetrics
        val bitmap = android.graphics.Bitmap.createBitmap(dm.widthPixels, dm.heightPixels,
            android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        val paint = android.graphics.Paint()
        val colorInts = wp.colors.map { c ->
            android.graphics.Color.argb(
                (c.alpha * 255).toInt(),
                (c.red * 255).toInt(),
                (c.green * 255).toInt(),
                (c.blue * 255).toInt(),
            )
        }.toIntArray()
        val positions = FloatArray(colorInts.size) { i -> i.toFloat() / (colorInts.size - 1) }
        val shader = android.graphics.LinearGradient(
            0f, 0f, 0f, dm.heightPixels.toFloat(),
            colorInts, positions, android.graphics.Shader.TileMode.CLAMP,
        )
        paint.shader = shader
        canvas.drawRect(0f, 0f, dm.widthPixels.toFloat(), dm.heightPixels.toFloat(), paint)
        wm.setBitmap(bitmap)
    } catch (_: Exception) {}
}

private suspend fun applyImageWallpaper(context: android.content.Context, uri: Uri): Boolean =
    withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                WallpaperManager.getInstance(context).setStream(stream)
            } ?: error("Selected image is no longer available")
        }.isSuccess
    }

private data class VideoValidationResult(val isValid: Boolean, val message: String)

private fun validateCiyatoVideo(context: android.content.Context, uri: Uri): VideoValidationResult = runCatching {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(context, uri)
    val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
    val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
    val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
    val mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE).orEmpty()
    retriever.release()
    when {
        durationMs <= 0L -> VideoValidationResult(false, "Ciyato could not read that video.")
        durationMs > 15_000L -> VideoValidationResult(false, "This video is longer than 15 seconds. Trim it in your video editor, then choose the short clip again.")
        width <= 0 || height <= 0 -> VideoValidationResult(false, "That video has an unsupported resolution.")
        !mimeType.startsWith("video/") -> VideoValidationResult(false, "Choose a supported video file.")
        else -> VideoValidationResult(true, "Ciyato-only video background applied. It pauses in Battery Saver.")
    }
}.getOrElse { VideoValidationResult(false, "Ciyato could not open that video.") }

private suspend fun importCiyatoVideo(context: android.content.Context, source: Uri): Uri? =
    withContext(Dispatchers.IO) {
        runCatching {
            val directory = File(context.filesDir, "wallpapers").apply { mkdirs() }
            val target = File(directory, "ciyato_video_wallpaper.mp4")
            context.contentResolver.openInputStream(source)?.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            } ?: error("Selected video is no longer readable")
            Uri.fromFile(target)
        }.getOrNull()
    }
