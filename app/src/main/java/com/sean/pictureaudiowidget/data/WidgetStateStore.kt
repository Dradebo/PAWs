package com.sean.pictureaudiowidget.data

interface WidgetStateStore {
    suspend fun getOrCreate(widgetId: Int): WidgetStateEntity
    suspend fun cycleSortMode(widgetId: Int): WidgetStateEntity
    suspend fun setCurrentMediaId(widgetId: Int, mediaId: String?): WidgetStateEntity
    suspend fun delete(widgetId: Int)
}
