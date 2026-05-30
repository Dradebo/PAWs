package com.sean.pictureaudiowidget.widget

import com.sean.pictureaudiowidget.media.SortMode

data class WidgetSessionState(
    val appWidgetId: Int,
    val sortMode: SortMode,
    val currentIndex: Int,
    val randomSeed: Int,
)