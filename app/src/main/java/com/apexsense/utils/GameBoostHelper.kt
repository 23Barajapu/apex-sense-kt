package com.apexsense.utils

import android.app.ActivityManager
import android.content.Context
import kotlinx.coroutines.delay

object GameBoostHelper {

    data class BoostStep(val textResId: Int, val progress: Float)

    suspend fun runBoost(
        context: Context,
        steps: List<BoostStep>,
        onProgress: (textResId: Int, progress: Float) -> Unit,
        stepDelayMs: Long = 600L
    ) {
        for ((index, step) in steps.withIndex()) {
            onProgress(step.textResId, step.progress)
            when (index) {
                0 -> clearMemory()
                2 -> trimBackgroundProcesses(context)
            }
            delay(stepDelayMs)
        }
    }

    private fun clearMemory() {
        System.gc()
        Runtime.getRuntime().gc()
    }

    private fun trimBackgroundProcesses(context: Context) {
        try {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val running = activityManager.runningAppProcesses ?: return
            val ownPackage = context.packageName
            for (process in running) {
                val pkg = process.processName.substringBefore(':')
                if (pkg != ownPackage && process.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    activityManager.killBackgroundProcesses(pkg)
                }
            }
        } catch (_: Exception) {
            // Best-effort; may be restricted on newer Android versions
        }
    }
}
