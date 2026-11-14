package com.example.aplicacionvinilos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.data.repository.CollectorRepository
import com.example.aplicacionvinilos.util.EmailValidator
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Login simulado: valida la forma del correo y busca al coleccionista vía repository.
class LoginViewModel(
    private val repository: CollectorRepository = CollectorRepository()
) : ViewModel() {

    private val _state = MutableLiveData<UiState<Collector>>(UiState.Idle)
    val state: LiveData<UiState<Collector>> = _state

    private val _emailError = MutableLiveData<EmailValidator.Result?>(null)
    val emailError: LiveData<EmailValidator.Result?> = _emailError

    private var job: Job? = null

    fun login(email: String) {
        if (job?.isActive == true) return // evita doble tap / llamadas duplicadas
        val validation = EmailValidator.validate(email)
        if (validation != EmailValidator.Result.VALID) {
            _emailError.value = validation
            return
        }
        _emailError.value = null
        _state.value = UiState.Loading
        job = viewModelScope.launch {
            repository.login(email)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    // Visitante: no consulta el backend y deja la sesión vacía.
    fun enterAsGuest() = repository.logout()

    fun onEmailChanged() {
        if (_emailError.value != null) _emailError.value = null
    }

    // Tras navegar, evita que el Success se vuelva a entregar al recrear la Activity.
    fun consumeState() {
        _state.value = UiState.Idle
    }
}
