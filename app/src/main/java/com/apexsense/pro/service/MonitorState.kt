package com.apexsense.pro.service

import kotlinx.coroutines.flow.MutableStateFlow

data class MonitorConfig(
    val showCpu: Boolean = true,
    val showGpu: Boolean = true,
    val showRam: Boolean = true,
    val showBattery: Boolean = true,
    val showTemp: Boolean = true,
    val showFps: Boolean = true,
    val showTime: Boolean = true
)

object MonitorState {
    val config = MutableStateFlow(MonitorConfig())
    
    fun update(update: (MonitorConfig) -> MonitorConfig) {
        config.value = update(config.value)
    }
}