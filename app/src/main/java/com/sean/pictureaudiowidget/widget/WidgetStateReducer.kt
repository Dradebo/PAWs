package com.sean.pictureaudiowidget.widget

import com.sean.pictureaudiowidget.media.MediaSorter
import com.sean.pictureaudiowidget.media.WidgetMediaItem

object WidgetStateReducer {

    fun currentItem(items: List<WidgetMediaItem>, current: WidgetSessionState): WidgetMediaItem? {
        val sorted = sortedItems(items, current)
        if (sorted.isEmpty()) return null
        val normalizedIndex = current.currentIndex.mod(sorted.size)
        return sorted[normalizedIndex]
    }

    fun next(items: List<WidgetMediaItem>, current: WidgetSessionState): WidgetSessionState {
        val sorted = sortedItems(items, current)
        if (sorted.isEmpty()) return current.copy(currentIndex = 0)
        return current.copy(currentIndex = (current.currentIndex + 1).mod(sorted.size))
    }

    fun cycleSort(items: List<WidgetMediaItem>, current: WidgetSessionState): WidgetSessionState {
        val nextState = current.copy(sortMode = current.sortMode.next(), currentIndex = 0)
        return if (items.isEmpty()) nextState else nextState.copy(currentIndex = 0)
    }

    private fun sortedItems(items: List<WidgetMediaItem>, current: WidgetSessionState): List<WidgetMediaItem> {
        return MediaSorter.sorted(items, current.sortMode, current.randomSeed)
    }
}