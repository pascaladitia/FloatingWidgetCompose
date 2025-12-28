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

internal class FloatingWidgetService : Service() {

    companion object {
        lateinit var content: (@androidx.compose.runtime.Composable () -> Unit)
        lateinit var config: FloatingWidgetConfig
    }

    private lateinit var wm: WindowManager
    private var composeView: ComposeView? = null

    override fun onCreate() {
        super.onCreate()
        if (!Settings.canDrawOverlays(this)) stopSelf()

        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val owner = OverlayLifecycleOwner().apply { onCreate() }

        composeView = ComposeView(this).apply {
            setTag(
                androidx.lifecycle.runtime.R.id.view_tree_lifecycle_owner,
                owner
            )
            setTag(
                androidx.savedstate.R.id.view_tree_saved_state_registry_owner,
                owner
            )
            setTag(
                androidx.lifecycle.viewmodel.R.id.view_tree_view_model_store_owner,
                owner
            )
            setContent { content() }
        }

        wm.addView(composeView, createParams())
        owner.onStart()
        owner.onResume()
    }

    private fun createParams(): WindowManager.LayoutParams {
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
        composeView?.let { wm.removeView(it) }
        composeView = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
