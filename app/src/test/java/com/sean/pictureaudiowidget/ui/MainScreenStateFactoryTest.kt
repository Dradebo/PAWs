package com.sean.pictureaudiowidget.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MainScreenStateFactoryTest {
    @Test
    fun `shows permission prompt when permissions are missing`() {
        val state = MainScreenStateFactory.create(hasPermissions = false, indexedItems = 0)

        assertThat(state.status).isEqualTo("Grant media permissions to start indexing local pictures and audio.")
        assertThat(state.showRefresh).isFalse()
    }

    @Test
    fun `shows refresh state when permissions exist`() {
        val state = MainScreenStateFactory.create(hasPermissions = true, indexedItems = 5)

        assertThat(state.status).isEqualTo("Indexed items available: 5")
        assertThat(state.showRefresh).isTrue()
    }
}
