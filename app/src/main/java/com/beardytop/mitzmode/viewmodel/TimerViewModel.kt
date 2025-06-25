package com.beardytop.mitzmode.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beardytop.mitzmode.R
import com.beardytop.mitzmode.data.FoodType
import com.beardytop.mitzmode.data.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    
    private var timerJob: Job? = null
    
    fun startTimer(foodType: FoodType, customDuration: Long? = null) {
        timerJob?.cancel()
        
        val duration = customDuration ?: when (foodType) {
            FoodType.MEAT -> 6 * 60 * 60 * 1000L  // 6 hours
            FoodType.DAIRY -> 30 * 60 * 1000L     // 30 minutes
        }
        
        val endTime = System.currentTimeMillis() + duration
        
        timerJob = viewModelScope.launch {
            showStartNotification(foodType)
            
            while (isActive) {
                val remaining = endTime - System.currentTimeMillis()
                if (remaining <= 0) {
                    showCompletionNotification(foodType)
                    break
                }
                _timerState.value = TimerState.Running(foodType, endTime, remaining)
                delay(1000)
            }
        }
    }
    
    fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState.Idle
        clearNotifications()
    }
    
    private fun showStartNotification(foodType: FoodType) {
        createNotificationChannel()
        
        val icon = when (foodType) {
            FoodType.MEAT -> R.drawable.ic_hamburger
            FoodType.DAIRY -> R.drawable.ic_milkshake
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Waiting after ${foodType.name.lowercase()}")
            .setContentText("Timer running...")
            .setSmallIcon(icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .build()
            
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
    
    private fun showCompletionNotification(foodType: FoodType) {
        val icon = when (foodType) {
            FoodType.MEAT -> R.drawable.ic_hamburger
            FoodType.DAIRY -> R.drawable.ic_milkshake
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Timer Complete!")
            .setContentText("You can now eat after your ${foodType.name.lowercase()}")
            .setSmallIcon(icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
            
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Meat/Dairy Timer",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Timer for waiting between meat and dairy"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun clearNotifications() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
    
    companion object {
        private const val CHANNEL_ID = "meat_dairy_timer"
        private const val NOTIFICATION_ID = 1
    }
} 