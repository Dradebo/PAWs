package com.sean.pictureaudiowidget.data

import com.google.common.truth.Truth.assertThat
import com.sean.pictureaudiowidget.media.SortMode
import kotlinx.coroutines.runBlocking
import org.junit.Test

class WidgetStateRepositoryTest {
    @Test
    fun `cycle sort mode advances and persists the next mode`() = runBlocking {
        val dao = FakeWidgetStateDao()
        val repository = WidgetStateRepository(dao)

        val first = repository.getOrCreate(42)
        val second = repository.cycleSortMode(42)
        val third = repository.cycleSortMode(42)

        assertThat(first.sortMode).isEqualTo(SortMode.RANDOM)
        assertThat(second.sortMode).isEqualTo(SortMode.SIZE)
        assertThat(third.sortMode).isEqualTo(SortMode.RECENT)
        assertThat(dao.get(42)?.sortMode).isEqualTo(SortMode.RECENT)
    }

    private class FakeWidgetStateDao : WidgetStateDao {
        private val map = mutableMapOf<Int, WidgetStateEntity>()

        override suspend fun get(widgetId: Int): WidgetStateEntity? = map[widgetId]

        override suspend fun upsert(entity: WidgetStateEntity) {
            map[entity.widgetId] = entity
        }

        override suspend fun delete(widgetId: Int) {
            map.remove(widgetId)
        }
    }
}
