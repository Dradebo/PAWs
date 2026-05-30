package com.sean.pictureaudiowidget.data

import com.sean.pictureaudiowidget.media.SortMode

class WidgetStateRepository(
    private val dao: WidgetStateDao,
) : WidgetStateStore {
    override suspend fun getOrCreate(widgetId: Int): WidgetStateEntity {
        return dao.get(widgetId) ?: WidgetStateEntity(widgetId = widgetId, sortMode = SortMode.RANDOM)
            .also(dao::upsert)
    }

    override suspend fun cycleSortMode(widgetId: Int): WidgetStateEntity {
        val current = getOrCreate(widgetId)
        val updated = current.copy(sortMode = current.sortMode.next(), currentMediaId = null)
        dao.upsert(updated)
        return updated
    }

    override suspend fun setCurrentMediaId(widgetId: Int, mediaId: String?): WidgetStateEntity {
        val current = getOrCreate(widgetId)
        val updated = current.copy(currentMediaId = mediaId)
        dao.upsert(updated)
        return updated
    }

    override suspend fun delete(widgetId: Int) {
        dao.delete(widgetId)
    }
}
