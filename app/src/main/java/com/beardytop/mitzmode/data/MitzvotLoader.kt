package com.beardytop.mitzmode.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.net.URL
import java.io.IOException

class MitzvotLoader(private val context: Context) {
    data class MitzvotList(val mitzvot: List<Mitzvah>)
    
    private val githubUrl = "https://raw.githubusercontent.com/Geulanow613/mitzmode/main/mitzvotcloud.json"

    suspend fun loadMitzvot(): List<Mitzvah> = withContext(Dispatchers.IO) {
        Log.d("MitzvotLoader", "Starting to load mitzvot")
        val localMitzvot = loadLocalMitzvot()
        Log.d("MitzvotLoader", "Loaded ${localMitzvot.size} local mitzvot with IDs: ${localMitzvot.map { it.id }}")
        
        val cloudMitzvot = if (isNetworkAvailable()) {
            Log.d("MitzvotLoader", "Network is available, attempting to load cloud mitzvot from $githubUrl")
            try {
                // Use withTimeoutOrNull to prevent hanging and reduce timeout to 3 seconds
                val cloud = withTimeoutOrNull(3000) {
                    loadCloudMitzvot()
                }
                if (cloud != null) {
                    Log.d("MitzvotLoader", "Successfully loaded ${cloud.size} cloud mitzvot with IDs: ${cloud.map { it.id }}")
                    cloud
                } else {
                    Log.w("MitzvotLoader", "Cloud mitzvot loading timed out after 3 seconds")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("MitzvotLoader", "Failed to load cloud mitzvot: ${e.message}", e)
                Log.e("MitzvotLoader", "Stack trace: ${e.stackTraceToString()}")
                emptyList()
            }
        } else {
            Log.d("MitzvotLoader", "No network available, skipping cloud mitzvot")
            emptyList()
        }
        
        // Combine both lists, ensuring no duplicate IDs
        val allMitzvot = (localMitzvot + cloudMitzvot).distinctBy { it.id }
        Log.d("MitzvotLoader", "Final combined list has ${allMitzvot.size} mitzvot")
        Log.d("MitzvotLoader", "Final list IDs: ${allMitzvot.map { it.id }}")
        return@withContext allMitzvot
    }

    private fun loadLocalMitzvot(): List<Mitzvah> {
        return try {
            val jsonString = context.assets.open("mitzvotlistfull.json").bufferedReader().use { it.readText() }
            Log.d("MitzvotLoader", "Local JSON content loaded successfully")
            Gson().fromJson<MitzvotList>(jsonString, object : TypeToken<MitzvotList>() {}.type).mitzvot
        } catch (e: Exception) {
            Log.e("MitzvotLoader", "Failed to load local mitzvot: ${e.message}", e)
            // Return empty list if local mitzvot can't be loaded (should never happen)
            emptyList()
        }
    }

    private fun loadCloudMitzvot(): List<Mitzvah> {
        val connection = URL(githubUrl).openConnection() as java.net.HttpURLConnection
        connection.connectTimeout = 2000  // Reduced from 5000 to 2000ms
        connection.readTimeout = 2000     // Reduced from 5000 to 2000ms
        connection.setRequestProperty("User-Agent", "MitzMode-Android-App")
        
        return try {
            val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d("MitzvotLoader", "Cloud JSON content loaded successfully")
            val cloudList = Gson().fromJson<MitzvotList>(jsonString, object : TypeToken<MitzvotList>() {}.type).mitzvot
            Log.d("MitzvotLoader", "Parsed ${cloudList.size} cloud mitzvot")
            cloudList
        } catch (e: Exception) {
            Log.e("MitzvotLoader", "Exception in loadCloudMitzvot: ${e.message}")
            throw e
        } finally {
            connection.disconnect()
        }
    }
    
    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                             capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            Log.d("MitzvotLoader", "Network available: $hasInternet")
            hasInternet
        } catch (e: Exception) {
            Log.e("MitzvotLoader", "Error checking network availability: ${e.message}")
            false
        }
    }
} 