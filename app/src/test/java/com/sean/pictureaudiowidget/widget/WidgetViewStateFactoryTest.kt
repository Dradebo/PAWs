package com.sean.pictureaudiowidget.widget

import com.google.common.truth.Truth.assertThat
import com.sean.pictureaudiowidget.media.PairingConfidence
import com.sean.pictureaudiowidget.media.SortMode
import com.sean.pictureaudiowidget.media.WidgetMediaItem
import org.junit.Test

class WidgetViewStateFactoryTest {
    @Test
    fun `builds empty state when no media is available`() {
        val viewState = WidgetViewStateFactory.create(
            WidgetSnapshot(
                widgetId = 7,
                sortMode = SortMode.RANDOM,
                currentItem = null,
                totalItems = 0,
            )
        )

        assertThat(viewState.title).isEqualTo("Ready to browse")
        assertThat(viewState.sortLabel).isEqualTo("Random")
        assertThat(viewState.openImageEnabled).isFalse()
        assertThat(viewState.openAudioEnabled).isFalse()
    }

    @Test
    fun `builds populated state with media specific actions`() {
        val viewState = WidgetViewStateFactory.create(
            WidgetSnapshot(
                widgetId = 7,
                sortMode = SortMode.RECENT,
                currentItem = WidgetMediaItem(
                    id = "x",
                    imageUri = "content://images/7",
                    audioUri = "content://audio/9",
                    displayTitle = "Road Trip",
                    sizeBytes = 10,
                    modifiedAtEpochMillis = 100,
                    bucketName = "Trips",
                    pairingConfidence = PairingConfidence.STRONG,
                ),
                totalItems = 3,
            )
        )

        assertThat(viewState.title).isEqualTo("Road Trip")
        assertThat(viewState.subtitle).isEqualTo("3 items • image + audio")
        assertThat(viewState.imageUri).isEqualTo("content://images/7")
        assertThat(viewState.openImageEnabled).isTrue()
        assertThat(viewState.openAudioEnabled).isTrue()
    }
}
