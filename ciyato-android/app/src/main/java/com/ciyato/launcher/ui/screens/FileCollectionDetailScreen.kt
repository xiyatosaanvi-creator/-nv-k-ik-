package com.ciyato.launcher.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CiyatoFile(
    val name: String,
    val uri: Uri,
    val sizeBytes: Long,
    val mimeType: String?,
    val lastModified: Long,
)

data class FileCollectionInfo(
    val title: String,
    val icon: ImageVector,
    val accentColor: Color,
    val mimeFilter: String?,
    val description: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileCollectionDetailScreen(
    collectionTitle: String,
    collectionIcon: ImageVector,
    collectionColor: Color,
    initialFolderUri: Uri? = null,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedFolderUri by remember(initialFolderUri) { mutableStateOf(initialFolderUri) }
    var files by remember { mutableStateOf<List<CiyatoFile>>(emptyList()) }
    var isLoading by remember(initialFolderUri) { mutableStateOf(initialFolderUri != null) }

    LaunchedEffect(initialFolderUri) {
        if (initialFolderUri != null) {
            files = loadFilesFromUri(context, initialFolderUri, collectionTitle)
            isLoading = false
        }
    }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            // Persist read permission
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedFolderUri = uri
            isLoading = true
            scope.launch {
                files = loadFilesFromUri(context, uri, collectionTitle)
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(collectionColor.copy(alpha = 0.18f)),
                        ) {
                            Icon(collectionIcon, contentDescription = null, tint = collectionColor, modifier = Modifier.size(16.dp))
                        }
                        Text(collectionTitle, color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CiyatoSec)
                    }
                },
                actions = {
                    if (selectedFolderUri != null) {
                        IconButton(onClick = { folderPickerLauncher.launch(null) }) {
                            Icon(Icons.Default.FolderOpen, contentDescription = "Change folder", tint = CiyatoSec)
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
                            0.15f to CiyatoBg,
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                when {
                    selectedFolderUri == null -> {
                        item { GrantAccessCard(collectionTitle, collectionColor, onEnable = { folderPickerLauncher.launch(null) }) }
                        item { SafExplainerCard() }
                    }

                    isLoading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(300.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    CircularProgressIndicator(color = CiyatoGold)
                                    Text("Reading folder contents…", color = CiyatoSec)
                                }
                            }
                        }
                    }

                    files.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(40.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    "No files found in the selected folder.",
                                    color = CiyatoMuted,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    else -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("${files.size} files", color = CiyatoWhite, fontWeight = FontWeight.SemiBold)
                                Text("Sorted by date", color = CiyatoMuted, fontSize = 12.sp)
                            }
                        }

                        items(files) { file ->
                            FileRow(
                                file = file,
                                accentColor = collectionColor,
                                onTap = {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(file.uri, file.mimeType ?: "*/*")
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    try { context.startActivity(intent) } catch (e: Exception) { }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GrantAccessCard(title: String, color: Color, onEnable: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.22f), RoundedCornerShape(22.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Enable $title Access", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(
            "Choose a folder you want Ciyato to browse. Ciyato only reads what you select and never deletes or modifies files automatically.",
            color = CiyatoSec,
            fontSize = 13.sp,
            lineHeight = 20.sp,
        )
        Button(
            onClick = onEnable,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CiyatoGold),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Choose Folder", color = CiyatoBg, fontWeight = FontWeight.Bold)
        }
        Text(
            "Uses Android Storage Access Framework · No broad storage access",
            color = CiyatoMuted,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SafExplainerCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(16.dp))
            Text("Privacy first", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
        Text(
            "Ciyato uses Android's built-in folder picker. You control exactly which folder is shared and can revoke that access from Android settings.",
            color = CiyatoMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun FileRow(file: CiyatoFile, accentColor: Color, onTap: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onTap)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.14f)),
        ) {
            Icon(fileIcon(file.mimeType), contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(file.name, color = CiyatoWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            Text(formatFileSize(file.sizeBytes), color = CiyatoMuted, fontSize = 11.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CiyatoMuted, modifier = Modifier.size(18.dp))
    }
}

private fun fileIcon(mimeType: String?): ImageVector {
    return when {
        mimeType == null -> Icons.Default.InsertDriveFile
        mimeType.startsWith("image/") -> Icons.Default.Image
        mimeType.startsWith("video/") -> Icons.Default.VideoFile
        mimeType.startsWith("audio/") -> Icons.Default.AudioFile
        mimeType == "application/pdf" -> Icons.Default.PictureAsPdf
        mimeType.contains("spreadsheet") || mimeType.contains("excel") -> Icons.Default.TableChart
        mimeType.contains("document") || mimeType.contains("word") -> Icons.Default.Description
        mimeType.contains("zip") || mimeType.contains("archive") -> Icons.Default.Archive
        mimeType == "application/vnd.android.package-archive" -> Icons.Default.Android
        else -> Icons.Default.InsertDriveFile
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}

private suspend fun loadFilesFromUri(
    context: android.content.Context,
    treeUri: Uri,
    collectionTitle: String,
): List<CiyatoFile> {
    return withContext(Dispatchers.IO) {
        val files = mutableListOf<CiyatoFile>()
        try {
            val docUri = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, treeUri) ?: return@withContext files
            docUri.listFiles().forEach { doc ->
                if (doc.isFile && doc.name != null) {
                    files.add(
                        CiyatoFile(
                            name = doc.name!!,
                            uri = doc.uri,
                            sizeBytes = doc.length(),
                            mimeType = doc.type,
                            lastModified = doc.lastModified(),
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Silently return empty on access error
        }
        files
            .filter { matchesCollection(it, collectionTitle) }
            .sortedByDescending { it.lastModified }
    }
}

private fun matchesCollection(file: CiyatoFile, title: String): Boolean {
    val name = file.name.lowercase()
    val mime = file.mimeType.orEmpty().lowercase()
    val isDocument = mime.contains("document") || mime.contains("word") ||
        mime.contains("spreadsheet") || mime.contains("excel") ||
        mime.contains("presentation") || mime == "application/pdf" ||
        name.endsWith(".doc") || name.endsWith(".docx") || name.endsWith(".xls") ||
        name.endsWith(".xlsx") || name.endsWith(".ppt") || name.endsWith(".pptx")

    return when (title) {
        "Screenshots" -> name.contains("screenshot") || name.contains("screen_shot")
        "Documents" -> isDocument || mime.startsWith("text/")
        "Photos" -> mime.startsWith("image/")
        "WhatsApp", "WhatsApp Media" -> name.contains("whatsapp") || name.startsWith("wa")
        "Videos" -> mime.startsWith("video/")
        "APKs" -> mime == "application/vnd.android.package-archive" || name.endsWith(".apk")
        "Work Files" -> isDocument
        "Receipts" -> listOf("receipt", "invoice", "bill", "payment").any(name::contains)
        "PDFs" -> mime == "application/pdf" || name.endsWith(".pdf")
        "Contracts" -> listOf("contract", "agreement", "nda").any(name::contains)
        "Screen Recordings" -> mime.startsWith("video/") &&
            listOf("screen", "recording", "record").any(name::contains)
        "Design Assets" -> mime.startsWith("image/") ||
            listOf(".psd", ".ai", ".fig", ".sketch", ".xd").any(name::endsWith)
        "Travel" -> listOf("travel", "flight", "hotel", "ticket", "trip", "itinerary").any(name::contains)
        "College" -> listOf("college", "class", "lecture", "assignment", "semester", "notes").any(name::contains)
        "Recently Added" -> file.lastModified > 0L &&
            file.lastModified >= System.currentTimeMillis() - 7L * 24L * 60L * 60L * 1000L
        else -> true
    }
}
