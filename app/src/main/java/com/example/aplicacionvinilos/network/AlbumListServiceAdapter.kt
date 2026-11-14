package com.example.aplicacionvinilos.network

import com.example.aplicacionvinilos.data.model.Album

// Service Adapter (HU01): GET /albums para AlbumListRepository.
class AlbumListServiceAdapter(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun getAlbums(): Result<List<Album>> =
        ApiCall.execute(TAG) { apiService.getAlbums() }

    private companion object {
        const val TAG = "AlbumListServiceAdapter"
    }
}
