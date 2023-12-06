package com.ko2ic.sample.ui.view.player

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.ko2ic.sample.model.PlayerState
import com.ko2ic.sample.ui.controller.ShortMovieItemViewModel

class ShortMoviePlayer(
    val player: Player,
) {

    val currentPlayerState: PlayerState get() = toPlayerState()
    private var isPlayerSetUp = false

    fun setListener(listener: () -> Unit) {
        val listener = object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        listener.invoke()
                    }
                }
            }
        }
        player.addListener(listener)
    }

    fun setUpWith(shortMovieDto: List<ShortMovieItemViewModel>, playerState: PlayerState?) {

        player.addMediaItems(0, shortMovieDto.toMediaItems())

        if (!isPlayerSetUp) {
            val currentMediaItems = List(player.mediaItemCount, player::getMediaItemAt)

            val hasPlayerState =
                playerState != null && currentMediaItems.any { mediaItem -> mediaItem.mediaId == playerState.currentMediaItemId }

            val playerState = if (hasPlayerState) {
                requireNotNull(playerState)
            } else {
                PlayerState.INITIAL
            }

            player.playWhenReady = playerState.isPlaying

            isPlayerSetUp = true
        }
    }

    fun prepare(position: Int, currentPosition: Long? = null) {
        if (player.currentMediaItemIndex == position && player.isPlaying) return
        player.seekTo(position, currentPosition ?: 0L)
        player.playWhenReady = true
        player.prepare()
    }

    fun play() {
        player.prepare()
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun release() {
        player.release()
    }

    private fun List<ShortMovieItemViewModel>.toMediaItems(): List<MediaItem> {
        return map { viewModel ->
            MediaItem.Builder()
                .setMediaId(viewModel.id)
                .setUri(viewModel.mediaUri)
                .build()
        }
    }

    private fun toPlayerState(): PlayerState {
        return PlayerState(
            currentMediaItemId = player.currentMediaItem?.mediaId,
            currentMediaItemIndex = player.currentMediaItemIndex,
            seekPositionMillis = player.currentPosition,
            isPlaying = player.playWhenReady
        )
    }
}
