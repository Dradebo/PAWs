package com.sean.pictureaudiowidget.media

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaPairerTest {
    @Test
    fun `pairs image and audio with same folder and basename`() {
        val images = listOf(
            MediaEntry("img-1", "content://image/1", "Morning", "morning", 50, 1000, "Trips")
        )
        val audios = listOf(
            MediaEntry("aud-1", "content://audio/1", "Morning Audio", "morning", 70, 1200, "Trips")
        )

        val result = MediaPairer.pair(images, audios)

        assertThat(result).hasSize(1)
        assertThat(result.single().audioUri).isEqualTo("content://audio/1")
        assertThat(result.single().pairingConfidence).isEqualTo(PairingConfidence.STRONG)
        assertThat(result.single().sizeBytes).isEqualTo(120)
        assertThat(result.single().modifiedAtEpochMillis).isEqualTo(1200)
    }

    @Test
    fun `keeps unpaired image when no matching audio exists`() {
        val images = listOf(
            MediaEntry("img-1", "content://image/1", "Morning", "morning", 50, 1000, "Trips")
        )

        val result = MediaPairer.pair(images, emptyList())

        assertThat(result).hasSize(1)
        assertThat(result.single().audioUri).isNull()
        assertThat(result.single().pairingConfidence).isEqualTo(PairingConfidence.NONE)
    }

    @Test
    fun `keeps leftover audio as standalone media item`() {
        val audios = listOf(
            MediaEntry("aud-1", "content://audio/1", "Voice Note", "voice-note", 70, 1200, "Inbox")
        )

        val result = MediaPairer.pair(emptyList(), audios)

        assertThat(result).hasSize(1)
        assertThat(result.single().imageUri).isNull()
        assertThat(result.single().audioUri).isEqualTo("content://audio/1")
        assertThat(result.single().displayTitle).isEqualTo("Voice Note")
    }
}
