package com.beardytop.mitzmode.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build

object DeviceCapabilityChecker {
    fun canHandleVideoBackground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        // Check for newer devices with sufficient memory
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> true // Android 11 and above
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && memoryInfo.totalMem >= 3L * 1024 * 1024 * 1024 -> true // 3GB RAM minimum for Android 9+
            else -> false
        }
    }
} 