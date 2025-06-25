package com.beardytop.mitzmode.util

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import kotlin.math.roundToInt

class HebrewDayTimer {
    fun getTimeUntilSunset(): Long {
        // This is a simplified version - in a real app you'd want to use
        // actual sunset calculations based on location
        val now = LocalDateTime.now()
        val sunset = now.withHour(19).withMinute(30).withSecond(0)
        
        if (now.isAfter(sunset)) {
            return sunset.plusDays(1)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - System.currentTimeMillis()
        }
        
        return sunset
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - System.currentTimeMillis()
    }
    
    fun formatTimeRemaining(millis: Long): String {
        val hours = ((millis / (1000 * 60 * 60)).toDouble()).toInt()
        val minutes = (((millis % (1000 * 60 * 60)) / (1000 * 60)).toDouble()).toInt()
        return String.format("%02d:%02d", hours, minutes)
    }
} 