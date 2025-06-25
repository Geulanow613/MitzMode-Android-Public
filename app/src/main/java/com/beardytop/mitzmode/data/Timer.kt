package com.beardytop.mitzmode.data

enum class FoodType {
    MEAT, DAIRY
}

data class TimerInfo(
    val foodType: FoodType,
    val startTime: Long,
    val duration: Long  // in milliseconds
)

sealed class TimerState {
    object Idle : TimerState()
    data class Running(
        val foodType: FoodType,
        val endTime: Long,
        val remainingMillis: Long
    ) : TimerState()
} 