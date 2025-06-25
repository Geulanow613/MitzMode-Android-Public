package com.beardytop.mitzmode.ui.components

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.NotificationCompat
import com.beardytop.mitzmode.R
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import android.Manifest
import androidx.core.content.ContextCompat
import android.content.Intent
import com.beardytop.mitzmode.service.TimerService
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.widget.Toast
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerDialog(
    onDismiss: () -> Unit,
    onHide: () -> Unit,
    onStart: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("timer_prefs", Context.MODE_PRIVATE) }

    // Helper functions
    fun clearNotifications() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)  // Cancel the ongoing timer notification
    }

    fun saveWaitTime(isMeat: Boolean, time: Long) {
        prefs.edit().apply {
            if (isMeat) {
                putLong("meat_wait_time", time)
            } else {
                putLong("dairy_wait_time", time)
            }
            apply()
        }
    }

    // State
    var selectedTime by remember { 
        mutableStateOf(prefs.getLong("meat_wait_time", 360L))
    }
    var remainingTime by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var showFoodSelection by remember { mutableStateOf(!TimerService.getServiceRunning()) }

    // Check if service is running and get current time
    LaunchedEffect(Unit) {
        if (TimerService.getServiceRunning()) {
            isRunning = true
            remainingTime = TimerService.getCurrentTimeMillis() / 1000
            showFoodSelection = false
        }
    }

    // Update broadcast receiver to handle service updates
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    TimerService.BROADCAST_TIME_UPDATE -> {
                        val timeMillis = intent.getLongExtra(TimerService.EXTRA_TIME_MILLIS, 0)
                        remainingTime = timeMillis / 1000
                        isPaused = intent.getBooleanExtra("is_paused", false)
                        isRunning = timeMillis > 0
                        showFoodSelection = !isRunning
                    }
                    TimerService.BROADCAST_SERVICE_STOPPED -> {
                        isRunning = false
                        isPaused = false
                        remainingTime = selectedTime * 60
                        showFoodSelection = false
                    }
                }
            }
        }

        // Register with RECEIVER_NOT_EXPORTED flag for internal app broadcasts
        context.registerReceiver(
            receiver,
            IntentFilter().apply {
                addAction(TimerService.BROADCAST_TIME_UPDATE)
                addAction(TimerService.BROADCAST_SERVICE_STOPPED)
            },
            Context.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Dialog(
        onDismissRequest = {
            if (!isRunning) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = !isRunning,
            dismissOnClickOutside = !isRunning
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onHide,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, "Hide Timer")
                    }
                }
                
                Text(
                    text = when {
                        showFoodSelection -> "Meat/Dairy Timer"
                        selectedTime >= 360 -> "After-Meat Timer"
                        else -> "After-Dairy Timer"
                    },
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (showFoodSelection) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                val savedMeatTime = prefs.getLong("meat_wait_time", 360L)
                                selectedTime = savedMeatTime
                                remainingTime = selectedTime * 60
                                showFoodSelection = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_hamburger),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("I ate Meat")
                        }

                        Button(
                            onClick = {
                                val savedDairyTime = prefs.getLong("dairy_wait_time", 30L)
                                selectedTime = savedDairyTime
                                remainingTime = selectedTime * 60
                                showFoodSelection = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Blue
                            ),
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_milkshake),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("I ate Dairy")
                        }
                    }
                } else {
                    // Timer display
                    Text(
                        text = formatTime(remainingTime),
                        style = MaterialTheme.typography.displayLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Timer controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (!isRunning) {
                            Button(
                                onClick = {
                                    try {
                                        isRunning = true
                                        val serviceIntent = Intent(context, TimerService::class.java).apply {
                                            action = TimerService.ACTION_START_TIMER
                                            putExtra(TimerService.EXTRA_TIME_MILLIS, selectedTime * 60L * 1000L)
                                        }
                                        Log.d("TimerDialog", "Starting service with ${selectedTime * 60 * 1000}ms")
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            context.startForegroundService(serviceIntent)
                                        } else {
                                            context.startService(serviceIntent)
                                        }
                                        onStart()
                                    } catch (e: Exception) {
                                        Log.e("TimerDialog", "Error starting timer", e)
                                        Toast.makeText(
                                            context,
                                            "Failed to start timer: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        isRunning = false
                                    }
                                },
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.PlayArrow, "Start")
                                    Spacer(Modifier.width(8.dp))
                                    Text("Start")
                                }
                            }
                        } else {
                            Button(
                                onClick = { 
                                    isPaused = !isPaused
                                    // Send pause/resume command to service
                                    val serviceIntent = Intent(context, TimerService::class.java).apply {
                                        action = if (isPaused) TimerService.ACTION_PAUSE else TimerService.ACTION_RESUME
                                    }
                                    context.startService(serviceIntent)
                                },
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isPaused) {
                                        Icon(Icons.Default.PlayArrow, "Resume")
                                        Spacer(Modifier.width(8.dp))
                                        Text("Resume")
                                    } else {
                                        Icon(Icons.Default.Pause, "Pause")
                                        Spacer(Modifier.width(8.dp))
                                        Text("Pause")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Reset button
                        Button(
                            onClick = {
                                isRunning = false
                                isPaused = false
                                remainingTime = selectedTime * 60
                                
                                // First stop the current service and wait a moment
                                val stopIntent = Intent(context, TimerService::class.java).apply {
                                    action = TimerService.ACTION_STOP
                                }
                                context.stopService(stopIntent)
                                
                                // Don't automatically start a new service - let user press Start again
                                showFoodSelection = false  // Keep the timer view visible
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Reset Timer",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Cancel button
                        Button(
                            onClick = {
                                isRunning = false
                                // Stop the background service
                                val serviceIntent = Intent(context, TimerService::class.java).apply {
                                    action = TimerService.ACTION_STOP
                                }
                                context.stopService(serviceIntent)
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Cancel Timer",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    if (!isRunning) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    selectedTime = (selectedTime - 30).coerceAtLeast(30)
                                    remainingTime = selectedTime * 60
                                    saveWaitTime(selectedTime >= 360, selectedTime)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "-30 min",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    selectedTime += 30
                                    remainingTime = selectedTime * 60
                                    saveWaitTime(selectedTime >= 360, selectedTime)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+30 min",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add cleanup when dialog is dismissed
    DisposableEffect(Unit) {
        onDispose {
            if (!isRunning) {
                clearNotifications()
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hours = TimeUnit.SECONDS.toHours(seconds)
    val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}

private fun updateNotification(context: Context, remainingSeconds: Long, isMeat: Boolean) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    // Create channel for ongoing notification
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "kashrut_timer",
            "Kashrut Timer",
            NotificationManager.IMPORTANCE_LOW  // LOW importance for persistent notification
        ).apply {
            description = "Timer for waiting between meat and dairy"
            setShowBadge(false)  // Don't show badge for ongoing notification
        }
        notificationManager.createNotificationChannel(channel)
    }

    val foodType = if (isMeat) "dairy" else "meat"
    val icon = if (isMeat) R.drawable.ic_hamburger else R.drawable.ic_milkshake

    val notification = NotificationCompat.Builder(context, "kashrut_timer")
        .setContentTitle("Waiting to eat ${foodType}...")
        .setContentText("${formatTime(remainingSeconds)} remaining")
        .setSmallIcon(icon)
        .setOngoing(true)  // Makes notification persistent
        .setAutoCancel(false)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOnlyAlertOnce(true)
        .build()

    notificationManager.notify(1, notification)
}

private fun showCompletionNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    // First, remove the ongoing notification
    notificationManager.cancel(1)
    
    // Create completion notification channel
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "kashrut_timer_complete",
            "Timer Complete",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            enableLights(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, "kashrut_timer_complete")
        .setContentTitle("Timer Complete! 🎉")
        .setContentText("Now you can eat whatever you want!")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setVibrate(longArrayOf(0, 250, 250, 250))
        .build()

    try {
        notificationManager.notify(2, notification)
    } catch (e: SecurityException) {
        // Handle permission denied
    }
} 