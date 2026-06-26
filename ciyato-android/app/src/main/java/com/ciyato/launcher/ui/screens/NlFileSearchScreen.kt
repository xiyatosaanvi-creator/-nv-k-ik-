package com.ciyato.launcher.ui.screens

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * NlFileSearchScreen — Suggestion #27
 * Natural language file search: "payment screenshot from yesterday",
 * "photo from last week", "video from January".
 */

data class NlFileResult(
    val id: Long,
    val uri: Uri,
    val name: String,
    val mimeType: String,
    val dateMs: Long,
    val sizeBytes: Long,
)

data class ParsedQuery(
    val keywords: List<String>,
    val mimeType: String?,
    val dateRange: Pair<Long, Long>?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NlFileSearchScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<NlFileResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val quickQueries = listOf(
        "payment screenshot from yesterday",
        "photos from last week",
        "video from last month",
        "documents from today",
        "receipt photos",
    )

    suspend fun search(q: String) {
        if (q.isBlank()) return
        isSearching = true
        hasSearched = false
        focusManager.clearFocus()
        kotlinx.coroutines.delay(400)
        val parsed = parseNlQuery(q)
        results = searchFiles(context, parsed).take(50)
        isSearching = false
        hasSearched = true
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = { Text("Smart File Search", color = CiyatoWhite, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = CiyatoWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("e.g. payment screenshot from yesterday", color = CiyatoMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.AutoAwesome, null, tint = CiyatoGold, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        IconButton(onClick = { scope.launch { search(query) } }) {
                            Icon(Icons.Default.Search, null, tint = CiyatoSec)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CiyatoWhite,
                        unfocusedTextColor = CiyatoWhite,
                        focusedBorderColor = CiyatoGold,
                        unfocusedBorderColor = CiyatoBorder,
                        cursorColor = CiyatoGold,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        scope.launch { search(query) }
                    }),
                )
            }

            if (!hasSearched) {
                item {
                    Text("Try these", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
                items(quickQueries) { q ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CiyatoBgEl)
                            .clickable {
                                query = q
                                scope.launch { search(q) }
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Search, null, tint = CiyatoMuted, modifier = Modifier.size(16.dp))
                        Text(q, color = CiyatoSec, fontSize = 13.sp)
                    }
                }
            }

            if (isSearching) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CiyatoGold, strokeWidth = 3.dp)
                    }
                }
            }

            if (hasSearched && results.isEmpty() && !isSearching) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("No files found", color = CiyatoWhite, fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold)
                            Text("Try a different query like \"photos from last week\"", color = CiyatoMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            }

            if (hasSearched && results.isNotEmpty()) {
                item {
                    Text("${results.size} results", color = CiyatoMuted, fontSize = 13.sp)
                }
                items(results, key = { it.id }) { file ->
                    NlFileResultRow(file = file)
                }
            }
        }
    }
}

@Composable
private fun NlFileResultRow(file: NlFileResult) {
    val df = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val icon = when {
        file.mimeType.startsWith("image") -> "🖼"
        file.mimeType.startsWith("video") -> "🎬"
        file.mimeType.contains("pdf") -> "📄"
        file.mimeType.contains("audio") -> "🎵"
        else -> "📁"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CiyatoBgEl),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(icon, fontSize = 28.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(file.name, color = CiyatoWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Text(df.format(Date(file.dateMs)), color = CiyatoMuted, fontSize = 11.sp)
            }
            Text(formatBytes(file.sizeBytes), color = CiyatoSec, fontSize = 11.sp)
        }
    }
}

