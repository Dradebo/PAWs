package com.sean.pictureaudiowidget.media

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaNameNormalizerTest {
    @Test
    fun `normalizes filenames by removing extensions separators and case`() {
        val result = MediaNameNormalizer.normalize("IMG_1001 Final.MP3")

        assertThat(result).isEqualTo("img1001final")
    }

    @Test
    fun `trims blank input to empty string`() {
        val result = MediaNameNormalizer.normalize("   ")

        assertThat(result).isEmpty()
    }
}
