package com.beardytop.mitzmode.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beardytop.mitzmode.data.BirkatHamazonText

@Composable
fun BirkatHamazon(
    onDismiss: () -> Unit
) {
    var currentSectionIndex by remember { mutableStateOf(0) }
    var showEnglish by remember { mutableStateOf(false) }
    val sections = BirkatHamazonText.sections
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ברכת המזון", textAlign = TextAlign.Center)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { showEnglish = !showEnglish }) {
                        Text(if (showEnglish) "Hide English" else "Show English")
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                sections.getOrNull(currentSectionIndex)?.let { section ->
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text(
                        text = section.hebrew,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showEnglish && section.english != null) {
                        Text(
                            text = section.english,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { currentSectionIndex = (currentSectionIndex - 1).coerceAtLeast(0) },
                        enabled = currentSectionIndex > 0
                    ) {
                        Text("Previous")
                    }
                    TextButton(
                        onClick = { currentSectionIndex = (currentSectionIndex + 1).coerceAtMost(sections.size - 1) },
                        enabled = currentSectionIndex < sections.size - 1
                    ) {
                        Text("Next")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
} 