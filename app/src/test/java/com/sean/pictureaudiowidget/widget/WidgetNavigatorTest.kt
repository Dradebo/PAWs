package com.sean.pictureaudiowidget.widget

import com.google.common.truth.Truth.assertThat
import com.sean.pictureaudiowidget.media.PairingConfidence
import com.sean.pictureaudiowidget.media.SortMode
import com.sean.pictureaudiowidget.media.WidgetMediaItem
import org.junit.Test

class WidgetNavigatorTest {
    private val items = listOf(
        WidgetMediaItem("a", "img://a", null, null, "A", 10, 100, "folder", PairingConfidence.NONE),
        WidgetMediaItem("b", "img://b", null, null, "B", 20, 200, "folder", PairingConfidence.NONE),
        WidgetMediaItem("c", "img://c", null, null, "C", 30, 300, "folder", PairingConfidence.NONE),
    )

    @Test
    fun `next item follows sorted order and wraps around`() {
        val first = WidgetNavigator.nextItemId(items = items, sortMode = SortMode.SIZE, currentMediaId = null, randomSeed = 7)
        val second = WidgetNavigator.nextItemId(items = items, sortMode = SortMode.SIZE, currentMediaId = first, randomSeed = 7)
        val third = WidgetNavigator.nextItemId(items = items, sortMode = SortMode.SIZE, currentMediaId = second, randomSeed = 7)
        val wrapped = WidgetNavigator.nextItemId(items = items, sortMode = SortMode.SIZE, currentMediaId = third, randomSeed = 7)

        assertThat(first).isEqualTo("c")
        assertThat(second).isEqualTo("b")
        assertThat(third).isEqualTo("a")
        assertThat(wrapped).isEqualTo("c")
    }

    @Test
    fun `current item is reset to first sorted item if missing from list`() {
        val result = WidgetNavigator.nextItemId(items = items, sortMode = SortMode.RECENT, currentMediaId = "missing", randomSeed = 9)
        assertThat(result).isEqualTo("c")
    }
}
