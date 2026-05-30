package com.sean.pictureaudiowidget.player

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.sean.pictureaudiowidget.R

class AudioPlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var titleView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        playerView = findViewById(R.id.playerView)
        titleView = findViewById(R.id.playerTitle)
        titleView.text = intent.getStringExtra(AudioPlayerContract.EXTRA_TITLE).orEmpty()
    }

    override fun onStart() {
        super.onStart()
        val model = AudioPlayerContract.createLaunchModel(
            audioUri = intent.getStringExtra(AudioPlayerContract.EXTRA_AUDIO_URI),
            title = intent.getStringExtra(AudioPlayerContract.EXTRA_TITLE).orEmpty(),
        )
        if (!model.canLaunch) {
            finish()
            return
        }

        val exoPlayer = ExoPlayer.Builder(this).build()
        player = exoPlayer
        playerView.player = exoPlayer
        exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(model.audioUri)))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    override fun onStop() {
        playerView.player = null
        player?.release()
        player = null
        super.onStop()
    }
}
