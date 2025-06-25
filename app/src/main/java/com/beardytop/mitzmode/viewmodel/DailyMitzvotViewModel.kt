package com.beardytop.mitzmode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beardytop.mitzmode.data.DailyMitzvotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*

@HiltViewModel
class DailyMitzvotViewModel @Inject constructor(
    private val repository: DailyMitzvotRepository
) : ViewModel() {
    private val _checklistStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val checklistStates: StateFlow<Map<String, Boolean>> = _checklistStates

    private val _showTzaddikAlert = MutableStateFlow(false)
    val showTzaddikAlert: StateFlow<Boolean> = _showTzaddikAlert

    private val _showBlessedAnimation = MutableStateFlow(false)
    val showBlessedAnimation: StateFlow<Boolean> = _showBlessedAnimation

    private var todayCheckCount = 0
    private var lastCheckDate = ""

    init {
        loadChecklistStates()
        lastCheckDate = getCurrentDate()
    }

    private fun loadChecklistStates() {
        _checklistStates.value = repository.getChecklistStates()
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun updateChecklistItem(key: String, value: Boolean) {
        val currentDate = getCurrentDate()
        
        // Reset count if it's a new day
        if (currentDate != lastCheckDate) {
            todayCheckCount = 0
            lastCheckDate = currentDate
        }

        val newStates = _checklistStates.value.toMutableMap()
        val oldValue = newStates[key] ?: false
        newStates[key] = value
        _checklistStates.value = newStates

        // Update count and show animation if needed
        if (value && !oldValue) {  // Only increment if changing from unchecked to checked
            todayCheckCount++
            println("Debug: Checkbox count = $todayCheckCount")  // Add this debug line
            if (todayCheckCount == 5 && !repository.hasShownBlessedToday()) {
                println("Debug: Showing blessed animation")  // Add this debug line
                _showBlessedAnimation.value = true
                repository.markBlessedShown()
            }
        }

        viewModelScope.launch {
            repository.saveChecklistStates(newStates)
        }
    }

    fun clearChecklist() {
        _checklistStates.value = emptyMap()
        viewModelScope.launch {
            repository.saveChecklistStates(emptyMap())
        }
    }

    fun hasShownTzaddikToday() = repository.hasShownTzaddikToday()

    fun showTzaddikAlert() {
        _showTzaddikAlert.value = true
        repository.markTzaddikShown()
    }

    fun dismissTzaddikAlert() {
        _showTzaddikAlert.value = false
    }

    fun dismissBlessedAnimation() {
        _showBlessedAnimation.value = false
    }
} 