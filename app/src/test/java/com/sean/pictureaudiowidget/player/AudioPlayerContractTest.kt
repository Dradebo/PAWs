package com.sean.pictureaudiowidget.player

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AudioPlayerContractTest {
    @Test
    fun `builds launch model when audio exists`() {
        val result = AudioPlayerContract.createLaunchModel(
            audioUri = "content://audio/1",
            title = "Voice Memo",
        )

        assertThat(result.canLaunch).isTrue()
        assertThat(result.title).isEqualTo("Voice Memo")
    }

    @Test
    fun `disables launch when audio is missing`() {
        val result = AudioPlayerContract.createLaunchModel(
            audioUri = null,
            title = "Voice Memo",
        )

        assertThat(result.canLaunch).isFalse()
        assertThat(result.audioUri).isNull()
    }
}
