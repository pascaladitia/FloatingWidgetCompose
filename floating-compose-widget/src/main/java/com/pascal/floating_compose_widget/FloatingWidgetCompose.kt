package com.pascal.floating_compose_widget

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.core.net.toUri

object FloatingWidgetCompose {

    fun canDrawOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun openOverlayPermissionSettings(context: Context) {
        val appContext = context.applicationContext
        val overlayIntent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${appContext.packageName}".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        runCatching {
            appContext.startActivity(overlayIntent)
        }.recoverCatching { error ->
            if (error is ActivityNotFoundException) {
                appContext.startActivity(
                    Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } else {
                throw error
            }
        }
    }

    fun start(
        context: Context,
        config: FloatingWidgetConfig,
        content: @Composable () -> Unit
    ): Boolean {
        val appContext = context.applicationContext
        if (!canDrawOverlays(appContext)) {
            openOverlayPermissionSettings(appContext)
            return false
        }

        FloatingWidgetService.config = config
        FloatingWidgetService.content = content

        return runCatching {
            appContext.startService(
                Intent(appContext, FloatingWidgetService::class.java)
            )
            true
        }.getOrDefault(false)
    }

    fun stop(context: Context) {
        val appContext = context.applicationContext
        appContext.stopService(
            Intent(appContext, FloatingWidgetService::class.java)
        )
    }
}
