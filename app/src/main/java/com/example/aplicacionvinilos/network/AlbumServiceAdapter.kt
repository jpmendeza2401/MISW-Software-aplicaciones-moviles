package com.example.aplicacionvinilos.network

import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.model.Comment
import com.example.aplicacionvinilos.data.model.CommentRequest
import com.example.aplicacionvinilos.data.model.Track
import com.example.aplicacionvinilos.data.model.TrackRequest

// Service Adapter (HU02, HU08 y HU09): adapta AlbumService al contrato que espera AlbumRepository.
// El manejo de errores vive en ApiCall (compartido por todos los adapters).
class AlbumServiceAdapter(
    private val albumService: AlbumService = RetrofitClient.albumService
) {

    suspend fun getAlbum(albumId: Int): Result<Album> =
        ApiCall.execute(TAG) { albumService.getAlbum(albumId) }

    // HU09: POST /albums/{albumId}/comments. Devuelve el comentario creado.
    suspend fun addComment(albumId: Int, comment: CommentRequest): Result<Comment> =
        ApiCall.execute(TAG) { albumService.addComment(albumId, comment) }

    // HU08: POST /albums/{albumId}/tracks. Devuelve el track creado.
    suspend fun addTrack(albumId: Int, track: TrackRequest): Result<Track> =
        ApiCall.execute(TAG) { albumService.addTrack(albumId, track) }

    private companion object {
        const val TAG = "AlbumServiceAdapter"
    }
}
