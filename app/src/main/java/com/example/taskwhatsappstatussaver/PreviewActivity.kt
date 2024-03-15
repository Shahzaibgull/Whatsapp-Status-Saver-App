package com.example.taskwhatsappstatussaver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView


class PreviewActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var player: SimpleExoPlayer
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        playerView = findViewById(R.id.playerView)
        imageView = findViewById(R.id.imageView) // Initialize imageView

        player = SimpleExoPlayer.Builder(this).build()

        val uriString = intent.getStringExtra("URI")
        if (!uriString.isNullOrBlank()) {
            val uri = Uri.parse(uriString)

            if (uriString.endsWith(".mp4")) {
                val mediaItem = MediaItem.fromUri(uri)
                player.setMediaItem(mediaItem)
                playerView.player = player
                player.prepare()
                player.play()
            } else {
                // It's an image, handle image loading here using Glide or any other library
                // For example, you can use Glide:
                Glide.with(this)
                    .load(uri)
                    .into(imageView)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
