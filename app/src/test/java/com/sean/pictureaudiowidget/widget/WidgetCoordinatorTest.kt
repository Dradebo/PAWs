package com.sean.pictureaudiowidget.widget

import com.google.common.truth.Truth.assertThat
import com.sean.pictureaudiowidget.data.WidgetStateEntity
import com.sean.pictureaudiowidget.data.WidgetStateStore
import com.sean.pictureaudiowidget.media.MediaCatalogRepository
import com.sean.pictureaudiowidget.media.PairingConfidence
import com.sean.pictureaudiowidget.media.SortMode
import com.sean.pictureaudiowidget.media.WidgetMediaItem
import kotlinx.coroutines.runBlocking
import org.junit.Test

class WidgetCoordinatorTest {
    private val catalog = listOf(
        WidgetMediaItem("a", "content://images/1", null, "Old", 10, 100, "Trips", PairingConfidence.NONE),
        WidgetMediaItem("b", "content://images/2", "content://audio/2", "Newest", 20, 300, "Trips", PairingConfidence.STRONG),
        WidgetMediaItem("c", null, "content://audio/3", "Middle", 30, 200, "Trips", PairingConfidence.NONE),
        WidgetMediaItem("d", "content://images/4", null, "Other", 40, 400, "Other", PairingConfidence.NONE),
    )

    @Test
    fun `snapshot returns current media item from stored state within selected source`() = runBlocking {
        val stateStore = FakeWidgetStateStore(
            WidgetStateEntity(widgetId = 9, sortMode = SortMode.RECENT, currentMediaId = "c", randomSeed = 4, selectedSourceBuckets = "Trips")
        )
        val coordinator = WidgetCoordinator(FakeCatalogRepository(catalog), stateStore)

        val snapshot = coordinator.snapshot(9)

        assertThat(snapshot.currentItem?.id).isEqualTo("c")
        assertThat(snapshot.sortMode).isEqualTo(SortMode.RECENT)
        assertThat(snapshot.totalItems).isEqualTo(3)
        assertThat(snapshot.selectedSourceCount).isEqualTo(1)
    }

    @Test
    fun `next advances and persists the next item id`() = runBlocking {
        val stateStore = FakeWidgetStateStore(
            WidgetStateEntity(widgetId = 9, sortMode = SortMode.RECENT, currentMediaId = "c", randomSeed = 4, selectedSourceBuckets = "Trips")
        )
        val coordinator = WidgetCoordinator(FakeCatalogRepository(catalog), stateStore)

        val snapshot = coordinator.next(9)

        assertThat(snapshot.currentItem?.id).isEqualTo("a")
        assertThat(stateStore.saved.last().currentMediaId).isEqualTo("a")
    }

    @Test
    fun `cycle sort updates sort mode and resets current item to first in new order`() = runBlocking {
        val stateStore = FakeWidgetStateStore(
            WidgetStateEntity(widgetId = 9, sortMode = SortMode.RANDOM, currentMediaId = "c", randomSeed = 2, selectedSourceBuckets = "Trips")
        )
        val coordinator = WidgetCoordinator(FakeCatalogRepository(catalog), stateStore)

        val snapshot = coordinator.cycleSort(9)

        assertThat(snapshot.sortMode).isEqualTo(SortMode.SIZE)
        assertThat(snapshot.currentItem?.id).isEqualTo("c")
        assertThat(stateStore.saved.last().sortMode).isEqualTo(SortMode.SIZE)
    }

    @Test
    fun `snapshot is empty until widget has selected sources`() = runBlocking {
        val stateStore = FakeWidgetStateStore(
            WidgetStateEntity(widgetId = 9, sortMode = SortMode.RANDOM)
        )
        val coordinator = WidgetCoordinator(FakeCatalogRepository(catalog), stateStore)

        val snapshot = coordinator.snapshot(9)

        assertThat(snapshot.currentItem).isNull()
        assertThat(snapshot.totalItems).isEqualTo(0)
        assertThat(snapshot.selectedSourceCount).isEqualTo(0)
    }

    private class FakeCatalogRepository(
        private val items: List<WidgetMediaItem>,
    ) : MediaCatalogRepository {
        override suspend fun loadCatalog(): List<WidgetMediaItem> = items
    }

    private class FakeWidgetStateStore(
        initial: WidgetStateEntity,
    ) : WidgetStateStore {
        private val data = mutableMapOf(initial.widgetId to initial)
        val saved = mutableListOf<WidgetStateEntity>()

        override suspend fun getOrCreate(widgetId: Int): WidgetStateEntity {
            return data[widgetId] ?: WidgetStateEntity(widgetId = widgetId, sortMode = SortMode.RANDOM).also {
                data[widgetId] = it
                saved += it
            }
        }

        override suspend fun cycleSortMode(widgetId: Int): WidgetStateEntity {
            val updated = getOrCreate(widgetId).copy(sortMode = getOrCreate(widgetId).sortMode.next(), currentMediaId = null)
            data[widgetId] = updated
            saved += updated
            return updated
        }

        override suspend fun shuffle(widgetId: Int): WidgetStateEntity {
            val updated = getOrCreate(widgetId).copy(sortMode = SortMode.RANDOM, currentMediaId = null, randomSeed = 99)
            data[widgetId] = updated
            saved += updated
            return updated
        }

        override suspend fun setCurrentMediaId(widgetId: Int, mediaId: String?): WidgetStateEntity {
            val updated = getOrCreate(widgetId).copy(currentMediaId = mediaId)
            data[widgetId] = updated
            saved += updated
            return updated
        }

        override suspend fun setSelectedSourceBuckets(widgetId: Int, buckets: Set<String>): WidgetStateEntity {
            val updated = getOrCreate(widgetId).copy(selectedSourceBuckets = buckets.sorted().joinToString("\n"), currentMediaId = null)
            data[widgetId] = updated
            saved += updated
            return updated
        }

        override suspend fun delete(widgetId: Int) {
            data.remove(widgetId)
        }
    }
}
