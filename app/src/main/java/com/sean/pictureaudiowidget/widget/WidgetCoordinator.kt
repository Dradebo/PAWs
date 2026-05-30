package com.sean.pictureaudiowidget.widget

import com.sean.pictureaudiowidget.data.WidgetStateStore
import com.sean.pictureaudiowidget.media.MediaCatalogRepository

class WidgetCoordinator(
    private val mediaCatalogRepository: MediaCatalogRepository,
    private val widgetStateStore: WidgetStateStore,
) {
    suspend fun snapshot(widgetId: Int): WidgetSnapshot {
        val state = widgetStateStore.getOrCreate(widgetId)
        val items = mediaCatalogRepository.loadCatalog()
        val currentId = WidgetNavigator.currentItemId(
            items = items,
            sortMode = state.sortMode,
            currentMediaId = state.currentMediaId,
            randomSeed = state.randomSeed,
        )
        if (currentId != state.currentMediaId) {
            widgetStateStore.setCurrentMediaId(widgetId, currentId)
        }
        val currentItem = items.firstOrNull { it.id == currentId }
        return WidgetSnapshot(
            widgetId = widgetId,
            sortMode = state.sortMode,
            currentItem = currentItem,
            totalItems = items.size,
        )
    }

    suspend fun next(widgetId: Int): WidgetSnapshot {
        val state = widgetStateStore.getOrCreate(widgetId)
        val items = mediaCatalogRepository.loadCatalog()
        val nextId = WidgetNavigator.nextItemId(
            items = items,
            sortMode = state.sortMode,
            currentMediaId = state.currentMediaId,
            randomSeed = state.randomSeed,
        )
        widgetStateStore.setCurrentMediaId(widgetId, nextId)
        return snapshot(widgetId)
    }

    suspend fun cycleSort(widgetId: Int): WidgetSnapshot {
        val state = widgetStateStore.cycleSortMode(widgetId)
        val items = mediaCatalogRepository.loadCatalog()
        val currentId = WidgetNavigator.currentItemId(
            items = items,
            sortMode = state.sortMode,
            currentMediaId = null,
            randomSeed = state.randomSeed,
        )
        widgetStateStore.setCurrentMediaId(widgetId, currentId)
        return snapshot(widgetId)
    }

    suspend fun clear(widgetId: Int) {
        widgetStateStore.delete(widgetId)
    }
}
