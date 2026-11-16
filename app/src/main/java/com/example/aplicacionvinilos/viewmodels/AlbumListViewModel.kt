package com.example.aplicacionvinilos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.repository.AlbumListRepository
import kotlinx.coroutines.launch

class AlbumListViewModel(
    private val repository: AlbumListRepository = AlbumListRepository()
) : ViewModel() {

    private val _albums = MutableLiveData<UiState<List<Album>>>(UiState.Idle)
    val albums: LiveData<UiState<List<Album>>> = _albums

    init {
        loadAlbums()
    }

    fun loadAlbums() {
        _albums.value = UiState.Loading
        viewModelScope.launch {
            repository.getAlbums()
                .onSuccess { _albums.value = UiState.Success(it) }
                .onFailure { _albums.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }
}
