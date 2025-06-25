package com.beardytop.mitzmode.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.beardytop.mitzmode.viewmodel.MitzModeViewModel
import com.beardytop.mitzmode.ui.components.*
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import kotlin.random.Random
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material.icons.filled.CheckCircle
import com.beardytop.mitzmode.utils.DeviceCapabilityChecker
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.beardytop.mitzmode.R
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
fun MitzModeApp(
    viewModel: MitzModeViewModel = hiltViewModel()
) {
    val currentMitzvah by viewModel.currentMitzvah.collectAsState()
    val showVideo by viewModel.showVideo.collectAsState()
    val showLevelUp by viewModel.showLevelUp.collectAsState()
    println("DEBUG: showVideo value: $showVideo")
    var showMitzvahInfo by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showMitzvahCount by remember { mutableStateOf(false) }
    var showThankYou by remember { mutableStateOf(false) }
    var showBirkatHamazon by remember { mutableStateOf(false) }
    var showTefilat by remember { mutableStateOf(false) }
    var showBrachot by remember { mutableStateOf(false) }
    var showAddMitzvah by remember { mutableStateOf(false) }
    var showDailyMitzvot by remember { mutableStateOf(false) }
    var showMitzvahLevel by remember { mutableStateOf(false) }
    var isSparkleAnimating by remember { mutableStateOf(false) }
    
    // Add this to track lifecycle and video state
    val lifecycleOwner = LocalLifecycleOwner.current
    var isVideoPlaying by remember { mutableStateOf(true) }
    // Add state to track if video has played once
    var hasVideoPlayedOnce by remember { mutableStateOf(false) }

    // Add this to track if we're coming from notification
    val isFromNotification = remember {
        mutableStateOf(false)
    }

    // Add state for notification message
    var showNotificationMessage by remember { mutableStateOf(false) }

    // Use key to force video restart
    var videoKey by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val isCapableDevice = remember { DeviceCapabilityChecker.canHandleVideoBackground(context) }

    // Handle app lifecycle changes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // Reset video play state on resume
                    hasVideoPlayedOnce = false
                    isVideoPlaying = true
                    videoKey++
                }
                Lifecycle.Event.ON_PAUSE -> {
                    isVideoPlaying = false
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(showThankYou) {
        if (showThankYou) {
            delay(2500)
            showThankYou = false
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // First layer: Background and content
        GradientBackground(
            startColor = MaterialTheme.colorScheme.background,
            endColor = MaterialTheme.colorScheme.surface
        ) {
            // Use key to force recomposition
            key(videoKey) {
                if (isCapableDevice) {
                    VideoBackground(
                        videoAsset = "background.mp4",
                        isPlaying = isVideoPlaying,
                        onVideoComplete = {
                            if (!hasVideoPlayedOnce) {
                                hasVideoPlayedOnce = true
                                isVideoPlaying = false
                            }
                        }
                    )
                } else {
                    val context = LocalContext.current
                    val assetManager = context.assets
                    val inputStream = remember { assetManager.open("starbg.png") }
                    val bitmap = remember { BitmapFactory.decodeStream(inputStream) }
                    
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
            
            // Main content (just the button)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(
                        top = 80.dp,
                        start = 32.dp,
                        end = 32.dp
                    )
                ) {
                    Text(
                        text = "Tap the \"Mitzvah Me\"\nbutton for a mitzvah!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDAA520),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Second layer: Dialogs
        if (currentMitzvah != null) {
            MitzvahDialog(
                mitzvah = currentMitzvah!!,
                onDismiss = { viewModel.clearCurrentMitzvah() },
                onAccept = {
                    viewModel.onMitzvahAccepted()
                    isSparkleAnimating = true
                    println("DEBUG: Mitzvah accepted!")
                },
                onNext = { viewModel.onMitzvahButtonPressed() }
            )
        }

        // Third layer: Always-clickable controls
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            contentColor = LocalContentColor.current
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Menu in top left
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 8.dp)
                        .align(Alignment.TopStart)
                ) {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Daily Mitzvot Checklist") },
                            onClick = {
                                showMenu = false
                                showDailyMitzvot = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                showMenu = false
                                showAbout = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Blessing After Meals") },
                            onClick = {
                                showMenu = false
                                showBirkatHamazon = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Traveler's Prayer") },
                            onClick = {
                                showMenu = false
                                showTefilat = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Blessings") },
                            onClick = {
                                showMenu = false
                                showBrachot = true
                            }
                        )
                    }
                }

                // Mitzvah count in top right
                val mitzvotCount = viewModel.completedMitzvot.value.size
                if (mitzvotCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 16.dp, end = 16.dp)
                            .clickable { showMitzvahLevel = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed Mitzvot",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "$mitzvotCount",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                }

                // Center Mitzvah Button
                val buttonModifier = if (isCapableDevice) {
                    Modifier
                        .fillMaxSize()
                        .offset(y = (-24).dp)
                } else {
                    Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                }

                Box(
                    modifier = buttonModifier,
                    contentAlignment = Alignment.Center
                ) {
                    MitzvahButton(
                        onClick = { 
                            viewModel.onMitzvahButtonPressed()
                        }
                    )
                }

                // Add a Mitzvah button
                Button(
                    onClick = { showAddMitzvah = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 72.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Add a Mitzvah")
                }

                // What's a Mitzvah button
                Button(
                    onClick = { showMitzvahInfo = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDAA520)  // Golden color to match the text above
                    )
                ) {
                    Text("What's a Mitzvah?")
                }
            }
        }

        // Fourth layer: Dialogs
        if (showAbout) {
            AboutDialog(
                onDismiss = { showAbout = false }
            )
        }
        
        showVideo?.let { videoNumber ->
            RewardVideoDialog(
                videoNumber = videoNumber,
                onDismiss = viewModel::onVideoComplete
            )
        }
        
        showLevelUp?.let { level ->
            LevelUpAnimation(
                level = level,
                onDismiss = viewModel::onLevelUpComplete
            )
        }
        
        // Show thank you message
        AnimatedVisibility(
            visible = showThankYou,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "Thanks for keeping it holy!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 64.dp)
                )
            }
        }
        
        if (showMitzvahInfo) {
            MitzvahInfoDialog(
                onDismiss = { showMitzvahInfo = false }
            )
        }

        if (showBirkatHamazon) {
            BirkatHamazonDialog(
                onDismiss = { showBirkatHamazon = false }
            )
        }

        if (showTefilat) {
            TefilatHaderechDialog(onDismiss = { showTefilat = false })
        }

        if (showBrachot) {
            BrachotDialog(onDismiss = { showBrachot = false })
        }
        
        // Show checklist dialog when selected from menu
        if (showDailyMitzvot) {
            DailyMitzvotChecklist(
                onDismiss = { showDailyMitzvot = false }
            )
        }
        
        // Show level dialog when clicking mitzvah count
        if (showMitzvahLevel) {
            val count = viewModel.completedMitzvot.value.size
            val currentLevel = when (count) {
                in 1..9 -> "Beginner"
                in 10..49 -> "Ba'al Teshuva"
                in 50..99 -> "Master Cholent Chef"
                in 100..199 -> "Aspiring Kiddush Maker"
                in 200..299 -> "Assistant Gabbai"
                in 300..399 -> "Guy who hands out candy at shul"
                in 400..499 -> "Western Wall Reveler"
                in 500..599 -> "Sofer"
                in 600..699 -> "Tzaddik"
                in 700..799 -> "Living Sefer Torah"
                in 800..899 -> "Eliyahu HaNavi"
                in 900..999 -> "King David"
                in 1000..Int.MAX_VALUE -> "Moshiach!!!"
                else -> "Beginner"  // Default case, should never happen
            }
            MitzvahLevelDialog(
                count = count,
                currentLevel = currentLevel,
                onDismiss = { showMitzvahLevel = false }
            )
        }

        if (showAddMitzvah) {
            AddMitzvahDialog(
                onDismiss = { showAddMitzvah = false }
            )
        }
    }
} 