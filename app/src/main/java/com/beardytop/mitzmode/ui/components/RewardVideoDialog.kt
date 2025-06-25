package com.beardytop.mitzmode.ui.components

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun RewardVideoDialog(
    videoNumber: Int,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember { createExoPlayer(context, videoNumber, onDismiss) }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    val levelTitle = when {
        videoNumber == 1 -> "Beginner"
        videoNumber == 2 -> "Ba'al Teshuva"
        videoNumber == 3 -> "Master Cholent Chef"
        videoNumber == 4 -> "Aspiring Kiddush Maker"
        videoNumber == 5 -> "Assistant Gabbai"
        videoNumber == 6 -> "Guy who hands out candy at shul"
        videoNumber == 7 -> "Western Wall Reveler"
        videoNumber == 8 -> "Sofer"
        videoNumber == 9 -> "Tzaddik"
        videoNumber == 10 -> "Living Sefer Torah"
        videoNumber == 11 -> "Eliyahu HaNavi"
        videoNumber == 12 -> "King David"
        videoNumber == 13 -> "Moshiach!!!"
        else -> ""  // Should never happen as we only call with valid video numbers
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            securePolicy = SecureFlagPolicy.SecureOn
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    android.widget.FrameLayout(context).apply {
                        setBackgroundColor(android.graphics.Color.BLACK)
                        layoutParams = WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        )
                        addView(PlayerView(context).apply {
                            player = exoPlayer
                            useController = false
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        })
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            LevelUpAnimation(
                level = levelTitle,
                onDismiss = {}
            )
        }
    }
}

private fun createExoPlayer(context: Context, videoNumber: Int, onComplete: () -> Unit): ExoPlayer {
    return ExoPlayer.Builder(context).build().apply {
        val assetPath = "asset:///mitzmodenew$videoNumber.mp4"
        setMediaItem(MediaItem.fromUri(assetPath))
        prepare()
        playWhenReady = true
        addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    onComplete()
                }
            }
        })
    }
} 