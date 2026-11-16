package com.example.aplicacionvinilos.util

// Validación de forma del correo (solo front). Kotlin puro para poder probarla en JVM.
object EmailValidator {

    enum class Result { VALID, EMPTY, INVALID_FORMAT }

    private const val MAX_LENGTH = 254

    private val EMAIL_REGEX = Regex(
        "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9](?:[A-Za-z0-9\\-]*[A-Za-z0-9])?" +
            "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9\\-]*[A-Za-z0-9])?)*\\.[A-Za-z]{2,}$"
    )

    fun validate(raw: String?): Result {
        val email = raw?.trim().orEmpty()
        return when {
            email.isEmpty() -> Result.EMPTY
            email.length > MAX_LENGTH || ".." in email || !EMAIL_REGEX.matches(email) ->
                Result.INVALID_FORMAT
            else -> Result.VALID
        }
    }
}
