package com.gokanaz.kanazplayer.service

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SleepTimerManager {
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive
    
    private val _remainingTime = MutableStateFlow(0L)
    val remainingTime: StateFlow<Long> = _remainingTime
    
    fun startTimer(context: Context, minutes: Int) {
        cancelTimer()
        
        val totalMillis = minutes * 60 * 1000L
        _remainingTime.value = totalMillis
        _isActive.value = true
        
        timerJob = scope.launch {
            while (_remainingTime.value > 0) {
                delay(1000)
                _remainingTime.value -= 1000
            }
            
            MusicPlayerManager.getPlayer(context).pause()
            _isActive.value = false
        }
    }
    
    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _isActive.value = false
        _remainingTime.value = 0
    }
}
