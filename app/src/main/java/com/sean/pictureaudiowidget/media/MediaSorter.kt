package com.sean.pictureaudiowidget.media

import kotlin.random.Random

object MediaSorter {
    fun sorted(items: List<WidgetMediaItem>, sortMode: SortMode, randomSeed: Int): List<WidgetMediaItem> = when (sortMode) {
        SortMode.SIZE -> items.sortedByDescending { it.sizeBytes }
        SortMode.RECENT -> items.sortedByDescending { it.modifiedAtEpochMillis }
        SortMode.RANDOM -> items.shuffled(Random(randomSeed))
    }
}
