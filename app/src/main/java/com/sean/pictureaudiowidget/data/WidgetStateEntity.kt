package com.sean.pictureaudiowidget.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sean.pictureaudiowidget.media.SortMode

@Entity(tableName = "widget_state")
data class WidgetStateEntity(
    @PrimaryKey val widgetId: Int,
    val sortMode: SortMode,
    val currentMediaId: String? = null,
    val randomSeed: Int = widgetId,
)
