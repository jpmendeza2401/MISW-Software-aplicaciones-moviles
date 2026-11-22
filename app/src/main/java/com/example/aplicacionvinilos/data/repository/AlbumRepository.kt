package com.example.aplicacionvinilos.data.repository

import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.model.AlbumRequest
import com.example.aplicacionvinilos.data.model.CollectorRef
import com.example.aplicacionvinilos.data.model.Comment
import com.example.aplicacionvinilos.data.model.CommentRequest
import com.example.aplicacionvinilos.network.AlbumService
import com.example.aplicacionvinilos.network.AlbumServiceAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Unica fuente de datos de albumes para los ViewModels. Delega el acceso remoto en el adapter,
// que oculta excepciones y detalles HTTP.
class AlbumRepository(
    private val adapter: AlbumServiceAdapter = AlbumServiceAdapter(),
    private val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    constructor(
        albumService: AlbumService,
        computeDispatcher: CoroutineDispatcher = Dispatchers.Default
    ) : this(AlbumServiceAdapter(albumService), computeDispatcher)

    suspend fun getAlbum(albumId: Int): Result<Album> = adapter.getAlbum(albumId)

    // HU07: crear álbum. El armado del request corre fuera del hilo principal.
    suspend fun createAlbum(
        name: String,
        cover: String,
        releaseDate: String,
        description: String,
        genre: String,
        recordLabel: String
    ): Result<Album> {
        val request = withContext(computeDispatcher) {
            AlbumRequest(
                name.trim(), cover.trim(), releaseDate + DATE_SUFFIX,
                description.trim(), genre, recordLabel
            )
        }
        return adapter.createAlbum(request)
    }

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

    private companion object {
        // Formato de fecha que usa el backend (yyyy-MM-dd + hora cero).
        const val DATE_SUFFIX = "T00:00:00-05:00"
    }
}
