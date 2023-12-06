package com.ko2ic.sample.ui.view

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.ko2ic.sample.databinding.PlayerViewItemBinding
import com.ko2ic.sample.ui.common.ItemViewTypeProvider
import com.ko2ic.sample.ui.controller.ShortMovieItemViewModel
import com.newspicks.ui.views.recyclerview.common.RecyclerViewAdapter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ShortMovieViewAdapter(
    list: ObservableArrayList<ShortMovieItemViewModel>,
    viewTypeProvider: ItemViewTypeProvider,
    onPostBindViewListener: ((ShortMovieItemViewModel, ViewGroup) -> Unit)? = null
) : RecyclerViewAdapter<ShortMovieItemViewModel>(
    list,
    viewTypeProvider,
    onPostBindViewListener
) {

    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
    }

    suspend fun attach(playerView: ShorMoviePlayerView, position: Int) {
        this.recyclerView?.let { recyclerView ->

            var viewHolder: ItemViewHolder?
            do {
                viewHolder =
                    recyclerView.findViewHolderForAdapterPosition(position) as? ItemViewHolder
            } while (viewHolder == null && recyclerView.awaitLayout() == Unit)

            viewHolder?.let { holder ->
                DataBindingUtil.findBinding<PlayerViewItemBinding>(holder.itemView)?.let { binding ->
                    if (binding.playerContainer == playerView.view.parent) {
                        // Already attached
                        return
                    }

                    playerView.view.findParentById(binding.root.id)
                        ?.let {
                            DataBindingUtil.bind<PlayerViewItemBinding>(it)
                        }?.apply {
                            playerContainer.removeView(playerView.view)
                            previewImage.isVisible = true
                        }
                    binding.playerContainer.addView(playerView.view)
                }
            }
        }
    }

    fun hidePreviewImage(position: Int) {
        this.recyclerView?.let { recyclerView ->
            recyclerView.findViewHolderForAdapterPosition(position)?.let { holder ->
                DataBindingUtil.findBinding<PlayerViewItemBinding>(holder.itemView)?.let { binding ->
                    binding.previewImage.isVisible = false
                    binding.playerContainer.isVisible = true
                }
            }
        }
    }
}

internal suspend fun View.awaitLayout() = suspendCoroutine<Unit> { cont ->
    doOnLayout {
        cont.resume(Unit)
    }
}

internal fun View.findParentById(@IdRes id: Int): ViewGroup? {
    return if (this.id == id) {
        this as? ViewGroup
    } else {
        (parent as? View)?.findParentById(id)
    }
}