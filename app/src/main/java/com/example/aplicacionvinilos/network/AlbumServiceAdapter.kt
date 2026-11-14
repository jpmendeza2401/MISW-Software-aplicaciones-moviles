package com.example.aplicacionvinilos.network

import com.example.aplicacionvinilos.data.model.Album

// Service Adapter (HU02): adapta AlbumService al contrato que espera AlbumRepository.
// El manejo de errores vive en ApiCall (compartido por todos los adapters).
class AlbumServiceAdapter(
    private val albumService: AlbumService = RetrofitClient.albumService
) {

    suspend fun getAlbum(albumId: Int): Result<Album> =
        ApiCall.execute(TAG) { albumService.getAlbum(albumId) }

    private companion object {
        const val TAG = "AlbumServiceAdapter"
    }
}
