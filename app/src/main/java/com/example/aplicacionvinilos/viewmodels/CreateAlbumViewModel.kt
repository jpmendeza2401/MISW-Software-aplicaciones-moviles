package com.example.aplicacionvinilos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.repository.AlbumRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// HU07: valida y crea un álbum (Idle → Loading → Success / Error).
class CreateAlbumViewModel(
    private val repository: AlbumRepository = AlbumRepository()
) : ViewModel() {

    private val _state = MutableLiveData<UiState<Album>>(UiState.Idle)
    val state: LiveData<UiState<Album>> = _state

    private val _validationError = MutableLiveData<AlbumValidator.Error?>(null)
    val validationError: LiveData<AlbumValidator.Error?> = _validationError

    private var job: Job? = null

    fun submit(
        name: String,
        cover: String,
        releaseDate: String,
        description: String,
        genre: String,
        recordLabel: String
    ) {
        if (job?.isActive == true) return // evita doble envío
        val error = AlbumValidator.validate(name, cover, releaseDate, description, genre, recordLabel)
        _validationError.value = error
        if (error != null) return

        _state.value = UiState.Loading
        job = viewModelScope.launch {
            repository.createAlbum(name, cover, releaseDate, description, genre, recordLabel)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun onFormChanged() {
        if (_validationError.value != null) _validationError.value = null
    }

    // Tras cerrar la pantalla, evita que el Success se vuelva a entregar al recrear la Activity.
    fun consumeState() {
        _state.value = UiState.Idle
    }
}
