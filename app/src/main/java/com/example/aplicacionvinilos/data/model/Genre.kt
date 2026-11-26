package com.example.aplicacionvinilos.data.model

// HU07: únicos géneros que acepta el backend.
enum class Genre(val apiValue: String) {
    CLASSICAL("Classical"),
    SALSA("Salsa"),
    ROCK("Rock"),
    FOLK("Folk");

    companion object {
        val labels: List<String> = entries.map { it.apiValue }
    }
}
