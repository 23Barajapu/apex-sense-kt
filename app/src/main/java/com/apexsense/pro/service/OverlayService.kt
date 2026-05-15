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
import com.apexsense.pro.presentation.theme.AccentOrange
import androidx.compose.ui.layout.onGloballyPositioned

class OverlayService : LifecycleService(), ViewModelStoreOwner, SavedStateRegistryOwner {

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
            "SHOW_CROSSHAIR" -> showCrosshair()
            "HIDE_CROSSHAIR" -> hideCrosshair()
            "SHOW_MONITOR" -> showMonitor()
            "HIDE_MONITOR" -> hideMonitor()
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
                            Log.d(
                                    "OverlayService",
                                    "Crosshair Recomposing: rotation=${config.rotation}"
                            )

                            // Only update window layout if size changed (since we use WRAP_CONTENT,
                            // the WindowManager needs to know the view might have new bounds)
                            LaunchedEffect(config.size) {
                                windowManager.updateViewLayout(this@apply, params)
                            }

                            Box(
                                    modifier = Modifier.size((64 * config.size).dp),
                                    contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.foundation.Canvas(
                                        modifier =
                                                Modifier.size((32 * config.size).dp).graphicsLayer {
                                                    rotationZ = config.rotation
                                                }
                                ) {
                                    val color = config.color.copy(alpha = config.alpha)
                                    val strokeWidth = config.thickness.dp.toPx()
                                    val totalSize = size.width
                                    val center = totalSize / 2
                                    val halfLen = (totalSize / 2) * config.length
                                    
                                    when (config.style) {
                                        "cross" -> {
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center, center - halfLen), androidx.compose.ui.geometry.Offset(center, center + halfLen), strokeWidth)
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center - halfLen, center), androidx.compose.ui.geometry.Offset(center + halfLen, center), strokeWidth)
                                        }
                                        "dot" -> {
                                            drawCircle(color, radius = (totalSize / 6) * config.length)
                                        }
                                        "circle" -> {
                                            drawCircle(color, radius = halfLen, style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth))
                                        }
                                        "gap_cross" -> {
                                            val gap = (totalSize / 4) // Keep gap constant relative to total size
                                            val lineLen = halfLen
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center, center - gap - lineLen), androidx.compose.ui.geometry.Offset(center, center - gap), strokeWidth)
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center, center + gap), androidx.compose.ui.geometry.Offset(center, center + gap + lineLen), strokeWidth)
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center - gap - lineLen, center), androidx.compose.ui.geometry.Offset(center - gap, center), strokeWidth)
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center + gap, center), androidx.compose.ui.geometry.Offset(center + gap + lineLen, center), strokeWidth)
                                        }
                                        "square" -> {
                                            drawRect(
                                                color, 
                                                topLeft = androidx.compose.ui.geometry.Offset(center - halfLen, center - halfLen),
                                                size = androidx.compose.ui.geometry.Size(halfLen * 2, halfLen * 2),
                                                style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
                                            )
                                        }
                                        "t_shape" -> {
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center, center), androidx.compose.ui.geometry.Offset(center, center + halfLen), strokeWidth)
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center - halfLen, center), androidx.compose.ui.geometry.Offset(center + halfLen, center), strokeWidth)
                                        }
                                        "chevron" -> {
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center - halfLen, center + halfLen), androidx.compose.ui.geometry.Offset(center, center), strokeWidth)
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center + halfLen, center + halfLen), androidx.compose.ui.geometry.Offset(center, center), strokeWidth)
                                        }
                                        "x_shape" -> {
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center - halfLen, center - halfLen), androidx.compose.ui.geometry.Offset(center + halfLen, center + halfLen), strokeWidth)
                                            drawLine(color, androidx.compose.ui.geometry.Offset(center + halfLen, center - halfLen), androidx.compose.ui.geometry.Offset(center - halfLen, center + halfLen), strokeWidth)
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
                            var currentTime by remember { mutableStateOf("") }

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
                                    currentTime =
                                            SimpleDateFormat("HH:mm", Locale.getDefault())
                                                    .format(Date())
                                    delay(1000)
                                }
                            }

                            LaunchedEffect(config) {
                                delay(100)
                                windowManager.updateViewLayout(this@apply, params)
                            }

                            // Stats Pill - Truly Dynamic Wide Layout
                            Row(
                                modifier = Modifier
                                    .onGloballyPositioned { _ ->
                                        windowManager.updateViewLayout(this@apply, params)
                                    }
                                    .background(Color.Black.copy(alpha = 0.9f), RoundedCornerShape(18.dp))
                                    .padding(horizontal = 24.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(40.dp)
                            ) {
                                if (config.showCpu)
                                    MonitorStatItem("CPU", "$cpuUsage%")
                                
                                if (config.showRam)
                                    MonitorStatItem("RAM", "$ramUsage%")
                                
                                if (config.showTemp)
                                    MonitorStatItem("TMP", "${temperature.toInt()}°C")
                                
                                if (config.showFps)
                                    MonitorStatItem("FPS", "120")
                                
                                if (config.showBattery)
                                    MonitorStatItem("BAT", "$batteryLevel%", isBattery = true)
                                
                                if (config.showTime)
                                    MonitorStatItem("TIME", currentTime)
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
    private fun MonitorStatItem(
        label: String,
        value: String,
        isBattery: Boolean = false
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = AccentOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                maxLines = 1,
                softWrap = false
            )
            Spacer(modifier = Modifier.width(16.dp))
            
            if (isBattery) {
                val cleanValue = value.replace("%", "")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = cleanValue,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 14.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "%",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 8.sp,
                        maxLines = 1
                    )
                }
            } else {
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
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
