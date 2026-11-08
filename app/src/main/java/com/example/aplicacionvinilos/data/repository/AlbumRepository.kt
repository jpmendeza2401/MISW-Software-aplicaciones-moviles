package com.example.aplicacionvinilos.data.repository

import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.network.AlbumService
import com.example.aplicacionvinilos.network.AlbumServiceAdapter

// Unica fuente de datos de albumes para los ViewModels. Delega el acceso remoto en el adapter,
// que oculta excepciones y detalles HTTP.
class AlbumRepository(
    private val adapter: AlbumServiceAdapter = AlbumServiceAdapter()
) {

    constructor(albumService: AlbumService) : this(AlbumServiceAdapter(albumService))

    suspend fun getAlbum(albumId: Int): Result<Album> = adapter.getAlbum(albumId)
}
