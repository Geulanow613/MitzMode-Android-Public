package com.beardytop.mitzmode.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class MitzvotRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mitzvot: List<Mitzvah>? = null
    private val mutex = Mutex()
    
    suspend fun getRandomMitzvah(): Mitzvah = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (mitzvot == null) {
                Log.d("MitzvotRepository", "Loading mitzvot for the first time")
                mitzvot = try {
                    val loadedMitzvot = MitzvotLoader(context).loadMitzvot()
                    if (loadedMitzvot.isEmpty()) {
                        Log.e("MitzvotRepository", "No mitzvot loaded! This should not happen.")
                        // Return a fallback mitzvah if somehow no mitzvot are loaded
                        listOf(
                            Mitzvah(
                                id = "fallback",
                                text = "Say a prayer for the success of this app and for peace in the world!",
                                links = emptyList()
                            )
                        )
                    } else {
                        Log.d("MitzvotRepository", "Successfully loaded ${loadedMitzvot.size} mitzvot")
                        loadedMitzvot
                    }
                } catch (e: Exception) {
                    Log.e("MitzvotRepository", "Error loading mitzvot: ${e.message}", e)
                    // Return fallback mitzvah in case of any error
                    listOf(
                        Mitzvah(
                            id = "error_fallback",
                            text = "Thank G-d for three things in your life right now. This app experienced a technical issue, but gratitude always works!",
                            links = emptyList()
                        )
                    )
                }
            }
            return@withContext mitzvot!!.random()
        }
    }
    
    suspend fun refreshMitzvot() = withContext(Dispatchers.IO) {
        mutex.withLock {
            Log.d("MitzvotRepository", "Refreshing mitzvot list")
            try {
                mitzvot = MitzvotLoader(context).loadMitzvot()
                Log.d("MitzvotRepository", "Successfully refreshed ${mitzvot?.size ?: 0} mitzvot")
            } catch (e: Exception) {
                Log.e("MitzvotRepository", "Error refreshing mitzvot: ${e.message}", e)
                // Don't clear existing mitzvot if refresh fails
            }
        }
    }
    
    // Add a method to preload mitzvot without blocking
    suspend fun preloadMitzvot() = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (mitzvot == null) {
                Log.d("MitzvotRepository", "Preloading mitzvot in background")
                try {
                    mitzvot = MitzvotLoader(context).loadMitzvot()
                    Log.d("MitzvotRepository", "Successfully preloaded ${mitzvot?.size ?: 0} mitzvot")
                } catch (e: Exception) {
                    Log.e("MitzvotRepository", "Error preloading mitzvot: ${e.message}", e)
                    // Keep mitzvot as null, will be loaded on first getRandomMitzvah call
                }
            }
        }
    }
    
    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (e: Exception) {
            Log.e("MitzvotRepository", "Error checking network availability: ${e.message}")
            false
        }
    }
} 