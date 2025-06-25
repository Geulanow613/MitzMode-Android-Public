package com.beardytop.mitzmode.ui.components

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun BlessedAnimation(
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    var currentColor by remember { mutableStateOf(0f) }
    var playbackError by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            currentColor = (currentColor + 0.02f) % 1f
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Video player
        val exoPlayer = remember {
            ExoPlayer.Builder(context).build().apply {
                try {
                    val assetFileDescriptor = context.assets.openFd("tzaddik.mp4")
                    setMediaItem(
                        MediaItem.fromUri(
                            Uri.parse("asset:///tzaddik.mp4")
                        )
                    )
                    prepare()
                    playWhenReady = true
                    
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_ENDED) {
                                onFinished()
                            }
                        }

                        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                            playbackError = true
                            error.printStackTrace()
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                    playbackError = true
                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }

        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false  // Hide the player controls
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Rainbow flashing text
        Text(
            text = "BLESSED!!",
            color = Color.hsv(currentColor * 360, 1f, 1f),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 128.dp)
        )

        if (playbackError) {
            Text(
                text = "Error playing video",
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
} 