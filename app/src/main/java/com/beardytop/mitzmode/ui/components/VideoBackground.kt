package com.beardytop.mitzmode.ui.components

import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

private const val TAG = "VideoBackground"

@Composable
fun VideoBackground(
    videoAsset: String,
    isPlaying: Boolean,
    onVideoReady: () -> Unit = {},
    onVideoComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    var isVideoReady by remember { mutableStateOf(false) }
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    val scope = rememberCoroutineScope()
    
    // Initialize ExoPlayer in a LaunchedEffect on the main thread
    LaunchedEffect(Unit) {
        // Create the player on the main thread
        val player = ExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(false)
            .build()
            
        // Configure data source and media item
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri("asset:///$videoAsset"))
        
        player.apply {
            setMediaSource(source)
            // Set playback properties
            volume = 0f
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = isPlaying
            
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    scope.launch(Dispatchers.Main) {
                        when (playbackState) {
                            Player.STATE_READY -> {
                                Log.d(TAG, "Video ready")
                                isVideoReady = true
                                onVideoReady()
                                // Ensure video starts playing if isPlaying is true
                                if (isPlaying) {
                                    play()
                                }
                            }
                            Player.STATE_ENDED -> {
                                Log.d(TAG, "Video completed")
                                onVideoComplete()
                            }
                            Player.STATE_BUFFERING -> {
                                Log.d(TAG, "Video buffering")
                            }
                            Player.STATE_IDLE -> {
                                Log.d(TAG, "Video idle")
                            }
                        }
                    }
                }
                
                override fun onPlayerError(error: PlaybackException) {
                    Log.e(TAG, "Video playback error: ${error.message}", error)
                }
            })
        }
        
        // Prepare the player
        player.prepare()
        exoPlayer = player
    }

    DisposableEffect(Unit) {
        onDispose {
            scope.launch(Dispatchers.Main) {
                exoPlayer?.release()
                exoPlayer = null
            }
        }
    }

    // Handle play/pause state changes
    LaunchedEffect(isPlaying) {
        exoPlayer?.let { player ->
            if (isPlaying) {
                player.play()
            } else {
                player.pause()
            }
        }
    }

    exoPlayer?.let { player ->
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    setPlayer(player)
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    setShutterBackgroundColor(Color.TRANSPARENT)
                    setBackgroundColor(Color.TRANSPARENT)
                    alpha = 0f
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { playerView ->
            playerView.alpha = if (isVideoReady) 1f else 0f
        }
    }
} 