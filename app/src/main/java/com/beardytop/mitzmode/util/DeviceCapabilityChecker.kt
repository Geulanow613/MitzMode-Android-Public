package com.beardytop.mitzmode.util

import android.app.ActivityManager
import android.content.Context
import android.os.Build

object DeviceCapabilityChecker {
    fun canHandleVideoBackground(context: Context): Boolean {
        // Check Android version first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {  // Android 11 or higher
            return true
        }

        // For older versions, check available memory
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        // Convert to GB
        val totalMemoryGB = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        
        // Return true if device has at least 3GB RAM and running Android 9 or higher
        return totalMemoryGB >= 3.0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }
} 