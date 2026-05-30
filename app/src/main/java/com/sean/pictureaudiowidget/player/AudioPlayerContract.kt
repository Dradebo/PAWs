package com.sean.pictureaudiowidget.player

import android.content.Context
import android.content.Intent

object AudioPlayerContract {
    const val EXTRA_AUDIO_URI = "extra_audio_uri"
    const val EXTRA_TITLE = "extra_title"

    fun createLaunchModel(audioUri: String?, title: String): LaunchModel {
        return LaunchModel(
            audioUri = audioUri,
            title = title,
            canLaunch = !audioUri.isNullOrBlank(),
        )
    }

    fun createIntent(context: Context, audioUri: String, title: String): Intent {
        return Intent(context, AudioPlayerActivity::class.java)
            .putExtra(EXTRA_AUDIO_URI, audioUri)
            .putExtra(EXTRA_TITLE, title)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    data class LaunchModel(
        val audioUri: String?,
        val title: String,
        val canLaunch: Boolean,
    )
}
