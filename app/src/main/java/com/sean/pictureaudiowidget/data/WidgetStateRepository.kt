package com.sean.pictureaudiowidget.data

import com.sean.pictureaudiowidget.media.SortMode

class WidgetStateRepository(
    private val dao: WidgetStateDao,
) : WidgetStateStore {
    override suspend fun getOrCreate(widgetId: Int): WidgetStateEntity {
        val existing = dao.get(widgetId)
        if (existing != null) return existing

        val created = WidgetStateEntity(widgetId = widgetId, sortMode = SortMode.RANDOM)
        dao.upsert(created)
        return created
    }

    override suspend fun cycleSortMode(widgetId: Int): WidgetStateEntity {
        val current = getOrCreate(widgetId)
        val updated = current.copy(sortMode = current.sortMode.next(), currentMediaId = null)
        dao.upsert(updated)
        return updated
    }

    override suspend fun shuffle(widgetId: Int): WidgetStateEntity {
        val current = getOrCreate(widgetId)
        val updated = current.copy(
            sortMode = SortMode.RANDOM,
            currentMediaId = null,
            randomSeed = (System.currentTimeMillis() xor widgetId.toLong()).toInt(),
        )
        dao.upsert(updated)
        return updated
    }

    override suspend fun setCurrentMediaId(widgetId: Int, mediaId: String?): WidgetStateEntity {
        val current = getOrCreate(widgetId)
        val updated = current.copy(currentMediaId = mediaId)
        dao.upsert(updated)
        return updated
    }

    override suspend fun setSelectedSourceBuckets(widgetId: Int, buckets: Set<String>): WidgetStateEntity {
        val current = getOrCreate(widgetId)
        val updated = current.copy(
            selectedSourceBuckets = buckets.sorted().joinToString(separator = "\n"),
            currentMediaId = null,
        )
        dao.upsert(updated)
        return updated
    }

    override suspend fun delete(widgetId: Int) {
        dao.delete(widgetId)
    }
}
