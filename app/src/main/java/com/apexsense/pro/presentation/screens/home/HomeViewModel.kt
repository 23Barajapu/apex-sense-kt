package com.apexsense.pro.presentation.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexsense.pro.data.repository.AppRepository
import com.apexsense.pro.domain.model.HardwareHistory
import com.apexsense.pro.utils.HardwareMonitorUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val deviceModel: String = "",
    val cpuUsage: Int = 0,
    val temperature: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel : ViewModel() {
    private val repository = AppRepository()
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    fun startMonitoring(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(deviceModel = HardwareMonitorUtils.getDeviceModel())
            while (true) {
                val cpu = HardwareMonitorUtils.getCpuUsage()
                val temp = HardwareMonitorUtils.getBatteryTemperature(context)
                
                _state.value = _state.value.copy(
                    cpuUsage = cpu,
                    temperature = temp
                )

                // Save to history every 60 seconds (simulated)
                repository.saveHardwareHistory(
                    HardwareHistory(
                        device_model = _state.value.deviceModel,
                        cpu_usage = cpu,
                        temp = temp
                    )
                )

                delay(5000)
            }
        }
    }
}
