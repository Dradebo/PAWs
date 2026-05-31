package com.sean.pictureaudiowidget.media

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaPairerTest {
    @Test
    fun `pairs image and audio with same folder and basename`() {
        val images = listOf(
            MediaEntry("img-1", "content://image/1", "Morning", "morning", 50, 1000, "Trips", MediaKind.IMAGE)
        )
        val audios = listOf(
            MediaEntry("aud-1", "content://audio/1", "Morning Audio", "morning", 70, 1200, "Trips", MediaKind.AUDIO)
        )

        val result = MediaPairer.pair(images, audios, emptyList())

        assertThat(result).hasSize(1)
        assertThat(result.single().audioUri).isEqualTo("content://audio/1")
        assertThat(result.single().videoUri).isNull()
        assertThat(result.single().pairingConfidence).isEqualTo(PairingConfidence.STRONG)
        assertThat(result.single().sizeBytes).isEqualTo(120)
        assertThat(result.single().modifiedAtEpochMillis).isEqualTo(1200)
    }

    @Test
    fun `keeps unpaired image when no matching audio exists`() {
        val images = listOf(
            MediaEntry("img-1", "content://image/1", "Morning", "morning", 50, 1000, "Trips", MediaKind.IMAGE)
        )

        val result = MediaPairer.pair(images, emptyList(), emptyList())

        assertThat(result).hasSize(1)
        assertThat(result.single().audioUri).isNull()
        assertThat(result.single().videoUri).isNull()
        assertThat(result.single().pairingConfidence).isEqualTo(PairingConfidence.NONE)
    }

    @Test
    fun `keeps leftover audio as standalone media item`() {
        val audios = listOf(
            MediaEntry("aud-1", "content://audio/1", "Voice Note", "voice-note", 70, 1200, "Inbox", MediaKind.AUDIO)
        )

        val result = MediaPairer.pair(emptyList(), audios, emptyList())

        assertThat(result).hasSize(1)
        assertThat(result.single().imageUri).isNull()
        assertThat(result.single().audioUri).isEqualTo("content://audio/1")
        assertThat(result.single().displayTitle).isEqualTo("Voice Note")
    }

    @Test
    fun `keeps videos as standalone playable media items`() {
        val videos = listOf(
            MediaEntry("vid-1", "content://video/1", "Clip", "clip", 90, 1300, "Trips", MediaKind.VIDEO)
        )

        val result = MediaPairer.pair(emptyList(), emptyList(), videos)

        assertThat(result).hasSize(1)
        assertThat(result.single().videoUri).isEqualTo("content://video/1")
        assertThat(result.single().canPlay).isTrue()
    }
}
