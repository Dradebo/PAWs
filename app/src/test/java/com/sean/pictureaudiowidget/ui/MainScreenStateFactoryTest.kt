package com.sean.pictureaudiowidget.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MainScreenStateFactoryTest {
    @Test
    fun `shows permission prompt when permissions are missing`() {
        val state = MainScreenStateFactory.create(hasPermissions = false, indexedItems = 0, sourceFolderCount = 0)

        assertThat(state.status).isEqualTo("Grant media permissions to start indexing local pictures and audio.")
        assertThat(state.showRefresh).isFalse()
        assertThat(state.showFolderBrowserButton).isFalse()
    }

    @Test
    fun `shows control center summaries when permissions exist`() {
        val state = MainScreenStateFactory.create(hasPermissions = true, indexedItems = 5, sourceFolderCount = 2)

        assertThat(state.status).isEqualTo("Library ready")
        assertThat(state.librarySummary).isEqualTo("5 paired media items across 2 folders.")
        assertThat(state.showRefresh).isTrue()
        assertThat(state.showFolderBrowserButton).isTrue()
    }
}
