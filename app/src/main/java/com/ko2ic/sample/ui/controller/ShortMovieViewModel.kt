package com.ko2ic.sample.ui.controller

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ko2ic.sample.model.PlayerState
import com.ko2ic.sample.model.ViewState
import com.ko2ic.sample.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ShortMovieViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    repository: Repository,
) : ViewModel() {

    companion object {

        const val KEY_PLAYER_STATE = "player_state"
        const val KEY_VIEW_STATE = "view_state"
    }

    val viewModels = ObservableArrayList<ShortMovieItemViewModel>()

    sealed class Event {
        data object OnInit : Event()
    }

    private val _viewModelEvent =
        MutableSharedFlow<Event>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val viewModelEvent: SharedFlow<Event>
        get() = _viewModelEvent.asSharedFlow()

    init {
        repository.fetch().onEach { data ->

            data.map {
                ShortMovieItemViewModel(
                    it.id,
                    it.mediaUri,
                    it.previewImageUri,
                    it.aspectRatio
                )
            }.let {
                viewModels.clear()
                viewModels.addAll(it)
            }
            _viewModelEvent.tryEmit(Event.OnInit)
        }.launchIn(viewModelScope)
    }

    fun currentViewState(): ViewState {
        return savedState.get<ViewState>(KEY_VIEW_STATE) ?: ViewState()
    }

    fun updateCurrentPage(page: Int) {
        currentViewState().copy(page = page).let {
            savedState[KEY_VIEW_STATE] = it
        }
    }

    fun savePlayerState(newState: PlayerState) {
        savedState[KEY_PLAYER_STATE] = newState
    }

    fun currentPlayerState(): PlayerState {
        return savedState.get<PlayerState>(KEY_PLAYER_STATE) ?: PlayerState.INITIAL
    }
}