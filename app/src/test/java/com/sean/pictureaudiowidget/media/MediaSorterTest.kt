package com.sean.pictureaudiowidget.media

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaSorterTest {
    private val items = listOf(
        WidgetMediaItem("a", "img://a", null, "Old Small", 10, 100, "folder", PairingConfidence.NONE),
        WidgetMediaItem("b", "img://b", null, "New Medium", 20, 300, "folder", PairingConfidence.NONE),
        WidgetMediaItem("c", "img://c", null, "Mid Large", 30, 200, "folder", PairingConfidence.NONE),
    )

    @Test
    fun `size sort orders descending by size`() {
        val result = MediaSorter.sorted(items, SortMode.SIZE, randomSeed = 7)
        assertThat(result.map { it.id }).containsExactly("c", "b", "a").inOrder()
    }

    @Test
    fun `recent sort orders descending by modified timestamp`() {
        val result = MediaSorter.sorted(items, SortMode.RECENT, randomSeed = 7)
        assertThat(result.map { it.id }).containsExactly("b", "c", "a").inOrder()
    }

    @Test
    fun `random sort is stable for the same seed`() {
        val first = MediaSorter.sorted(items, SortMode.RANDOM, randomSeed = 123)
        val second = MediaSorter.sorted(items, SortMode.RANDOM, randomSeed = 123)
        assertThat(first.map { it.id }).containsExactlyElementsIn(second.map { it.id }).inOrder()
    }
}
