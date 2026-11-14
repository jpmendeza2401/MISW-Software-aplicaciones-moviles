package com.example.aplicacionvinilos.data.session

import com.example.aplicacionvinilos.data.model.Collector

// Sesión simulada: el backend no tiene endpoint de login, así que el usuario
// encontrado por correo se guarda en memoria y queda disponible en toda la app:
//   CollectorSession.current?.id_usuario
//   CollectorSession.current?.name / telephone / email
// Solo guarda 4 campos (UserSession), sin Context ni Views: no hay fuga de memoria.
object CollectorSession {

    @Volatile
    var current: UserSession? = null
        private set

    val isLoggedIn: Boolean get() = current != null

    fun start(collector: Collector) {
        current = UserSession(
            id_usuario = collector.id,
            name = collector.name,
            telephone = collector.telephone,
            email = collector.email
        )
    }

    fun clear() {
        current = null
    }
}
