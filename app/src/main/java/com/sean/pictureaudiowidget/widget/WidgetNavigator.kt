package com.sean.pictureaudiowidget.widget

import com.sean.pictureaudiowidget.media.MediaSorter
import com.sean.pictureaudiowidget.media.SortMode
import com.sean.pictureaudiowidget.media.WidgetMediaItem

object WidgetNavigator {
    fun currentItemId(
        items: List<WidgetMediaItem>,
        sortMode: SortMode,
        currentMediaId: String?,
        randomSeed: Int,
    ): String? {
        if (items.isEmpty()) return null

        val sorted = MediaSorter.sorted(items, sortMode, randomSeed)
        return sorted.firstOrNull { it.id == currentMediaId }?.id ?: sorted.first().id
    }

    fun nextItemId(
        items: List<WidgetMediaItem>,
        sortMode: SortMode,
        currentMediaId: String?,
        randomSeed: Int,
    ): String? {
        if (items.isEmpty()) return null

        val sorted = MediaSorter.sorted(items, sortMode, randomSeed)
        val currentIndex = sorted.indexOfFirst { it.id == currentMediaId }

        return if (currentIndex == -1) {
            sorted.first().id
        } else {
            sorted[(currentIndex + 1) % sorted.size].id
        }
    }
}
