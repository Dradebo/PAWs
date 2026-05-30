package com.sean.pictureaudiowidget.widget

import com.sean.pictureaudiowidget.media.SortMode
import com.sean.pictureaudiowidget.media.WidgetMediaItem

data class WidgetSnapshot(
    val widgetId: Int,
    val sortMode: SortMode,
    val currentItem: WidgetMediaItem?,
    val totalItems: Int,
)
