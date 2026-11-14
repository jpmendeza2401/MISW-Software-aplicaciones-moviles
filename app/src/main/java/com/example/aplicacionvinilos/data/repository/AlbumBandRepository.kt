package com.example.aplicacionvinilos.data.repository

import com.example.aplicacionvinilos.data.model.Band
import com.example.aplicacionvinilos.network.AlbumBandServiceAdapter
import com.example.aplicacionvinilos.network.ApiService

// Única fuente de datos de bandas por álbum; delega el acceso remoto en el adapter.
class AlbumBandRepository(
    private val adapter: AlbumBandServiceAdapter = AlbumBandServiceAdapter()
) {

    // Permite inyectar un ApiService falso en pruebas.
    constructor(apiService: ApiService) : this(AlbumBandServiceAdapter(apiService))

    suspend fun getAlbumBand(albumId: Int, bandId: Int): Result<Band> =
        adapter.getAlbumBand(albumId, bandId)
}
