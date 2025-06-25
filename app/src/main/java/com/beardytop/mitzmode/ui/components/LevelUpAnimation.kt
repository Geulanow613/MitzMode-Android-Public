package com.beardytop.mitzmode.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext

@Composable
fun LevelUpAnimation(
    level: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentColorIndex by remember { mutableStateOf(0) }
    val rainbowColors = listOf(
        Color(0xFFFF0000), // Red
        Color(0xFFFF7F00), // Orange
        Color(0xFFFFFF00), // Yellow
        Color(0xFF00FF00), // Green
        Color(0xFF0000FF), // Blue
        Color(0xFF4B0082), // Indigo
        Color(0xFF9400D3)  // Violet
    )
    
    val textColor = rainbowColors[currentColorIndex]
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(100) // Change color every 100ms
            currentColorIndex = (currentColorIndex + 1) % rainbowColors.size
        }
    }
    
    // Auto-dismiss after 5 seconds
    LaunchedEffect(Unit) {
        delay(5000)
        onDismiss()
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "New Level Achieved!",
            style = MaterialTheme.typography.headlineLarge,
            color = textColor,
            fontSize = 36.sp,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = level,
            style = MaterialTheme.typography.headlineMedium,
            color = textColor,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )
    }
} 