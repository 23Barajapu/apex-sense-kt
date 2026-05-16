package com.apexsense.pro.service

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.apexsense.pro.presentation.theme.ApexSenseTheme
import com.apexsense.pro.utils.HardwareMonitorUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.apexsense.pro.presentation.theme.AccentOrange
import androidx.compose.ui.layout.onGloballyPositioned

class OverlayService : LifecycleService(), ViewModelStoreOwner, SavedStateRegistryOwner {

    companion object {
        private val _isCrosshairActive = MutableStateFlow(false)
        val isCrosshairActive: StateFlow<Boolean> = _isCrosshairActive

        private val _isMonitorActive = MutableStateFlow(false)
        val isMonitorActive: StateFlow<Boolean> = _isMonitorActive
    }

    private lateinit var windowManager: WindowManager
    private var crosshairView: View? = null
    private var monitorView: View? = null

    private val _viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val viewModelStore: ViewModelStore
        get() = _viewModelStore
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val action = intent?.action
        when (action) {
            "SHOW_CROSSHAIR" -> { showCrosshair(); _isCrosshairActive.value = true }
            "HIDE_CROSSHAIR" -> { hideCrosshair(); _isCrosshairActive.value = false }
            "SHOW_MONITOR" -> { showMonitor(); _isMonitorActive.value = true }
            "HIDE_MONITOR" -> { hideMonitor(); _isMonitorActive.value = false }
        }
        return START_STICKY
    }

    private fun showCrosshair() {
        if (crosshairView != null) return

        val params =
                WindowManager.LayoutParams(
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                                else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                                PixelFormat.TRANSLUCENT
                        )
                        .apply { gravity = Gravity.CENTER }

        crosshairView =
                ComposeView(this).apply {
                    setViewTreeLifecycleOwner(this@OverlayService)
                    setViewTreeViewModelStoreOwner(this@OverlayService)
                    setViewTreeSavedStateRegistryOwner(this@OverlayService)

                    setContent {
                        ApexSenseTheme {
                            val config by CrosshairState.config.collectAsState()
                            
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .graphicsLayer {
                                        scaleX = config.size
                                        scaleY = config.size
                                        rotationZ = config.rotation
                                        alpha = config.alpha
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                    val drawColor = config.color
                                    val strokeWidth = config.thickness.dp.toPx()
                                    val totalSize = size.width
                                    val center = totalSize / 2
                                    val halfLen = (totalSize / 2) * config.length
                                    
                                    when (config.style) {
                                        "cross" -> {
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center - halfLen), androidx.compose.ui.geometry.Offset(center, center + halfLen), strokeWidth)
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center), androidx.compose.ui.geometry.Offset(center + halfLen, center), strokeWidth)
                                        }
                                        "dot" -> {
                                            drawCircle(drawColor, radius = (totalSize / 8) * config.length)
                                        }
                                        "circle" -> {
                                            drawCircle(drawColor, radius = halfLen, style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth))
                                        }
                                        "gap_cross" -> {
                                            val gap = (totalSize / 4)
                                            val lineLen = halfLen
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center - gap - lineLen), androidx.compose.ui.geometry.Offset(center, center - gap), strokeWidth)
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center + gap), androidx.compose.ui.geometry.Offset(center, center + gap + lineLen), strokeWidth)
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - gap - lineLen, center), androidx.compose.ui.geometry.Offset(center - gap, center), strokeWidth)
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center + gap, center), androidx.compose.ui.geometry.Offset(center + gap + lineLen, center), strokeWidth)
                                        }
                                        "square" -> {
                                            drawRect(
                                                drawColor, 
                                                topLeft = androidx.compose.ui.geometry.Offset(center - halfLen, center - halfLen),
                                                size = androidx.compose.ui.geometry.Size(halfLen * 2, halfLen * 2),
                                                style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
                                            )
                                        }
                                        "t_shape" -> {
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center), androidx.compose.ui.geometry.Offset(center, center + halfLen), strokeWidth)
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center), androidx.compose.ui.geometry.Offset(center + halfLen, center), strokeWidth)
                                        }
                                        "chevron" -> {
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center + halfLen), androidx.compose.ui.geometry.Offset(center, center), strokeWidth)
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center + halfLen, center + halfLen), androidx.compose.ui.geometry.Offset(center, center), strokeWidth)
                                        }
                                        "x_shape" -> {
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center - halfLen), androidx.compose.ui.geometry.Offset(center + halfLen, center + halfLen), strokeWidth)
                                            drawLine(drawColor, androidx.compose.ui.geometry.Offset(center + halfLen, center - halfLen), androidx.compose.ui.geometry.Offset(center - halfLen, center + halfLen), strokeWidth)
                                        }
                                    }
                                }
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

        val params =
                WindowManager.LayoutParams(
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                                else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                                PixelFormat.TRANSLUCENT
                        )
                        .apply {
                            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                            y = 60 // Closer to top like a status bar
                        }

        monitorView =
                ComposeView(this).apply {
                    setViewTreeLifecycleOwner(this@OverlayService)
                    setViewTreeViewModelStoreOwner(this@OverlayService)
                    setViewTreeSavedStateRegistryOwner(this@OverlayService)

                    setContent {
                        ApexSenseTheme {
                            val config by AppMonitorState.config.collectAsState()
                            var cpuUsage by remember { mutableIntStateOf(0) }
                            var ramUsage by remember { mutableIntStateOf(0) }
                            var batteryLevel by remember { mutableIntStateOf(0) }
                            var temperature by remember { mutableDoubleStateOf(0.0) }
                            var fpsRate by remember { mutableIntStateOf(60) }


                            LaunchedEffect(Unit) {
                                while (true) {
                                    cpuUsage = HardwareMonitorUtils.getCpuUsage()
                                    ramUsage = HardwareMonitorUtils.getRamUsage(this@apply.context)
                                    batteryLevel =
                                            HardwareMonitorUtils.getBatteryLevel(this@apply.context)
                                    temperature =
                                            HardwareMonitorUtils.getBatteryTemperature(
                                                    this@apply.context
                                             )
                                    fpsRate = HardwareMonitorUtils.getRefreshRate(this@apply.context)
                                    delay(1000)
                                }
                            }

                            LaunchedEffect(config) {
                                // Reset to wrap content first to allow shrinking
                                params.width = WindowManager.LayoutParams.WRAP_CONTENT
                                params.height = WindowManager.LayoutParams.WRAP_CONTENT
                                try { windowManager.updateViewLayout(this@apply, params) } catch (_: Exception) {}
                            }

                            // Compact Stats Pill - count active items for divider logic
                            val activeFlags = listOf(config.showCpu, config.showGpu, config.showRam, config.showTemp, config.showFps, config.showBattery)
                            val totalActive = activeFlags.count { it }

                            Row(
                                modifier = Modifier
                                    .onGloballyPositioned { layoutCoordinates ->
                                        val newWidth = layoutCoordinates.size.width
                                        val newHeight = layoutCoordinates.size.height
                                        if (params.width != newWidth || params.height != newHeight) {
                                            params.width = newWidth
                                            params.height = newHeight
                                            try { windowManager.updateViewLayout(this@apply, params) } catch (_: Exception) {}
                                        }
                                    }
                                    .background(Color(0xFF0D0D0D).copy(alpha = 0.75f), RoundedCornerShape(14.dp))
                                    .border(0.5f.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                var shown = 0
                                if (config.showCpu) { MonitorStatItem(label = "CPU", value = "$cpuUsage%"); shown++; if (shown < totalActive) StatDivider() }
                                if (config.showGpu) { MonitorStatItem(label = "GPU", value = "27%"); shown++; if (shown < totalActive) StatDivider() }
                                if (config.showRam) { MonitorStatItem(label = "RAM", value = "$ramUsage%"); shown++; if (shown < totalActive) StatDivider() }
                                if (config.showTemp) { MonitorStatItem(label = "TMP", value = "${temperature.toInt()}°"); shown++; if (shown < totalActive) StatDivider() }
                                if (config.showFps) { MonitorStatItem(label = "FPS", value = "$fpsRate"); shown++; if (shown < totalActive) StatDivider() }
                                if (config.showBattery) { MonitorStatItem(label = "BAT", value = "$batteryLevel%", isBattery = true); shown++; if (shown < totalActive) StatDivider() }

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

    // Unified Stat Item for both overlays
    @Composable
    private fun MonitorStatItem(
        label: String? = null,
        icon: (@Composable () -> Unit)? = null,
        value: String,
        isBattery: Boolean = false
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            if (icon != null) {
                Box(modifier = Modifier.graphicsLayer(alpha = 0.8f)) {
                    icon()
                }
            } else if (!label.isNullOrEmpty()) {
                Text(
                    text = label,
                    color = AccentOrange,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
            }

            if (isBattery) {
                val cleanValue = value.replace("%", "").trim()
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = cleanValue,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "%",
                        color = AccentOrange,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 1.dp, start = 1.dp)
                    )
                }
            } else {
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }

    @Composable
    private fun StatDivider() {
        Box(
            modifier = Modifier
                .width(0.5f.dp)
                .height(10.dp)
                .background(Color.White.copy(alpha = 0.12f))
        )
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