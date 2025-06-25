package com.beardytop.mitzmode.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beardytop.mitzmode.data.Mitzvah
import com.beardytop.mitzmode.data.MitzvotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.google.gson.Gson
import com.beardytop.mitzmode.util.SentryUtil

@HiltViewModel
class MitzModeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mitzvotRepository: MitzvotRepository
) : ViewModel() {
    private val prefs = context.getSharedPreferences("mitzvot_prefs", Context.MODE_PRIVATE)
    
    private val _currentMitzvah = MutableStateFlow<Mitzvah?>(null)
    val currentMitzvah: StateFlow<Mitzvah?> = _currentMitzvah.asStateFlow()
    
    private val _completedMitzvot = MutableStateFlow<List<Mitzvah>>(emptyList())
    val completedMitzvot: StateFlow<List<Mitzvah>> = _completedMitzvot.asStateFlow()
    
    private val _showVideo = MutableStateFlow<Int?>(null)
    val showVideo: StateFlow<Int?> = _showVideo.asStateFlow()
    
    private val _isIdle = MutableStateFlow(false)
    val isIdle: StateFlow<Boolean> = _isIdle.asStateFlow()
    
    private var idleJob: Job? = null
    
    private val _acceptedMitzvotCount = MutableStateFlow(0)
    val acceptedMitzvotCount: StateFlow<Int> = _acceptedMitzvotCount.asStateFlow()
    
    private var _showLevelUp = MutableStateFlow<String?>(null)
    val showLevelUp: StateFlow<String?> = _showLevelUp.asStateFlow()
    
    init {
        viewModelScope.launch {
            loadSavedMitzvot()
            startIdleTimer()
            // Preload mitzvot in background so first button press is fast
            preloadMitzvotInBackground()
        }
    }
    
    private fun loadSavedMitzvot() {
        val savedMitzvot = prefs.getStringSet("completed_mitzvot", emptySet()) ?: emptySet()
        _completedMitzvot.value = savedMitzvot.mapNotNull { mitzvahJson ->
            try {
                Gson().fromJson(mitzvahJson, Mitzvah::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private suspend fun preloadMitzvotInBackground() {
        try {
            // This loads mitzvot without blocking the UI
            mitzvotRepository.preloadMitzvot()
        } catch (e: Exception) {
            // Log error but don't crash - mitzvot will be loaded on first button press if needed
            handleError(e, "preloadMitzvotInBackground")
        }
    }
    
    fun onMitzvahButtonPressed() {
        viewModelScope.launch {
            _currentMitzvah.value = mitzvotRepository.getRandomMitzvah()
            resetIdleTimer()
        }
    }
    
    private fun startIdleTimer() {
        idleJob?.cancel()
        idleJob = viewModelScope.launch {
            delay(30000) // 30 seconds
            _isIdle.value = true
        }
    }
    
    private fun resetIdleTimer() {
        _isIdle.value = false
        startIdleTimer()
    }
    
    fun onVideoComplete() {
        _showVideo.value = null
    }
    
    fun onLevelUpComplete() {
        _showLevelUp.value = null
    }
    
    private fun handleError(e: Exception, context: String) {
        SentryUtil.logError(e, mapOf(
            "context" to context,
            "screen" to "main",
            "user_action" to "mitzvah_interaction"
        ))
    }
    
    private fun getCurrentLevel(count: Int): String {
        println("DEBUG: Calculating level for count: $count")
        val level = when (count) {
            in 1..9 -> "Beginner"
            in 10..49 -> "Ba'al Teshuva"
            in 50..99 -> "Master Cholent Chef"
            in 100..199 -> "Aspiring Kiddush Maker"
            in 200..299 -> "Assistant Gabbai"
            in 300..399 -> "Guy who hands out candy at shul"
            in 400..499 -> "Western Wall Reveler"
            in 500..599 -> "Sofer"
            in 600..699 -> "Tzaddik"
            in 700..799 -> "Living Sefer Torah"
            in 800..899 -> "Eliyahu HaNavi"
            in 900..999 -> "King David"
            in 1000..Int.MAX_VALUE -> "Moshiach!!!"
            else -> "Beginner"  // Default case, should never happen
        }
        println("DEBUG: Level calculated as: $level")
        return level
    }
    
    private fun getVideoNumberForLevel(count: Int): Int {
        return when (count) {
            1 -> 1  // Beginner video at first mitzvah
            10 -> 2  // Ba'al Teshuva video at 10th mitzvah
            50 -> 3  // Master Cholent Chef at 50th mitzvah
            100 -> 4  // Aspiring Kiddush Maker at 100th mitzvah
            200 -> 5  // Assistant Gabbai at 200th mitzvah
            300 -> 6  // Guy who hands out candy at shul at 300th mitzvah
            400 -> 7  // Western Wall Reveler at 400th mitzvah
            500 -> 8  // Sofer at 500th mitzvah
            600 -> 9  // Tzaddik at 600th mitzvah
            700 -> 10 // Living Sefer Torah at 700th mitzvah
            800 -> 11 // Eliyahu HaNavi at 800th mitzvah
            900 -> 12 // King David at 900th mitzvah
            1000 -> 13 // Moshiach!!! at 1000th mitzvah
            else -> 0  // No video for non-milestone numbers
        }
    }
    
    fun onMitzvahAccepted() {
        viewModelScope.launch {
            try {
                val mitzvah = currentMitzvah.value ?: return@launch
                
                // Get current count before adding new mitzvah
                val currentCount = _completedMitzvot.value.size
                println("DEBUG: Current mitzvot count: $currentCount")
                
                // Calculate new count
                val newCount = currentCount + 1
                println("DEBUG: New mitzvot count will be: $newCount")
                println("DEBUG: New level will be: ${getCurrentLevel(newCount)}")
                
                // Check for milestone achievements
                val shouldShowVideo = when (newCount) {
                    1, 10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000 -> {
                        println("DEBUG: Milestone reached at count $newCount!")
                        true
                    }
                    else -> false
                }
                
                // Update completed mitzvot list FIRST
                val updatedList = _completedMitzvot.value + mitzvah
                _completedMitzvot.value = updatedList
                
                // Save to preferences
                prefs.edit().putStringSet(
                    "completed_mitzvot",
                    updatedList.map { Gson().toJson(it) }.toSet()
                ).apply()
                
                // THEN check for video/level up AFTER updating the list
                if (shouldShowVideo) {
                    val newLevel = getCurrentLevel(newCount)
                    println("DEBUG: Playing video ${getVideoNumberForLevel(newCount)} for level $newLevel")
                    _showVideo.value = getVideoNumberForLevel(newCount)
                    _showLevelUp.value = newLevel
                }
                
                println("DEBUG: Mitzvah accepted! showVideo = ${_showVideo.value}, showLevelUp = ${_showLevelUp.value}")
            } catch (e: Exception) {
                handleError(e, "onMitzvahAccepted")
            }
        }
    }
    
    fun clearCurrentMitzvah() {
        _currentMitzvah.value = null
    }
} 