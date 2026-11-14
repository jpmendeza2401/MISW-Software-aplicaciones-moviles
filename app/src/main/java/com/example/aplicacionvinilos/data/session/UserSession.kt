package com.example.aplicacionvinilos.data.session

// Datos mínimos del usuario logueado. Se guardan solo estos 4 campos de la respuesta
// de GET /collectors (sin comments, favoritePerformers ni collectorAlbums).
@Suppress("PropertyName")
data class UserSession(
    val id_usuario: Int,
    val name: String,
    val telephone: String,
    val email: String
)
