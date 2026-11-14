package com.example.aplicacionvinilos.data.repository

import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.network.AlbumListServiceAdapter
import com.example.aplicacionvinilos.network.ApiService

// HU01. Única fuente de datos del catálogo de álbumes; delega el acceso remoto en el adapter.
class AlbumListRepository(
    private val adapter: AlbumListServiceAdapter = AlbumListServiceAdapter()
) {

    // Permite inyectar un ApiService falso en pruebas.
    constructor(apiService: ApiService) : this(AlbumListServiceAdapter(apiService))

    suspend fun getAlbums(): Result<List<Album>> = adapter.getAlbums()
}
