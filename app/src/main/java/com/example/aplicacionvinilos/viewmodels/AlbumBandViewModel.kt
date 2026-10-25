package com.example.aplicacionvinilos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionvinilos.data.model.Band
import com.example.aplicacionvinilos.data.repository.AlbumBandRepository
import kotlinx.coroutines.launch

class AlbumBandViewModel(
    private val repository: AlbumBandRepository = AlbumBandRepository()
) : ViewModel() {

    private val _band = MutableLiveData<UiState<Band>>(UiState.Idle)
    val band: LiveData<UiState<Band>> = _band

    fun loadBand(albumId: Int, bandId: Int) {
        _band.value = UiState.Loading
        viewModelScope.launch {
            repository.getAlbumBand(albumId, bandId)
                .onSuccess { _band.value = UiState.Success(it) }
                .onFailure { _band.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }
}
