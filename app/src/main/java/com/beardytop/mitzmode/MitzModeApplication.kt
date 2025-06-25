package com.beardytop.mitzmode

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid
import io.sentry.protocol.User
import com.jakewharton.threetenabp.AndroidThreeTen

@HiltAndroidApp
class MitzModeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize ThreeTenABP first (doesn't require network)
        AndroidThreeTen.init(this)
        
        // Initialize Sentry only if we have network connection to avoid blocking
        if (isNetworkAvailable()) {
            initializeSentry()
        } else {
            Log.d("MitzModeApplication", "No network available, skipping Sentry initialization")
        }
    }
    
    private fun initializeSentry() {
        try {
            SentryAndroid.init(this) { options ->
                options.dsn = "https://9d18041059471fc2e7d7aabf542df3ef@o4508615756414976.ingest.us.sentry.io/4508615782891520"
                options.isEnableAutoSessionTracking = true
                // Set reasonable timeouts for network operations
                options.connectionTimeoutMillis = 3000
                options.readTimeoutMillis = 3000
            }
            
            // Add additional context
            Sentry.configureScope { scope ->
                // Add device info
                scope.setTag("device_manufacturer", android.os.Build.MANUFACTURER)
                scope.setTag("device_model", android.os.Build.MODEL)
                scope.setTag("android_version", android.os.Build.VERSION.RELEASE)
            }

            // Note: We'll add user tracking later when we implement user authentication
            // For now, we'll just track anonymous usage
            Sentry.setUser(User().apply {
                ipAddress = "{{auto}}"
                // You can also add other anonymous user data if needed:
                // username = "anonymous"
                // id = UUID.randomUUID().toString()
            })
            
            Log.d("MitzModeApplication", "Sentry initialized successfully")
        } catch (e: Exception) {
            // If Sentry fails to initialize, just continue without it
            Log.e("MitzModeApplication", "Failed to initialize Sentry: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (e: Exception) {
            Log.e("MitzModeApplication", "Error checking network availability: ${e.message}")
            false
        }
    }
} 