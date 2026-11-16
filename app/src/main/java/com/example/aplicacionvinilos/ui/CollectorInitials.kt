
package com.example.aplicacionvinilos.ui

import java.util.Locale

fun getCollectorInitials(name: String): String {
    val parts = name.trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }

    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 ->
            parts[0].take(2).uppercase(Locale.getDefault())
        else ->
            "${parts.first().first()}${parts.last().first()}"
                .uppercase(Locale.getDefault())
    }
}
