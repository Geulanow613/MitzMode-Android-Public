package com.beardytop.mitzmode.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.beardytop.mitzmode.R
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import android.os.PowerManager
import android.util.Log
import android.os.Build
import android.app.Notification
import android.content.pm.ServiceInfo

class TimerService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var timeLeftMillis: Long = 0
    private var isPaused = false
    private var timerJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TimerService", "onStartCommand: ${intent?.action}")
        when (intent?.action) {
            ACTION_START_TIMER -> {
                val timeMillis = intent.getLongExtra(EXTRA_TIME_MILLIS, 0)
                Log.d("TimerService", "Starting timer with $timeMillis ms")
                timeLeftMillis = timeMillis
                isPaused = false
                isServiceRunning = true
                
                // Acquire wake lock
                acquireWakeLock()
                
                // Start foreground service with notification
                startForeground(
                    NOTIFICATION_ID, 
                    createNotification(timeMillis),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
                
                // Start timer
                startTimer(timeMillis)
            }
            ACTION_PAUSE -> {
                isPaused = true
                updateNotification()
            }
            ACTION_RESUME -> {
                isPaused = false
                updateNotification()
            }
            ACTION_STOP -> {
                stopTimer()
            }
        }
        return START_STICKY
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MitzMode::TimerWakeLock"
        ).apply {
            acquire(timeLeftMillis + 1000) // Add 1 second buffer
        }
    }

    private fun startTimer(timeMillis: Long) {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            Log.d("TimerService", "Timer started with $timeMillis ms")
            timeLeftMillis = timeMillis
            while (timeLeftMillis > 0) {
                if (!isPaused) {
                    currentTimeMillis = timeLeftMillis
                    // Send broadcast
                    sendBroadcast(Intent(BROADCAST_TIME_UPDATE).apply {
                        putExtra(EXTRA_TIME_MILLIS, timeLeftMillis)
                        putExtra("is_paused", isPaused)
                    })
                    // Update notification on a different coroutine
                    launch(Dispatchers.Default) {
                        updateNotification()
                    }
                    Log.d("TimerService", "Timer tick: ${timeLeftMillis/1000}s remaining")
                    delay(1000)
                    timeLeftMillis -= 1000
                } else {
                    delay(100)
                }
            }
            Log.d("TimerService", "Timer completed")
            withContext(Dispatchers.Main) {
                showCompletionNotification()
                stopSelf()
            }
        }
    }

    private fun createNotification(timeMillis: Long): Notification {
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setShowBadge(true)
            }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }

        val hours = TimeUnit.MILLISECONDS.toHours(timeMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis) % 60

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Kashrut Timer")
            .setContentText(String.format("%02d:%02d:%02d remaining", hours, minutes, seconds))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification() {
        val notification = createNotification(timeLeftMillis)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun stopTimer() {
        Log.d("TimerService", "Stopping timer")
        timerJob?.cancel()
        timerJob = null
        isServiceRunning = false
        currentTimeMillis = 0
        wakeLock?.release()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TimerService", "Service being destroyed")
        timerJob?.cancel()
        serviceScope.cancel()
        isServiceRunning = false
        wakeLock?.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "timer_channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_START_TIMER = "START_TIMER"
        const val ACTION_STOP = "STOP_TIMER"
        const val ACTION_PAUSE = "PAUSE_TIMER"
        const val ACTION_RESUME = "RESUME_TIMER"
        const val EXTRA_TIME_MILLIS = "time_millis"
        const val BROADCAST_TIME_UPDATE = "com.beardytop.mitzmode.TIME_UPDATE"
        const val BROADCAST_SERVICE_STOPPED = "com.beardytop.mitzmode.SERVICE_STOPPED"

        @Volatile private var isServiceRunning = false
        @Volatile private var currentTimeMillis: Long = 0

        fun getServiceRunning() = isServiceRunning
        fun getCurrentTimeMillis() = currentTimeMillis
    }

    private fun showCompletionNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "timer_complete",
                "Timer Complete",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                enableLights(true)
            }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "timer_complete")
            .setContentTitle("Timer Complete!")
            .setContentText("You can now eat dairy/meat")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)  // Cancel the ongoing notification
        notificationManager.notify(2, notification)  // Show completion notification
    }
} 