package com.example.aplicacionvinilos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.repository.AlbumRepository
import com.example.aplicacionvinilos.data.repository.CollectorRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// HU07 y HU11: valida y crea un álbum y, si se pide, lo asocia al coleccionista.
// Estado: Idle → Loading → Success / Error.
class CreateAlbumViewModel(
    private val albums: AlbumRepository = AlbumRepository(),
    private val collectors: CollectorRepository = CollectorRepository()
) : ViewModel() {

    // HU11: datos para asociar el álbum creado al coleccionista en sesión.
    data class Association(val collectorId: Int, val price: String, val status: String)

    private val _state = MutableLiveData<UiState<Album>>(UiState.Idle)
    val state: LiveData<UiState<Album>> = _state

    private val _validationError = MutableLiveData<AlbumValidator.Error?>(null)
    val validationError: LiveData<AlbumValidator.Error?> = _validationError

    private var job: Job? = null

    // Álbum ya creado: si la asociación falla, reintentar no vuelve a crearlo (evita duplicados).
    private var createdAlbum: Album? = null

    fun submit(
        name: String,
        cover: String,
        releaseDate: String,
        description: String,
        genre: String,
        recordLabel: String,
        association: Association? = null
    ) {
        if (job?.isActive == true) return // evita doble envío
        val albumError = if (createdAlbum != null) null else {
            AlbumValidator.validate(name, cover, releaseDate, description, genre, recordLabel)
        }
        val error = albumError
            ?: association?.let { AlbumValidator.validateAssociation(it.price, it.status) }
        _validationError.value = error
        if (error != null) return

        _state.value = UiState.Loading
        job = viewModelScope.launch {
            val album = createdAlbum ?: albums
                .createAlbum(name, cover, releaseDate, description, genre, recordLabel)
                .getOrElse {
                    _state.value = UiState.Error(it.message ?: "Error desconocido")
                    return@launch
                }
                .also { createdAlbum = it }

            if (association == null) {
                _state.value = UiState.Success(album)
                return@launch
            }
            collectors.addAlbumToCollector(
                association.collectorId, album.id, association.price.trim().toInt(), association.status
            )
                .onSuccess { _state.value = UiState.Success(album) }
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