private fun parseNlQuery(query: String): ParsedQuery {
    val lower = query.lowercase()
    val now = System.currentTimeMillis()

    val dateRange: Pair<Long, Long>? = when {
        "today" in lower -> (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)) to now
        "yesterday" in lower -> {
            val yd = now - TimeUnit.DAYS.toMillis(1)
            (yd - TimeUnit.HOURS.toMillis(24)) to yd
        }
        "last week" in lower || "this week" in lower -> (now - TimeUnit.DAYS.toMillis(7)) to now
        "last month" in lower || "this month" in lower -> (now - TimeUnit.DAYS.toMillis(30)) to now
        "january" in lower -> dateRangeForMonth(Calendar.JANUARY)
        "february" in lower -> dateRangeForMonth(Calendar.FEBRUARY)
        "march" in lower -> dateRangeForMonth(Calendar.MARCH)
        "april" in lower -> dateRangeForMonth(Calendar.APRIL)
        "may" in lower -> dateRangeForMonth(Calendar.MAY)
        "june" in lower -> dateRangeForMonth(Calendar.JUNE)
        "july" in lower -> dateRangeForMonth(Calendar.JULY)
        "august" in lower -> dateRangeForMonth(Calendar.AUGUST)
        "september" in lower -> dateRangeForMonth(Calendar.SEPTEMBER)
        "october" in lower -> dateRangeForMonth(Calendar.OCTOBER)
        "november" in lower -> dateRangeForMonth(Calendar.NOVEMBER)
        "december" in lower -> dateRangeForMonth(Calendar.DECEMBER)
        else -> null
    }

    val mimeType: String? = when {
        "photo" in lower || "image" in lower || "screenshot" in lower || "picture" in lower -> "image"
        "video" in lower -> "video"
        "pdf" in lower || "document" in lower || "doc" in lower -> "application/pdf"
        "audio" in lower || "music" in lower -> "audio"
        else -> null
    }

    val keywords = lower.split(" ")
        .filter { it.length > 3 && it !in setOf("from", "last", "this", "the", "with", "that", "photo", "image") }

    return ParsedQuery(keywords, mimeType, dateRange)
}

private fun dateRangeForMonth(month: Int): Pair<Long, Long> {
    val cal = Calendar.getInstance()
    cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    val start = cal.timeInMillis
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    cal.set(Calendar.HOUR_OF_DAY, 23)
    return start to cal.timeInMillis
}

private fun searchFiles(context: Context, parsed: ParsedQuery): List<NlFileResult> {
    val results = mutableListOf<NlFileResult>()

    val collections = mutableListOf<Pair<Uri, String>>()
    when {
        parsed.mimeType?.startsWith("image") == true ->
            collections.add(MediaStore.Images.Media.EXTERNAL_CONTENT_URI to "image/*")
        parsed.mimeType?.startsWith("video") == true ->
            collections.add(MediaStore.Video.Media.EXTERNAL_CONTENT_URI to "video/*")
        parsed.mimeType?.startsWith("audio") == true ->
            collections.add(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI to "audio/*")
        else -> {
            collections.add(MediaStore.Images.Media.EXTERNAL_CONTENT_URI to "image/*")
            collections.add(MediaStore.Files.getContentUri("external") to "application/pdf")
        }
    }

    collections.forEach { (uri, mime) ->
        val proj = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE,
        )

        val selParts = mutableListOf<String>()
        val selArgs = mutableListOf<String>()

        if (parsed.dateRange != null) {
            val (start, end) = parsed.dateRange
            selParts.add("${MediaStore.Files.FileColumns.DATE_MODIFIED} >= ? AND ${MediaStore.Files.FileColumns.DATE_MODIFIED} <= ?")
            selArgs.add((start / 1000).toString())
            selArgs.add((end / 1000).toString())
        }

        parsed.keywords.filter { it.length > 3 }.take(2).forEach { kw ->
            selParts.add("${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE ?")
            selArgs.add("%$kw%")
        }

        val selection = selParts.joinToString(" AND ").takeIf { it.isNotBlank() }

        try {
            context.contentResolver.query(uri, proj, selection, selArgs.toTypedArray().takeIf { it.isNotEmpty() },
                "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC")?.use { cursor ->
                val idCol = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
                val nameCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val dateCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)
                val sizeCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)
                val mimeCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)

                while (cursor.moveToNext() && results.size < 50) {
                    val id = if (idCol >= 0) cursor.getLong(idCol) else continue
                    val name = if (nameCol >= 0) cursor.getString(nameCol) ?: continue else continue
                    val dateS = if (dateCol >= 0) cursor.getLong(dateCol) else 0L
                    val size = if (sizeCol >= 0) cursor.getLong(sizeCol) else 0L
                    val mimeStr = if (mimeCol >= 0) cursor.getString(mimeCol) ?: "" else ""
                    val fileUri = ContentUris.withAppendedId(uri, id)
                    results.add(NlFileResult(id, fileUri, name, mimeStr, dateS * 1000, size))
                }
            }
        } catch (_: Exception) {}
    }
    return results.distinctBy { it.id }
}
