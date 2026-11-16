package com.example.aplicacionvinilos.viewmodels;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.data.repository.CollectorRepository
import kotlinx.coroutines.launch

// HU05: gestiona la consulta del listado de coleccionistas.
public class CollectorViewModel(
    private val repository: CollectorRepository = CollectorRepository()
) : ViewModel() {

    private val _collectors =
        MutableLiveData<UiState<List<Collector>>>(UiState.Idle)

    val collectors: LiveData<UiState<List<Collector>>> = _collectors

    init {
        loadCollectors()
    }

    fun loadCollectors() {
        _collectors.value = UiState.Loading

        viewModelScope.launch {
            repository.getCollectors()
                .onSuccess {
                    _collectors.value = UiState.Success(it)
                }
                .onFailure {
                    _collectors.value = UiState.Error(
                        it.message ?: "Error desconocido"
                    )
                }
        }
    }
}
