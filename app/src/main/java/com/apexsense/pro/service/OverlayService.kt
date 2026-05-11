package com.apexsense.pro.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.apexsense.pro.presentation.theme.ApexSenseTheme
import com.apexsense.pro.utils.HardwareMonitorUtils
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onSizeChanged

class OverlayService : LifecycleService(), ViewModelStoreOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var crosshairView: View? = null
    private var monitorView: View? = null

    private val _viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val viewModelStore: ViewModelStore get() = _viewModelStore
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val action = intent?.action
        when (action) {
            "SHOW_CROSSHAIR" -> showCrosshair()
            "HIDE_CROSSHAIR" -> hideCrosshair()
            "SHOW_MONITOR" -> showMonitor()
            "HIDE_MONITOR" -> hideMonitor()
        }
        return START_STICKY
    }

    private fun showCrosshair() {
        if (crosshairView != null) return

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        crosshairView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeViewModelStoreOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)
            
            setContent {
                ApexSenseTheme {
                    val config by CrosshairState.config.collectAsState()
                    Log.d("OverlayService", "Crosshair Recomposing: rotation=${config.rotation}")
                    
                    // Only update window layout if size changed (since we use WRAP_CONTENT, 
                    // the WindowManager needs to know the view might have new bounds)
                    LaunchedEffect(config.size) {
                        windowManager.updateViewLayout(this@apply, params)
                    }

                    Box(
                        modifier = Modifier
                            .size((60 * config.size).dp), // Slightly larger box to allow for rotation without clipping
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = config.style,
                            contentDescription = null,
                            tint = config.color.copy(alpha = config.alpha),
                            modifier = Modifier
                                .size((30 * config.size).dp)
                                .graphicsLayer { rotationZ = config.rotation }
                        )
                    }
                }
            }
            
            // Add drag functionality
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f

            setOnTouchListener { _, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        true
                    }
                    android.view.MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(crosshairView, params)
                        true
                    }
                    else -> false
                }
            }
        }

        // Add to window
        try {
            windowManager.addView(crosshairView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideCrosshair() {
        crosshairView?.let {
            windowManager.removeView(it)
            crosshairView = null
        }
    }

    private fun showMonitor() {
        if (monitorView != null) return

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 100
        }

        monitorView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeViewModelStoreOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)
            
            setContent {
                ApexSenseTheme {
                    val config by MonitorState.config.collectAsState()
                    var cpuUsage by remember { mutableIntStateOf(0) }
                    var ramUsage by remember { mutableIntStateOf(0) }
                    var batteryLevel by remember { mutableIntStateOf(0) }
                    var temperature by remember { mutableDoubleStateOf(0.0) }
                    var currentTime by remember { mutableStateOf("") }
                    
                    LaunchedEffect(Unit) {
                        while(true) {
                            cpuUsage = HardwareMonitorUtils.getCpuUsage()
                            ramUsage = HardwareMonitorUtils.getRamUsage(this@apply.context)
                            batteryLevel = HardwareMonitorUtils.getBatteryLevel(this@apply.context)
                            temperature = HardwareMonitorUtils.getBatteryTemperature(this@apply.context)
                            currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                            delay(1000)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .padding(4.dp)
                            .onSizeChanged { size ->
                                // Explicitly update the WindowManager params with exact pixel sizes 
                                // to overcome Android's WRAP_CONTENT updateViewLayout bugs.
                                if (params.width != size.width || params.height != size.height) {
                                    params.width = size.width
                                    params.height = size.height
                                    windowManager.updateViewLayout(this@apply, params)
                                }
                            }
                    ) {
                        // Drag Handle - High Fidelity
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF5C1616), RoundedCornerShape(10.dp))
                                .border(1.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenWith,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Stats Pill - High Fidelity
                        Row(
                            modifier = Modifier
                                .height(34.dp)
                                .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(100.dp))
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            if (config.showCpu) MonitorStatItem(Icons.Default.Memory, "CPU", "$cpuUsage %")
                            if (config.showGpu) MonitorStatItem(Icons.Default.DeveloperBoard, "GPU", "27 %")
                            if (config.showRam) MonitorStatItem(Icons.Default.Storage, "RAM", "$ramUsage %")
                            if (config.showBattery) MonitorStatItem(Icons.Default.BatteryFull, "", "$batteryLevel %")
                            if (config.showTemp) MonitorStatItem(Icons.Default.DeviceThermostat, "", "${temperature.toInt()} °C")
                            if (config.showFps) MonitorStatItem(Icons.Default.Layers, "FPS", "119")
                            if (config.showTime) MonitorStatItem(Icons.Default.Schedule, "", currentTime)
                        }
                    }
                }
            }
            
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f

            setOnTouchListener { _, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        true
                    }
                    android.view.MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(monitorView, params)
                        true
                    }
                    else -> false
                }
            }
        }

        try {
            windowManager.addView(monitorView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Composable
    private fun MonitorStatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = null, tint = Color(0xFFB0B0B0), modifier = Modifier.size(16.dp))
            if (label.isNotEmpty()) {
                Text(label, color = Color(0xFFB0B0B0), fontSize = 10.sp, fontWeight = FontWeight.Black, maxLines = 1, softWrap = false)
            }
            Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
        }
    }

    private fun hideMonitor() {
        monitorView?.let {
            windowManager.removeView(it)
            monitorView = null
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        hideCrosshair()
        hideMonitor()
    }
}
