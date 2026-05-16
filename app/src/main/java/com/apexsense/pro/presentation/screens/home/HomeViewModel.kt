package com.apexsense.pro.presentation.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
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
    val ramUsed: Double = 0.0,
    val ramTotal: Double = 0.0,
    val storageUsed: Double = 0.0,
    val storageTotal: Double = 0.0,
    val gameCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel : ViewModel() {
    private val repository = AppRepository()
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    fun startMonitoring(context: Context) {
        viewModelScope.launch {
            val localCount = getLocalGameCount(context)
            val ramInfo = HardwareMonitorUtils.getRamInfo(context)
            val storageInfo = HardwareMonitorUtils.getStorageInfo()
            
            _state.value = _state.value.copy(
                deviceModel = HardwareMonitorUtils.getDeviceModel(),
                gameCount = localCount,
                ramUsed = ramInfo.first,
                ramTotal = ramInfo.second,
                storageUsed = storageInfo.first,
                storageTotal = storageInfo.second
            )
            while (true) {
                val cpu = HardwareMonitorUtils.getCpuUsage()
                val temp = HardwareMonitorUtils.getBatteryTemperature(context)
                val currentRam = HardwareMonitorUtils.getRamInfo(context)
                
                _state.value = _state.value.copy(
                    cpuUsage = cpu,
                    temperature = temp,
                    ramUsed = currentRam.first
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

    private fun getLocalGameCount(context: Context): Int {
        val packageManager = context.packageManager
        val prefs = context.getSharedPreferences("game_vault_prefs", Context.MODE_PRIVATE)
        val manualSet = prefs.getStringSet("manual_games", emptySet()) ?: emptySet()
        val hiddenSet = prefs.getStringSet("hidden_games", emptySet()) ?: emptySet()
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        
        var count = 0
        for (app in packages) {
            if (hiddenSet.contains(app.packageName)) continue

            val isManual = manualSet.contains(app.packageName)
            val isGame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                app.category == ApplicationInfo.CATEGORY_GAME
            } else {
                @Suppress("DEPRECATION")
                (app.flags and ApplicationInfo.FLAG_IS_GAME) != 0
            }
            
            val packageLower = app.packageName.lowercase()
            val isCommonGame = packageLower.contains("game") || 
                              packageLower.contains("tencent") || 
                              packageLower.contains("garena") ||
                              packageLower.contains("mobile")

            if (isGame || isCommonGame || isManual) {
                if ((app.flags and ApplicationInfo.FLAG_SYSTEM) != 0 && !isGame && !isManual) continue
                count++
            }
        }
        return count
    }
}
