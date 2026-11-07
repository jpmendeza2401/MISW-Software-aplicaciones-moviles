package com.example.aplicacionvinilos.data.repository

import android.util.Log
import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.network.ApiService
import com.example.aplicacionvinilos.network.RetrofitClient
import com.google.gson.JsonParser
import java.io.IOException
import retrofit2.HttpException

// HU05: consultar listado de coleccionistas.
// Envuelve la respuesta en Result y maneja errores HTTP y de red.
class CollectorRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun getCollectors(): Result<List<Collector>> =
        try {
            Result.success(apiService.getCollectors())
        } catch (e: HttpException) {
            Result.failure(Exception(extractMessage(e)))
        } catch (e: IOException) {
            Log.e(
                "CollectorRepository",
                "Fallo de red: ${e.message}",
                e
            )
            Result.failure(Exception("Sin conexión con el servidor"))
        }

    private fun extractMessage(e: HttpException): String {
        val body = e.response()?.errorBody()?.string()
        val message = runCatching {
            JsonParser.parseString(body)
                .asJsonObject
                .get("message")
                .asString
        }.getOrNull()

        return message ?: "Error ${e.code()}"
    }
    suspend fun getCollector(collectorId: Int): Result<Collector> {
        return try {
            Result.success(apiService.getCollector(collectorId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
