
package com.example.aplicacionvinilos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.data.repository.CollectorRepository
import kotlinx.coroutines.launch

class CollectorDetailViewModel(
    private val repository: CollectorRepository = CollectorRepository()
) : ViewModel() {

    private val _collector =
        MutableLiveData<UiState<Collector>>(UiState.Idle)

    val collector: LiveData<UiState<Collector>> = _collector

    fun loadCollector(collectorId: Int) {
        if (collectorId <= 0) {
            _collector.value = UiState.Error("Identificador de coleccionista inválido")
            return
        }

        _collector.value = UiState.Loading

        viewModelScope.launch {
            repository.getCollector(collectorId)
                .onSuccess {
                    _collector.value = UiState.Success(it)
                }
                .onFailure {
                    _collector.value = UiState.Error(
                        it.message ?: "No fue posible cargar el coleccionista"
                    )
                }
        }
    }
}
