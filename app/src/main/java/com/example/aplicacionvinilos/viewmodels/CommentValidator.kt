package com.example.aplicacionvinilos.viewmodels

// HU09: reglas del formulario "Publicar comentario".
object CommentValidator {

    const val MIN_RATING = 1
    const val MAX_RATING = 5
    const val MAX_DESCRIPTION_LENGTH = 600

    enum class Error { EMPTY_DESCRIPTION, DESCRIPTION_TOO_LONG, INVALID_RATING }

    /** Devuelve null si el comentario es válido, o el primer error encontrado. */
    fun validate(description: String, rating: Int): Error? = when {
        description.isBlank() -> Error.EMPTY_DESCRIPTION
        description.trim().length > MAX_DESCRIPTION_LENGTH -> Error.DESCRIPTION_TOO_LONG
        rating !in MIN_RATING..MAX_RATING -> Error.INVALID_RATING
        else -> null
    }
}
