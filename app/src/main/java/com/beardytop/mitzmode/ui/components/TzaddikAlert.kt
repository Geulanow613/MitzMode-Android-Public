package com.beardytop.mitzmode.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TzaddikAlert(onDismiss: () -> Unit) {
    var currentColor by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        // Show for 3 seconds
        delay(3000)
        onDismiss()
    }
    
    // Rainbow animation
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(500),
                repeatMode = RepeatMode.Reverse
            )
        ) { value, _ ->
            currentColor = value
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tzaddik Alert!!!",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.hsv(currentColor * 360f, 1f, 1f),
            modifier = Modifier.padding(16.dp)
        )
    }
} 