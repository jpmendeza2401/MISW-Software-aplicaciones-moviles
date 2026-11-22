package com.example.aplicacionvinilos.data.model

// HU11: estados con los que un coleccionista ofrece un álbum.
enum class AlbumStatus(val apiValue: String) {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    companion object {
        val labels: List<String> = entries.map { it.apiValue }
    }
}
