package com.ciyato.launcher.ui.components

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*

/**
 * MediaControlsWidget — Suggestion #55
 * Shows currently playing track with playback controls.
 * Requires BIND_NOTIFICATION_LISTENER_SERVICE permission (shared with CiyatoNotificationListener).
 */

data class MediaState(
    val title: String? = null,
    val artist: String? = null,
    val albumArt: android.graphics.Bitmap? = null,
    val isPlaying: Boolean = false,
    val controller: MediaController? = null,
)

@Composable
fun rememberMediaState(): State<MediaState> {
    val context = LocalContext.current
    val state = remember { mutableStateOf(MediaState()) }

    LaunchedEffect(Unit) {
        try {
            val msm = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            val cn = ComponentName(context, CiyatoNotificationListener::class.java)
            val controllers = msm.getActiveSessions(cn)
            val active = controllers.firstOrNull()
            if (active != null) {
                val metadata = active.metadata
                val isPlaying = active.playbackState?.state == PlaybackState.STATE_PLAYING
                state.value = MediaState(
                    title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE),
                    artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST),
                    albumArt = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART),
                    isPlaying = isPlaying,
                    controller = active,
                )
            }
        } catch (_: Exception) {}
    }

    return state
}

@Composable
fun MediaControlsWidget(modifier: Modifier = Modifier) {
    val media by rememberMediaState()

    if (media.title == null && media.artist == null) return

    Card(
        colors = CardDefaults.cardColors(containerColor = CiyatoBgEl),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.semantics {
            contentDescription = "Now playing: ${media.title ?: "Unknown"} by ${media.artist ?: "Unknown"}"
        },
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E2128)),
                contentAlignment = Alignment.Center,
            ) {
                Text("🎵", fontSize = 22.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    media.title ?: "Unknown",
                    color = CiyatoWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    media.artist ?: "Unknown Artist",
                    color = CiyatoMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                MediaButton(icon = { Icon(Icons.Default.SkipPrevious, "Previous", tint = CiyatoSec, modifier = Modifier.size(20.dp)) }) {
                    media.controller?.transportControls?.skipToPrevious()
                }
                MediaButton(
                    highlight = true,
                    icon = {
                        Icon(
                            if (media.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            if (media.isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                ) {
                    if (media.isPlaying) media.controller?.transportControls?.pause()
                    else media.controller?.transportControls?.play()
                }
                MediaButton(icon = { Icon(Icons.Default.SkipNext, "Next", tint = CiyatoSec, modifier = Modifier.size(20.dp)) }) {
                    media.controller?.transportControls?.skipToNext()
                }
            }
        }
    }
}

@Composable
private fun MediaButton(
    highlight: Boolean = false,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (highlight) CiyatoGold else Color.Transparent),
    ) {
        icon()
    }
}
