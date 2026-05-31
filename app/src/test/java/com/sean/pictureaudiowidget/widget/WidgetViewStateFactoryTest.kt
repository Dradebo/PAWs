package com.sean.pictureaudiowidget.widget

import com.google.common.truth.Truth.assertThat
import com.sean.pictureaudiowidget.media.PairingConfidence
import com.sean.pictureaudiowidget.media.SortMode
import com.sean.pictureaudiowidget.media.WidgetMediaItem
import org.junit.Test

class WidgetViewStateFactoryTest {
    @Test
    fun `builds folder setup state when no sources are selected`() {
        val viewState = WidgetViewStateFactory.create(
            WidgetSnapshot(
                widgetId = 7,
                sortMode = SortMode.RANDOM,
                currentItem = null,
                totalItems = 0,
                selectedSourceCount = 0,
            )
        )

        assertThat(viewState.title).isEqualTo("Choose folders")
        assertThat(viewState.sortLabel).isEqualTo("Random")
        assertThat(viewState.viewerEnabled).isFalse()
        assertThat(viewState.playVisible).isFalse()
    }

    @Test
    fun `builds image plus audio state with viewer and play actions`() {
        val viewState = WidgetViewStateFactory.create(
            WidgetSnapshot(
                widgetId = 7,
                sortMode = SortMode.RECENT,
                currentItem = WidgetMediaItem(
                    id = "x",
                    imageUri = "content://images/7",
                    audioUri = "content://audio/9",
                    videoUri = null,
                    displayTitle = "Road Trip",
                    sizeBytes = 10,
                    modifiedAtEpochMillis = 100,
                    bucketName = "Trips",
                    pairingConfidence = PairingConfidence.STRONG,
                ),
                totalItems = 3,
                selectedSourceCount = 1,
            )
        )

        assertThat(viewState.title).isEqualTo("Road Trip")
        assertThat(viewState.subtitle).isEqualTo("3 items • photo + sound")
        assertThat(viewState.previewUri).isEqualTo("content://images/7")
        assertThat(viewState.viewerEnabled).isTrue()
        assertThat(viewState.playVisible).isTrue()
        assertThat(viewState.playEnabled).isTrue()
    }
}
