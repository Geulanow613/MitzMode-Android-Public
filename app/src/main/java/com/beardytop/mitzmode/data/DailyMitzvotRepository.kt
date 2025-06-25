package com.beardytop.mitzmode.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyMitzvotRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("daily_mitzvot_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveChecklistStates(states: Map<String, Boolean>) {
        val date = LocalDate.now().toString()
        prefs.edit()
            .putString("checklist_$date", gson.toJson(states))
            .apply()
    }

    fun getChecklistStates(): Map<String, Boolean> {
        val today = LocalDate.now().toString()
        val lastSavedDate = prefs.getString("last_saved_date", "") ?: ""
        
        // If it's a new day, clear the states
        if (lastSavedDate != today) {
            prefs.edit()
                .remove("checklist_$lastSavedDate")
                .putString("last_saved_date", today)
                .remove("tzaddik_shown_$lastSavedDate")
                .apply()
            return emptyMap()
        }
        
        val json = prefs.getString("checklist_$today", null) ?: return emptyMap()
        return try {
            gson.fromJson(json, object : TypeToken<Map<String, Boolean>>() {}.type)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun hasShownTzaddikToday(): Boolean {
        val date = LocalDate.now().toString()
        return prefs.getBoolean("tzaddik_shown_$date", false)
    }

    fun markTzaddikShown() {
        val date = LocalDate.now().toString()
        prefs.edit()
            .putBoolean("tzaddik_shown_$date", true)
            .apply()
    }

    fun hasShownBlessedToday(): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return prefs.getString("last_blessed_date", "") == today
    }

    fun markBlessedShown() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        prefs.edit().putString("last_blessed_date", today).apply()
    }
} 