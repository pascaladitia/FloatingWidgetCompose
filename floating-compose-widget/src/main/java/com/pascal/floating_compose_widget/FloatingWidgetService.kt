package com.pascal.floating_compose_widget

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible

internal class FloatingWidgetService : Service() {

    companion object {
        var content: (@androidx.compose.runtime.Composable () -> Unit)? = null
        var config: FloatingWidgetConfig? = null
    }

    private var wm: WindowManager? = null
    private var composeView: ComposeView? = null
    private var owner: OverlayLifecycleOwner? = null

    override fun onCreate() {
        super.onCreate()
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        val widgetContent = content
        val widgetConfig = config
        if (widgetContent == null || widgetConfig == null) {
            stopSelf()
            return
        }

        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val lifecycleOwner = OverlayLifecycleOwner().apply { onCreate() }
        owner = lifecycleOwner

        composeView = ComposeView(this).apply {
            setTag(
                androidx.lifecycle.runtime.R.id.view_tree_lifecycle_owner,
                lifecycleOwner
            )
            setTag(
                androidx.savedstate.R.id.view_tree_saved_state_registry_owner,
                lifecycleOwner
            )
            setTag(
                androidx.lifecycle.viewmodel.R.id.view_tree_view_model_store_owner,
                lifecycleOwner
            )
            setContent { widgetContent() }
        }

        runCatching {
            wm?.addView(composeView, createParams(widgetConfig))
            lifecycleOwner.onStart()
            lifecycleOwner.onResume()
        }.onFailure {
            composeView = null
            stopSelf()
        }
    }

    @Suppress("DEPRECATION")
    private fun createParams(config: FloatingWidgetConfig): WindowManager.LayoutParams {
        val type =
            if (Build.VERSION.SDK_INT >= 26)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE

        val flags = if (config.touchable)
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        else
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

        return WindowManager.LayoutParams(
            if (config.sizeMode == SizeMode.FULL)
                WindowManager.LayoutParams.MATCH_PARENT
            else
                WindowManager.LayoutParams.WRAP_CONTENT,
            if (config.sizeMode == SizeMode.FULL)
                WindowManager.LayoutParams.MATCH_PARENT
            else
                WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            flags,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = config.startX
            y = config.startY
        }
    }

    override fun onDestroy() {
        composeView?.let { view ->
            runCatching {
                if (view.isAttachedToWindow || view.isVisible) {
                    wm?.removeView(view)
                }
            }
        }
        owner?.onDestroy()
        owner = null
        composeView = null
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!Settings.canDrawOverlays(this) || content == null || config == null) {
            stopSelf(startId)
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
