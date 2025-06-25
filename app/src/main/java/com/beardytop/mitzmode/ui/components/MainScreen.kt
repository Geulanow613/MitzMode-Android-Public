package com.beardytop.mitzmode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beardytop.mitzmode.data.Mitzvah

@Composable
fun MainScreen(
    currentMitzvah: Mitzvah?,
    mitzvahCount: Int,
    onMitzvahButtonPressed: () -> Unit,
    onShowMitzvahInfo: () -> Unit,
    onShowAbout: () -> Unit,
    onShowBirkatHamazon: () -> Unit,
    onShowTefilat: () -> Unit,
    onShowBrachot: () -> Unit,
    onShowDailyMitzvot: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var showSuggest by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showBirkatHamazon by remember { mutableStateOf(false) }
    var showTefilatHaderech by remember { mutableStateOf(false) }
    var showBrachot by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Three dots menu in the corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Daily Mitzvot Checklist") },
                    onClick = {
                        expanded = false
                        onShowDailyMitzvot()
                    }
                )
                DropdownMenuItem(
                    text = { Text("About") },
                    onClick = {
                        expanded = false
                        onShowAbout()
                    }
                )
                DropdownMenuItem(
                    text = { Text("ברכת המזון") },
                    onClick = {
                        expanded = false
                        onShowBirkatHamazon()
                    }
                )
                DropdownMenuItem(
                    text = { Text("תפילת הדרך") },
                    onClick = {
                        expanded = false
                        onShowTefilat()
                    }
                )
                DropdownMenuItem(
                    text = { Text("ברכות") },
                    onClick = {
                        expanded = false
                        onShowBrachot()
                    }
                )
            }
        }

        // Mitzvah counter in top right
        if (mitzvahCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        color = Color(0xFF424242),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Mitzvah Count",
                        tint = Color(0xFFFFD700),  // Golden color
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = mitzvahCount.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        // Main content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tap the\n\"Mitzvah Me\" button for a mitzvah!",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
            
            MitzvahButton(
                onClick = onMitzvahButtonPressed,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
} 