package com.pascal.floating_compose_widget

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.core.net.toUri

object FloatingWidgetCompose {

    fun start(
        context: Context,
        config: FloatingWidgetConfig,
        content: @Composable () -> Unit
    ) {
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${context.packageName}".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        }

        FloatingWidgetService.config = config
        FloatingWidgetService.content = content

        context.startService(
            Intent(context, FloatingWidgetService::class.java)
        )
    }

    fun stop(context: Context) {
        context.stopService(
            Intent(context, FloatingWidgetService::class.java)
        )
    }
}
