package com.example.aplicacionvinilos.viewmodels

import androidx.lifecycle.ViewModel
import com.example.aplicacionvinilos.data.repository.CollectorRepository

// Cierre de sesión para cualquier pantalla: la UI no toca CollectorSession directamente.
class SessionViewModel(
    private val repository: CollectorRepository = CollectorRepository()
) : ViewModel() {

    fun logout() = repository.logout()
}
