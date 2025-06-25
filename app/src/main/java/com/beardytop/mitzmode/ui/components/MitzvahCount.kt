package com.beardytop.mitzmode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MitzvahCount(
    count: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val neonGreen = Color(0xFF32CD32)  // Darker shade of neon green

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(onClick = onDismiss)
    ) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 128.dp, end = 16.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = neonGreen
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-166).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Count display
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                ),
                color = neonGreen,
                fontSize = 96.sp
            )
            
            // Subtitle
            Text(
                text = "Mitzvot on your record",
                style = MaterialTheme.typography.titleMedium,
                color = neonGreen,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Mazel Tov text at 75% down the screen
        Text(
            text = "Mazel Tov!",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = neonGreen,
            fontSize = 48.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 236.dp)
        )
    }
} 