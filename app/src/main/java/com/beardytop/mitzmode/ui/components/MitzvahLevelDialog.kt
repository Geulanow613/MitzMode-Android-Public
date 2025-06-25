package com.beardytop.mitzmode.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll

@Composable
fun MitzvahLevelDialog(
    count: Int,
    currentLevel: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
        containerColor = Color.Black,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .border(
                width = 2.dp,
                color = Color(0xFFFFD700),
                shape = RoundedCornerShape(16.dp)
            ),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Certificate of\nAchievement",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp
                    ),
                    color = Color(0xFFFFD700)
                )
                
                HorizontalDivider(
                    modifier = Modifier.width(100.dp),
                    color = Color(0xFFFFD700),
                    thickness = 2.dp
                )
                
                Text(
                    text = "📜",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 36.sp
                    ),
                    color = Color(0xFFFFD700)
                )
                
                Text(
                    text = "This document certifies that\nhave completed",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    ),
                    color = Color.White
                )
                
                Text(
                    text = if (count == 1) "1 Mitzvah" else "$count Mitzvot",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        textAlign = TextAlign.Center
                    ),
                    color = Color(0xFFFFD700)
                )
                
                Text(
                    text = "Current Level:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                
                Box(
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = Color(0xFFFFD700),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = currentLevel,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFFD700)
                    )
                }
                
                HorizontalDivider(
                    modifier = Modifier.width(100.dp),
                    color = Color(0xFFFFD700),
                    thickness = 2.dp
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val numStars = when (currentLevel) {
                        "Beginner" -> 1
                        "Ba'al Teshuva" -> 2
                        "Master Cholent Chef" -> 3
                        "Aspiring Kiddush Maker" -> 4
                        "Assistant Gabbai" -> 5
                        "Guy who hands out candy at shul" -> 6
                        "Western Wall Reveler" -> 7
                        "Sofer" -> 8
                        "Tzaddik" -> 9
                        "Living Sefer Torah" -> 10
                        "Eliyahu HaNavi" -> 11
                        "King David" -> 12
                        "Moshiach!!!" -> 13
                        else -> 1
                    }
                    
                    repeat(numStars) {
                        Text(
                            text = "✡",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 36.sp
                            ),
                            color = Color(0xFFFFD700),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF000033)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text("Close")
            }
        }
    )
} 