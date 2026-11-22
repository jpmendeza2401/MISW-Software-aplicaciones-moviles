package com.example.aplicacionvinilos.data.repository

import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.data.model.CollectorAlbum
import com.example.aplicacionvinilos.data.model.CollectorAlbumRequest
import com.example.aplicacionvinilos.data.session.CollectorSession
import com.example.aplicacionvinilos.network.ApiService
import com.example.aplicacionvinilos.network.CollectorServiceAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repository (HU05 y HU06): única fuente de datos de coleccionistas para los ViewModels.
// Ya no conoce Retrofit ni HttpException: delega el acceso remoto en el Service Adapter.
// Flujo: Activity → ViewModel → CollectorRepository → CollectorServiceAdapter → ApiService → API REST
class CollectorRepository(
    private val adapter: CollectorServiceAdapter = CollectorServiceAdapter(),
    private val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    // Permite inyectar un ApiService falso en pruebas, igual que AlbumRepository.
    constructor(apiService: ApiService) : this(CollectorServiceAdapter(apiService))

    suspend fun getCollectors(): Result<List<Collector>> = adapter.getCollectors()

    suspend fun getCollector(collectorId: Int): Result<Collector> =
        adapter.getCollector(collectorId)

    // HU11: asociar un álbum al coleccionista con su precio y estado.
    suspend fun addAlbumToCollector(
        collectorId: Int,
        albumId: Int,
        price: Int,
        status: String
    ): Result<CollectorAlbum> =
        adapter.addAlbumToCollector(collectorId, albumId, CollectorAlbumRequest(price, status))

    // Login simulado: no existe endpoint, se busca el correo en GET /collectors.
    // Desempate: si hay varios con el mismo correo, gana el de id más grande.
    // La búsqueda corre en Dispatchers.Default para no bloquear el hilo principal (ANR)
    // si la lista es grande; se recorre una sola vez, sin copias intermedias.
    suspend fun login(email: String): Result<Collector> =
        adapter.getCollectors().mapCatching { collectors ->
            val target = email.trim()
            val match = withContext(computeDispatcher) {
                var best: Collector? = null
                for (c in collectors) {
                    if (c.email.trim().equals(target, ignoreCase = true) &&
                        (best == null || c.id > best.id)
                    ) best = c
                }
                best
            } ?: throw NoSuchElementException(MSG_EMAIL_NOT_FOUND)
            CollectorSession.start(match)
            match
        }

    fun logout() = CollectorSession.clear()

    companion object {
        const val MSG_EMAIL_NOT_FOUND = "No existe un usuario con ese correo"
    }
}
