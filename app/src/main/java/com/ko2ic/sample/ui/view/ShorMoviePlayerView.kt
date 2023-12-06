package com.ko2ic.sample.ui.view

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.BindingAdapter
import com.ko2ic.sample.databinding.PlayerViewBinding
import com.ko2ic.sample.ui.view.player.ShortMoviePlayer

class ShorMoviePlayerView(layoutInflater: LayoutInflater) {

    private val binding = PlayerViewBinding.inflate(layoutInflater)
    val view: View = binding.root

    fun attach(appPlayer: ShortMoviePlayer) {
        binding.playerView.player = appPlayer.player
    }

    fun detachPlayer() {
        binding.playerView.player = null
    }
}

@BindingAdapter("android:visibility")
fun View.bindVisibility(visible: Boolean?) {
    val visibility = if (visible != null && visible) View.VISIBLE else View.GONE
    this.visibility = visibility
}
