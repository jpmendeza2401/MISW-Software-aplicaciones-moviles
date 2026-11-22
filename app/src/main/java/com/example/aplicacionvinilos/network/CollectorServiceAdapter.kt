package com.example.aplicacionvinilos.network

import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.data.model.CollectorAlbum
import com.example.aplicacionvinilos.data.model.CollectorAlbumRequest

// Service Adapter (HU05, HU06 y HU11): adapta la interfaz Retrofit (ApiService) al contrato que espera
// CollectorRepository. El manejo de errores vive en ApiCall (compartido por todos los adapters).
class CollectorServiceAdapter(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    // HU05: GET /collectors
    suspend fun getCollectors(): Result<List<Collector>> =
        ApiCall.execute(TAG, notFoundMessage = "No se encontró el listado de coleccionistas") {
            apiService.getCollectors()
        }

    // HU06: GET /collectors/{collectorId}
    suspend fun getCollector(collectorId: Int): Result<Collector> =
        ApiCall.execute(TAG, notFoundMessage = "El coleccionista no existe") {
            apiService.getCollector(collectorId)
        }

    // HU11: POST /collectors/{collectorId}/albums/{albumId}
    suspend fun addAlbumToCollector(
        collectorId: Int,
        albumId: Int,
        body: CollectorAlbumRequest
    ): Result<CollectorAlbum> =
        ApiCall.execute(TAG, notFoundMessage = "El coleccionista o el álbum no existe") {
            apiService.addAlbumToCollector(collectorId, albumId, body)
        }

    private companion object {
        const val TAG = "CollectorServiceAdapter"
    }
}
