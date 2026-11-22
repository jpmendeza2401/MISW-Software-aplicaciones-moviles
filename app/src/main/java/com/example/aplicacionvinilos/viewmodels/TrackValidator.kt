package com.example.aplicacionvinilos.viewmodels

// HU08: reglas del formulario "Agregar track". Kotlin puro para probarlo en JVM.
object TrackValidator {

    const val MIN_NAME_LENGTH = 3
    const val MIN_DURATION_SECONDS = 60

    enum class Error { INVALID_NAME, INVALID_DURATION_FORMAT, DURATION_TOO_SHORT }

    // m:ss o mm:ss, con segundos de 00 a 59.
    private val DURATION_REGEX = Regex("""^(\d{1,2}):([0-5]\d)$""")

    /** Devuelve null si el track es válido, o el primer error encontrado. */
    fun validate(name: String, duration: String): Error? {
        if (name.trim().length < MIN_NAME_LENGTH) return Error.INVALID_NAME
        val match = DURATION_REGEX.matchEntire(duration.trim()) ?: return Error.INVALID_DURATION_FORMAT
        val (minutes, seconds) = match.destructured
        return if (minutes.toInt() * 60 + seconds.toInt() < MIN_DURATION_SECONDS) {
            Error.DURATION_TOO_SHORT
        } else {
            null
        }
    }
}
