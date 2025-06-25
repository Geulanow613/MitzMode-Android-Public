package com.beardytop.mitzmode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MitzvahInfoDialog(
    onDismiss: () -> Unit
) {
    var fontSize by remember { mutableStateOf(16f) }
    val scrollState = rememberScrollState()
    
    val transformableState = rememberTransformableState { zoomChange, _, _ ->
        fontSize = (fontSize * zoomChange).coerceIn(12f, 40f)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("What's a Mitzvah?") },
        text = {
            Box(
                modifier = Modifier
                    .transformable(transformableState)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = """The word mitzvah (מִצְוָה) literally means "commandment." In Judaism, mitzvot are the 613 commandments given by G-d through the Torah, and also some extra mitzvot which our rabbis, through Divine inspiration, added on. 

While mitzvah means commandment, it also carries the deeper meaning of "connection." By performing a mitzvah, we fulfill G-d's will and connect with the Divine. It's like following instructions from a loved one; by doing so, you strengthen your bond with them. 

G-d is constantly sending down pure Heavenly Light which sustains the world and everything in it. If you go against His will, it's like putting an umbrella between yourself and this Light. But by performing mitzvot you can connect with the Heavenly Goodness that is G-d Himself, and experience Heaven on Earth- in a state you might like to call "Mitz Mode." It might not happen right away, but do a few mitzvot and see how you feel. This is just the beginning...""",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = fontSize.sp
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
} 