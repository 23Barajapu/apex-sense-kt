package com.apexsense.presentation.screens.library

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apexsense.R
import com.apexsense.data.repository.AppRepository
import com.apexsense.domain.model.Game
import com.apexsense.utils.GameBoostHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LibraryState(
    val games: List<Game> = emptyList(),
    val allApps: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val showAddSheet: Boolean = false,
    val isBoosting: Boolean = false,
    val boostingProgress: Float = 0f,
    val boostingText: String = ""
)

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository()
    private val packageManager = application.packageManager
    private val prefs = application.getSharedPreferences("game_vault_prefs", android.content.Context.MODE_PRIVATE)
    private val appContext = application.applicationContext

    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state

    private var cachedInstalledApps: List<Game>? = null

    private val boostSteps = listOf(
        GameBoostHelper.BoostStep(R.string.boost_step_clearing_ram, 0.2f),
        GameBoostHelper.BoostStep(R.string.boost_step_optimizing_gpu, 0.4f),
        GameBoostHelper.BoostStep(R.string.boost_step_stabilizing_network, 0.6f),
        GameBoostHelper.BoostStep(R.string.boost_step_applying_game_mode, 0.8f),
        GameBoostHelper.BoostStep(R.string.boost_step_success, 1.0f)
    )

    init {
        loadGames()
    }

    fun toggleAddSheet(show: Boolean) {
        _state.value = _state.value.copy(showAddSheet = show)
        if (show) {
            loadAllApps()
        }
    }

    fun addManualGame(packageName: String) {
        val manualSet = prefs.getStringSet("manual_games", emptySet())?.toMutableSet() ?: mutableSetOf()
        val hiddenSet = prefs.getStringSet("hidden_games", emptySet())?.toMutableSet() ?: mutableSetOf()

        if (hiddenSet.remove(packageName)) {
            prefs.edit().putStringSet("hidden_games", hiddenSet).apply()
        }

        manualSet.add(packageName)
        prefs.edit().putStringSet("manual_games", manualSet).apply()
        loadGames()
    }

    fun removeManualGame(packageName: String) {
        val manualSet = prefs.getStringSet("manual_games", emptySet())?.toMutableSet() ?: mutableSetOf()
        manualSet.remove(packageName)
        prefs.edit().putStringSet("manual_games", manualSet).apply()

        val hiddenSet = prefs.getStringSet("hidden_games", emptySet())?.toMutableSet() ?: mutableSetOf()
        hiddenSet.add(packageName)
        prefs.edit().putStringSet("hidden_games", hiddenSet).apply()

        loadGames()
    }

    private fun loadAllApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val all = cachedInstalledApps ?: getInstalledApps().also { cachedInstalledApps = it }
            val currentGames = _state.value.games.map { it.package_name }.toSet()
            val filtered = all.filter { it.package_name !in currentGames }
            _state.value = _state.value.copy(allApps = filtered)
        }
    }

    fun launchGameWithBoost(packageName: String) {
        if (_state.value.isBoosting) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isBoosting = true, boostingProgress = 0f, boostingText = "")

            GameBoostHelper.runBoost(
                context = appContext,
                steps = boostSteps,
                onProgress = { textResId, progress ->
                    _state.value = _state.value.copy(
                        boostingText = appContext.getString(textResId),
                        boostingProgress = progress
                    )
                }
            )

            _state.value = _state.value.copy(isBoosting = false)

            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                appContext.startActivity(intent)
            } else {
                Toast.makeText(
                    appContext,
                    appContext.getString(R.string.failed_label, packageName),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun loadGames() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val installedGames = withContext(Dispatchers.IO) {
                getInstalledGames()
            }

            _state.value = _state.value.copy(
                games = installedGames,
                isLoading = false
            )
        }
    }

    private fun getInstalledApps(): List<Game> {
        val apps = mutableListOf<Game>()
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in packages) {
            val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null) {
                apps.add(
                    Game(
                        id = app.uid,
                        name = packageManager.getApplicationLabel(app).toString(),
                        icon_url = app.packageName,
                        package_name = app.packageName
                    )
                )
            }
        }
        return apps.sortedBy { it.name }
    }

    @Suppress("DEPRECATION")
    private fun getInstalledGames(): List<Game> {
        val games = mutableListOf<Game>()
        val manualSet = prefs.getStringSet("manual_games", emptySet()) ?: emptySet()
        val hiddenSet = prefs.getStringSet("hidden_games", emptySet()) ?: emptySet()
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        for (app in packages) {
            if (hiddenSet.contains(app.packageName)) continue

            val isManual = manualSet.contains(app.packageName)
            val isGame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                app.category == ApplicationInfo.CATEGORY_GAME
            } else {
                (app.flags and ApplicationInfo.FLAG_IS_GAME) != 0
            }

            val packageLower = app.packageName.lowercase()
            val isCommonGame = packageLower.contains("game") ||
                packageLower.contains("tencent") ||
                packageLower.contains("garena") ||
                packageLower.contains("mobile")

            if (isGame || isCommonGame || isManual) {
                if ((app.flags and ApplicationInfo.FLAG_SYSTEM) != 0 && !isGame && !isManual) continue

                games.add(
                    Game(
                        id = app.uid,
                        name = packageManager.getApplicationLabel(app).toString(),
                        icon_url = app.packageName,
                        package_name = app.packageName
                    )
                )
            }
        }
        return games.sortedBy { it.name }
    }
}
