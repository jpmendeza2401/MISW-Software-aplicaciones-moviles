package com.example.aplicacionvinilos.viewmodels

import com.example.aplicacionvinilos.data.model.Genre
import com.example.aplicacionvinilos.data.model.RecordLabel

// HU07 y HU11: reglas del formulario "Crear álbum". Kotlin puro para probarlo en JVM.
object AlbumValidator {

    const val MIN_NAME_LENGTH = 3

    enum class Error {
        INVALID_NAME, INVALID_COVER, INVALID_DATE, EMPTY_DESCRIPTION, INVALID_GENRE, INVALID_LABEL
    }

    private val URL_REGEX = Regex("""^https?://[^\s/${'$'}.?#][^\s]*$""", RegexOption.IGNORE_CASE)
    private val DATE_REGEX = Regex("""^(\d{4})-(\d{2})-(\d{2})$""")
    private val DAYS_IN_MONTH = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    /** Devuelve null si el formulario es válido, o el primer error encontrado. */
    fun validate(
        name: String,
        cover: String,
        releaseDate: String,
        description: String,
        genre: String,
        recordLabel: String
    ): Error? = when {
        name.trim().length < MIN_NAME_LENGTH -> Error.INVALID_NAME
        !isValidCover(cover) -> Error.INVALID_COVER
        !isValidDate(releaseDate) -> Error.INVALID_DATE
        description.isBlank() -> Error.EMPTY_DESCRIPTION
        Genre.labels.none { it == genre } -> Error.INVALID_GENRE
        RecordLabel.labels.none { it == recordLabel } -> Error.INVALID_LABEL
        else -> null
    }

    fun isValidCover(value: String): Boolean = URL_REGEX.matches(value.trim())

    fun isValidDate(value: String): Boolean {
        val m = DATE_REGEX.matchEntire(value) ?: return false
        val (year, month, day) = m.destructured
        val y = year.toInt()
        val mo = month.toInt()
        if (mo !in 1..12) return false
        val leap = (y % 4 == 0 && y % 100 != 0) || y % 400 == 0
        val max = if (mo == 2 && leap) 29 else DAYS_IN_MONTH[mo - 1]
        return day.toInt() in 1..max
    }
}
