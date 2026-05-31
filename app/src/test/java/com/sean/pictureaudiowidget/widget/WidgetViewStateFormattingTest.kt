package com.sean.pictureaudiowidget.widget

import com.google.common.truth.Truth.assertThat
import com.sean.pictureaudiowidget.media.PairingConfidence
import com.sean.pictureaudiowidget.media.SortMode
import com.sean.pictureaudiowidget.media.WidgetMediaItem
import org.junit.Test

class WidgetViewStateFormattingTest {
    @Test
    fun `cleans camera filenames for widget title`() {
        val viewState = WidgetViewStateFactory.create(
            WidgetSnapshot(
                widgetId = 7,
                sortMode = SortMode.RANDOM,
                currentItem = WidgetMediaItem(
                    id = "x",
                    imageUri = "content://images/7",
                    audioUri = null,
                    videoUri = null,
                    displayTitle = "IMG-20240901-WA0002.jpg",
                    sizeBytes = 10,
                    modifiedAtEpochMillis = 100,
                    bucketName = "Camera",
                    pairingConfidence = PairingConfidence.NONE,
                ),
                totalItems = 44648,
                selectedSourceCount = 1,
            )
        )

        assertThat(viewState.title).isEqualTo("IMG 20240901 WA0002")
        assertThat(viewState.subtitle).isEqualTo("44.6k items • Camera")
        assertThat(viewState.sortLabel).isEqualTo("Random")
        assertThat(viewState.playVisible).isFalse()
    }
}
