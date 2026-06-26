package com.ciyato.launcher.ui.screens

import android.app.WallpaperManager
import android.content.Intent
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

/**
 * WallpaperPickerScreen — Suggestion #93
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
    GradientWallpaper("sunset", "Sunset", listOf(Color(0xFF1A0A2E), Color(0xFF7C2D12), Color(0xFFEA580C))),
    GradientWallpaper("rose", "Rose Gold", listOf(Color(0xFF1A0A0F), Color(0xFF7F1D1D), Color(0xFFB91C1C))),
    GradientWallpaper("forest", "Forest", listOf(Color(0xFF0A1B0D), Color(0xFF14532D), Color(0xFF15803D))),
    GradientWallpaper("galaxy", "Galaxy", listOf(Color(0xFF0C0A1E), Color(0xFF3B0764), Color(0xFF6D28D9))),
    GradientWallpaper("gold", "Gold Rush", listOf(Color(0xFF1A1200), Color(0xFF713F12), Color(0xFFC6A15B))),
    GradientWallpaper("ice", "Ice Blue", listOf(Color(0xFF0C1A2E), Color(0xFF1E3A5F), Color(0xFF7DB7FF))),
    GradientWallpaper("mono", "Monochrome", listOf(Color(0xFF000000), Color(0xFF1A1A1A), Color(0xFF2D2D2D))),
    GradientWallpaper("neon", "Neon Nights", listOf(Color(0xFF0A0014), Color(0xFF1A004F), Color(0xFF5E0082))),
    GradientWallpaper("earth", "Earthy", listOf(Color(0xFF1C1208), Color(0xFF3D2B1F), Color(0xFF78350F))),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperPickerScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf<String?>(null) }

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
                    val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                    context.startActivity(Intent.createChooser(intent, "Select Wallpaper"))
                },
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Wallpaper, null, tint = CiyatoGold, modifier = Modifier.size(24.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Choose from Gallery", color = CiyatoWhite, fontWeight = FontWeight.SemiBold)
                        Text("Pick any photo from your library", color = CiyatoMuted, fontSize = 12.sp)
                    }
                    Text("→", color = CiyatoSec, fontSize = 18.sp)
                }
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
