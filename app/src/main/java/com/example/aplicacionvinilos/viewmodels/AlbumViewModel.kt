package com.example.aplicacionvinilos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.repository.AlbumRepository
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val repository: AlbumRepository = AlbumRepository()
) : ViewModel() {

    private val _album = MutableLiveData<UiState<Album>>(UiState.Idle)
    val album: LiveData<UiState<Album>> = _album

    fun loadAlbum(albumId: Int) {
        _album.value = UiState.Loading
        viewModelScope.launch {
            repository.getAlbum(albumId)
                .onSuccess { _album.value = UiState.Success(it) }
                .onFailure { _album.value = UiState.Error(it.message.orEmpty()) }
        }
    }
}
