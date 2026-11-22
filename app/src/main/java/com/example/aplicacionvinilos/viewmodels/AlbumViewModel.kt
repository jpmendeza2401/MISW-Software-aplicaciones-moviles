package com.example.aplicacionvinilos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.model.Comment
import com.example.aplicacionvinilos.data.model.Track
import com.example.aplicacionvinilos.data.repository.AlbumRepository
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val repository: AlbumRepository = AlbumRepository()
) : ViewModel() {

    private val _album = MutableLiveData<UiState<Album>>(UiState.Idle)
    val album: LiveData<UiState<Album>> = _album

    // HU09: estado de la publicación del comentario (Idle → Loading → Success / Error).
    private val _commentState = MutableLiveData<UiState<Comment>>(UiState.Idle)
    val commentState: LiveData<UiState<Comment>> = _commentState

    // HU09: error de validación del formulario (null = sin error).
    private val _validationError = MutableLiveData<CommentValidator.Error?>(null)
    val validationError: LiveData<CommentValidator.Error?> = _validationError

    // HU08: estado del alta de track (Idle → Loading → Success / Error) y error de validación.
    private val _trackState = MutableLiveData<UiState<Track>>(UiState.Idle)
    val trackState: LiveData<UiState<Track>> = _trackState

    private val _trackError = MutableLiveData<TrackValidator.Error?>(null)
    val trackError: LiveData<TrackValidator.Error?> = _trackError

    fun loadAlbum(albumId: Int) {
        _album.value = UiState.Loading
        viewModelScope.launch {
            repository.getAlbum(albumId)
                .onSuccess { _album.value = UiState.Success(it) }
                .onFailure { _album.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    /**
     * HU09: valida y publica el comentario. Si el usuario no es coleccionista
     * (collectorId null) no se hace la petición.
     * Al publicar con éxito se recarga el álbum sin ocultar el contenido,
     * para que el nuevo comentario aparezca en la lista.
     */
    fun publishComment(albumId: Int, description: String, rating: Int, collectorId: Int?) {
        if (collectorId == null) return
        if (_commentState.value is UiState.Loading) return

        val error = CommentValidator.validate(description, rating)
        _validationError.value = error
        if (error != null) return

        _commentState.value = UiState.Loading
        viewModelScope.launch {
            repository.addComment(albumId, description.trim(), rating, collectorId)
                .onSuccess {
                    _commentState.value = UiState.Success(it)
                    refreshAlbum(albumId)
                }
                .onFailure { _commentState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    /** HU08: valida y agrega el track; al éxito recarga el álbum para mostrarlo en la lista. */
    fun addTrack(albumId: Int, name: String, duration: String) {
        if (_trackState.value is UiState.Loading) return // evita doble envío

        val error = TrackValidator.validate(name, duration)
        _trackError.value = error
        if (error != null) return

        _trackState.value = UiState.Loading
        viewModelScope.launch {
            repository.addTrack(albumId, name.trim(), duration.trim())
                .onSuccess {
                    _trackState.value = UiState.Success(it)
                    refreshAlbum(albumId)
                }
                .onFailure { _trackState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun clearTrackForm() {
        _trackError.value = null
        _trackState.value = UiState.Idle
    }

    fun clearCommentForm() {
        _validationError.value = null
        _commentState.value = UiState.Idle
    }

    private suspend fun refreshAlbum(albumId: Int) {
        repository.getAlbum(albumId).onSuccess { _album.value = UiState.Success(it) }
    }
}
