package com.example.aplicacionvinilos.data.model

// HU07: únicos sellos discográficos que acepta el backend.
enum class RecordLabel(val apiValue: String) {
    SONY_MUSIC("Sony Music"),
    EMI("EMI"),
    DISCOS_FUENTES("Discos Fuentes"),
    ELEKTRA("Elektra"),
    FANIA_RECORDS("Fania Records");

    companion object {
        val labels: List<String> = entries.map { it.apiValue }
    }
}
