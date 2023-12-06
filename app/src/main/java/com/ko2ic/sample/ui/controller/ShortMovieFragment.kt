package com.ko2ic.sample.ui.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import coil.ImageLoader
import coil.load
import com.ko2ic.sample.R
import com.ko2ic.sample.databinding.FragmentShortMovieBinding
import com.ko2ic.sample.databinding.PlayerViewItemBinding
import com.ko2ic.sample.ui.common.CollectionItemViewModel
import com.ko2ic.sample.ui.common.ItemViewTypeProvider
import com.ko2ic.sample.ui.view.ShorMoviePlayerView
import com.ko2ic.sample.ui.view.ShortMovieViewAdapter
import com.ko2ic.sample.ui.view.player.ShortMoviePlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ShortMovieFragment : Fragment(R.layout.fragment_short_movie) {

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var player: ShortMoviePlayer

    private val viewModel: ShortMovieViewModel by viewModels()

    private var shortMovieAdapter: ShortMovieViewAdapter? = null

    private var binding: FragmentShortMovieBinding? = null

    private var playerView: ShorMoviePlayerView? = null

    private val onPageChangeListener: ViewPager2.OnPageChangeCallback by lazy {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                if (state != ViewPager2.SCROLL_STATE_IDLE) return
                viewModel.updateCurrentPage(binding!!.viewPager.currentItem)
                player.prepare(binding!!.viewPager.currentItem)
                lifecycleScope.launch {
                    shortMovieAdapter?.attach(playerView!!, binding!!.viewPager.currentItem)
                }
            }

            override fun onPageSelected(position: Int) {
                if (binding!!.viewPager.scrollState == ViewPager2.SCROLL_STATE_IDLE) return
                player.pause()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentShortMovieBinding.bind(view)
        this.binding = binding
        playerView = ShorMoviePlayerView(LayoutInflater.from(view.context))

        initShortMovieAdapter()

        binding.viewPager.adapter = shortMovieAdapter
        binding.viewPager.offscreenPageLimit = 1 + viewModel.currentViewState().page

        binding.viewPager.registerOnPageChangeCallback(onPageChangeListener)

        player.setListener {
            shortMovieAdapter?.hidePreviewImage(viewModel.currentViewState().page)
        }
        observeViewModelEvent()
    }

    override fun onStart() {
        super.onStart()
        player.play()
    }

    override fun onStop() {
        super.onStop()
        viewModel.savePlayerState(player.currentPlayerState)
        player.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
        playerView?.detachPlayer()
        binding?.viewPager?.unregisterOnPageChangeCallback(onPageChangeListener)
        shortMovieAdapter = null
        binding = null
    }

    private fun initShortMovieAdapter() {
        val itemViewTypeProvider = object : ItemViewTypeProvider {
            override fun getLayoutRes(modelCollectionItem: CollectionItemViewModel): Int {
                return when (modelCollectionItem) {
                    is ShortMovieItemViewModel -> R.layout.player_view_item
                    else -> throw IllegalArgumentException("Unknown view type")
                }
            }
        }

        shortMovieAdapter = ShortMovieViewAdapter(viewModel.viewModels, itemViewTypeProvider) { item, itemView ->
            val itemBinding =
                DataBindingUtil.findBinding<PlayerViewItemBinding>(itemView) ?: return@ShortMovieViewAdapter
            itemBinding.previewImage.load(
                uri = item.previewImageUri,
                imageLoader = imageLoader
            )
            itemView.setOnClickListener {
                if (viewModel.currentPlayerState().isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
                viewModel.savePlayerState(player.currentPlayerState)
                item.onClickPlayToggle()
            }

            ConstraintSet().apply {
                clone(itemBinding.root)
                val ratio = item.aspectRatio?.let { "$it:1" }
                setDimensionRatio(itemBinding.playerContainer.id, ratio)
                setDimensionRatio(itemBinding.previewImage.id, ratio)
                applyTo(itemBinding.root)
            }
        }
    }

    private fun observeViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.viewModelEvent.collect { event ->
                    withContext(Dispatchers.Main) {
                        when (event) {
                            ShortMovieViewModel.Event.OnInit -> {
                                player.setUpWith(
                                    viewModel.viewModels.toList(),
                                    viewModel.currentPlayerState(),
                                )

                                player.prepare(
                                    viewModel.currentViewState().page,
                                    viewModel.currentPlayerState().seekPositionMillis
                                )

                                playerView?.attach(player)
                                shortMovieAdapter?.attach(playerView!!, viewModel.currentViewState().page)
                                binding!!.viewPager.setCurrentItem(viewModel.currentViewState().page, false)
                                player.play()
                            }
                        }
                    }
                }
            }
        }
    }
}
