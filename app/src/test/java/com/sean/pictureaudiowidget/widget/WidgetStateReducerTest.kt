package com.sean.pictureaudiowidget.widget

import com.google.common.truth.Truth.assertThat
import com.sean.pictureaudiowidget.media.PairingConfidence
import com.sean.pictureaudiowidget.media.SortMode
import com.sean.pictureaudiowidget.media.WidgetMediaItem
import org.junit.Test

class WidgetStateReducerTest {
    private val items = listOf(
        WidgetMediaItem(id = "a", imageUri = null, audioUri = null, videoUri = null, displayTitle = "Old Small", sizeBytes = 10, modifiedAtEpochMillis = 100, bucketName = null, pairingConfidence = PairingConfidence.NONE),
        WidgetMediaItem(id = "b", imageUri = null, audioUri = null, videoUri = null, displayTitle = "New Medium", sizeBytes = 20, modifiedAtEpochMillis = 300, bucketName = null, pairingConfidence = PairingConfidence.NONE),
        WidgetMediaItem(id = "c", imageUri = null, audioUri = null, videoUri = null, displayTitle = "Mid Large", sizeBytes = 30, modifiedAtEpochMillis = 200, bucketName = null, pairingConfidence = PairingConfidence.NONE),
    )

    @Test
    fun `next wraps around within current sort order`() {
        val initial = WidgetSessionState(appWidgetId = 7, sortMode = SortMode.SIZE, currentIndex = 2, randomSeed = 11)
        val next = WidgetStateReducer.next(items = items, current = initial)

        assertThat(next.currentIndex).isEqualTo(0)
        assertThat(WidgetStateReducer.currentItem(items, next)?.id).isEqualTo("c")
    }

    @Test
    fun `cycling sort changes mode and resets position to first item`() {
        val initial = WidgetSessionState(appWidgetId = 7, sortMode = SortMode.RANDOM, currentIndex = 2, randomSeed = 11)
        val next = WidgetStateReducer.cycleSort(items = items, current = initial)

        assertThat(next.sortMode).isEqualTo(SortMode.SIZE)
        assertThat(next.currentIndex).isEqualTo(0)
        assertThat(WidgetStateReducer.currentItem(items, next)?.id).isEqualTo("c")
    }

    @Test
    fun `current item returns null for empty media list`() {
        val state = WidgetSessionState(appWidgetId = 7, sortMode = SortMode.RECENT, currentIndex = 0, randomSeed = 11)
        val currentItem = WidgetStateReducer.currentItem(items = emptyList(), current = state)

        assertThat(currentItem).isNull()
    }
}
