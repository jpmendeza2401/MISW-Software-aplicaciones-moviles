package com.example.aplicacionvinilos.data.repository

import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.model.CollectorRef
import com.example.aplicacionvinilos.data.model.Comment
import com.example.aplicacionvinilos.data.model.CommentRequest
import com.example.aplicacionvinilos.data.model.Track
import com.example.aplicacionvinilos.data.model.TrackRequest
import com.example.aplicacionvinilos.network.AlbumService
import com.example.aplicacionvinilos.network.AlbumServiceAdapter

// Unica fuente de datos de albumes para los ViewModels. Delega el acceso remoto en el adapter,
// que oculta excepciones y detalles HTTP.
class AlbumRepository(
    private val adapter: AlbumServiceAdapter = AlbumServiceAdapter()
) {

    constructor(albumService: AlbumService) : this(AlbumServiceAdapter(albumService))

    suspend fun getAlbum(albumId: Int): Result<Album> = adapter.getAlbum(albumId)

    // HU08: asociar un track al álbum.
    suspend fun addTrack(albumId: Int, name: String, duration: String): Result<Track> =
        adapter.addTrack(albumId, TrackRequest(name, duration))

    // HU09: publicar comentario en nombre del coleccionista que inició sesión.
    suspend fun addComment(
        albumId: Int,
        description: String,
        rating: Int,
        collectorId: Int
    ): Result<Comment> = adapter.addComment(
        albumId,
        CommentRequest(description, rating, CollectorRef(collectorId))
    )
}
