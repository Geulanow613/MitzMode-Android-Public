package com.beardytop.mitzmode.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.alpha
import com.beardytop.mitzmode.data.Mitzvah
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.zIndex
import androidx.compose.ui.window.DialogProperties
import kotlin.random.Random
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.beardytop.mitzmode.util.DeviceCapabilityChecker
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import com.beardytop.mitzmode.utils.openUrl
import androidx.compose.ui.unit.TextUnit

@Composable
fun SparkleText(
    text: String,
    isAnimating: Boolean,
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 16.sp
) {
    val context = LocalContext.current
    val isCapableDevice = DeviceCapabilityChecker.canHandleVideoBackground(context)
    
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(1000)
            onAnimationComplete()
        }
    }
    
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = text,
            modifier = modifier
                .alpha(if (isAnimating) 0f else 1f)
                .graphicsLayer(
                    scaleX = if (isAnimating) 1.1f else 1f,
                    scaleY = if (isAnimating) 1.1f else 1f
                ),
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center,
                fontSize = textSize
            ),
            color = Color.White,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible
        )

        if (isAnimating && isCapableDevice) {
            val particles = remember { List(5) { Random.nextFloat() } }
            particles.forEach { offset ->
                val transition = rememberInfiniteTransition(label = "sparkle")
                val particleAlpha by transition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "alpha"
                )
                val particleOffset by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = -50f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "offset"
                )
                
                Text(
                    text = "✡",
                    modifier = Modifier
                        .offset(
                            x = (offset * 50 - 25).dp,
                            y = particleOffset.dp
                        )
                        .alpha(particleAlpha),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitzvahDialog(
    mitzvah: Mitzvah,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    onNext: () -> Unit
) {
    var accepted by remember(mitzvah.id) { mutableStateOf(false) }
    var isSparkleAnimating by remember { mutableStateOf(false) }
    var textSize by remember { mutableStateOf(16.sp) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Reset scroll position when mitzvah changes
    LaunchedEffect(mitzvah.id) {
        scrollState.scrollTo(0)
    }
    
    // Get screen width and calculate responsive text sizes
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val buttonTextSize = when {
        screenWidth < 360 -> 12
        screenWidth < 400 -> 14
        else -> 16
    }.sp
    
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(0.85f),
        title = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Text size controls
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { 
                            textSize = (textSize.value - 2).coerceAtLeast(12f).sp
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("a", style = MaterialTheme.typography.labelMedium)
                    }
                    IconButton(
                        onClick = { 
                            textSize = (textSize.value + 2).coerceAtMost(20f).sp
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("A", style = MaterialTheme.typography.labelLarge)
                    }
                }

                // Close button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = mitzvah.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = textSize,
                            lineHeight = textSize.value.times(1.5f).sp
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    mitzvah.links.orEmpty().forEach { link ->
                        Text(
                            text = link.displayText.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = textSize,
                                lineHeight = textSize.value.times(1.5f).sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clickable { context.openUrl(link.url.orEmpty()) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { 
                        isSparkleAnimating = true
                        onAccept()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    enabled = !isSparkleAnimating && !accepted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (accepted) Color.Gray else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    SparkleText(
                        text = if (accepted) "Accepted" else "Accept",
                        isAnimating = isSparkleAnimating,
                        onAnimationComplete = {
                            isSparkleAnimating = false
                            accepted = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textSize = buttonTextSize
                    )
                }

                TextButton(
                    onClick = onNext,
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = buttonTextSize
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }
        }
    )
} 