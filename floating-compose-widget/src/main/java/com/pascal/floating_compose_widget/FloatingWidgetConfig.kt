package com.pascal.floating_compose_widget

data class FloatingWidgetConfig(
    val touchable: Boolean = true,
    val sizeMode: SizeMode = SizeMode.WRAP,
    val startX: Int = 0,
    val startY: Int = 200
)
