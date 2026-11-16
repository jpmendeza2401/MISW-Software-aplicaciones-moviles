package com.example.aplicacionvinilos.network

import com.example.aplicacionvinilos.data.model.Band

// Service Adapter: GET /albums/{albumId}/bands/{bandId} para AlbumBandRepository.
class AlbumBandServiceAdapter(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun getAlbumBand(albumId: Int, bandId: Int): Result<Band> =
        ApiCall.execute(TAG) { apiService.getAlbumBand(albumId, bandId) }

    private companion object {
        const val TAG = "AlbumBandServiceAdapter"
    }
}
