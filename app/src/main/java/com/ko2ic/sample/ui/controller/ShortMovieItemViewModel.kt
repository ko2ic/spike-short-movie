package com.ko2ic.sample.ui.controller

import androidx.databinding.ObservableBoolean
import com.ko2ic.sample.ui.common.CollectionItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShortMovieItemViewModel(
    val id: String,
    val mediaUri: String,
    val previewImageUri: String,
    val aspectRatio: Float? = null
) : CollectionItemViewModel {

    val isCheckedPlayToggle = ObservableBoolean(false)
    val showPlayToggle = ObservableBoolean(false)

    fun onClickPlayToggle() {
        isCheckedPlayToggle.set(!isCheckedPlayToggle.get())
        GlobalScope.launch(Dispatchers.IO) {
            showPlayToggle.set(true)
            delay(3000)
            showPlayToggle.set(false)
        }
    }
}