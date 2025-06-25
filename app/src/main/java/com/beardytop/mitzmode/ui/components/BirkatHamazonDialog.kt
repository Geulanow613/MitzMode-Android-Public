package com.beardytop.mitzmode.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.beardytop.mitzmode.data.BirkatHamazonText
import com.beardytop.mitzmode.data.BirkatSection
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun BirkatHamazonDialog(
    onDismiss: () -> Unit
) {
    var showEnglish by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val sections = BirkatHamazonText.sections
    var fontScale by remember { mutableStateOf(1f) }
    
    val transformableState = rememberTransformableState { zoomChange, _, _ ->
        fontScale = (fontScale * zoomChange).coerceIn(0.5f, 3f)
    }

    val scaledFontSize = (MaterialTheme.typography.bodyLarge.fontSize.value * fontScale).sp

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with close button and title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    
                    Text(
                        text = "ברכת המזון",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }

                // Toggle buttons for English and zoom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { showEnglish = !showEnglish }) {
                        Text(if (showEnglish) "Hide English" else "Show English")
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { fontScale = (fontScale - 0.2f).coerceIn(0.5f, 3f) }
                        ) {
                            Text("A-", style = MaterialTheme.typography.titleMedium)
                        }
                        IconButton(
                            onClick = { fontScale = (fontScale + 0.2f).coerceIn(0.5f, 3f) }
                        ) {
                            Text("A+", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                // Scrollable content with font scaling
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .transformable(transformableState)
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        sections.forEach { section ->
                            Text(
                                text = section.hebrew,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = scaledFontSize
                                ),
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (showEnglish && section.english != null) {
                                Text(
                                    text = section.english,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = (MaterialTheme.typography.bodyMedium.fontSize.value * fontScale).sp
                                    ),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BirkatSectionItem(
    section: BirkatSection,
    showEnglish: Boolean
) {
    Column {
        Text(
            text = section.title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = section.hebrew.replace(
                "[HOLIDAY_INSERT]",
                ""
            ),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )

        if (showEnglish) {
            Text(
                text = section.english?.replace(
                    "[HOLIDAY_INSERT]",
                    ""
                ) ?: "",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

// Update the Hebrew text to include Yaaleh Veyavo and holiday insertions:
val birkatHamazonText = """
    ... (previous text)

    אלהינו ואלהי אבותינו יעלה ויבא ויגיע ויראה וירצה וישמע ויפקד ויזכר כרוננו ופקדוננו וזכרון אבותינו וזכרון משיח בן דוד עבדך וזכרון ירושלים עיר קדשך וזכרון כל עמך בית ישראל לפניך לפלטה לטובה לחן ולחסד ולרחמי לחיים ולשלום
    ביום ראש החדש הזה / ביום חג המצות הזה / ביום חג הסכות הזה
    זכרנו ה' אלהינו בו לטובה ופקדנו בו לברכה והושיענו בו לחיים ובדבר ישועה ורחמים חוס וחננו ורחם עלינו והושיענ�� כי אליך עינינו כי אל מלך חנון ורחום אתה

    ... (rest of text)
"""

// Add a helper function to determine which holiday text to show:
fun getHolidayInsertion(isRoshChodesh: Boolean = false, isPesach: Boolean = false, isSuccot: Boolean = false): String {
    return when {
        isRoshChodesh -> "ביום ראש החדש הזה"
        isPesach -> "ביום חג המצות הזה"
        isSuccot -> "ביום חג הסכות הזה"
        else -> ""
    }
} 