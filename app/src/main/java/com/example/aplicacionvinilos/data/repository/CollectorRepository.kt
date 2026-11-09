package com.example.aplicacionvinilos.data.repository

import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.network.ApiService
import com.example.aplicacionvinilos.network.CollectorServiceAdapter

// Repository (HU05 y HU06): única fuente de datos de coleccionistas para los ViewModels.
// Ya no conoce Retrofit ni HttpException: delega el acceso remoto en el Service Adapter.
// Flujo: Activity → ViewModel → CollectorRepository → CollectorServiceAdapter → ApiService → API REST
class CollectorRepository(
    private val adapter: CollectorServiceAdapter = CollectorServiceAdapter()
) {

    // Permite inyectar un ApiService falso en pruebas, igual que AlbumRepository.
    constructor(apiService: ApiService) : this(CollectorServiceAdapter(apiService))

    suspend fun getCollectors(): Result<List<Collector>> = adapter.getCollectors()

    suspend fun getCollector(collectorId: Int): Result<Collector> =
        adapter.getCollector(collectorId)
}
