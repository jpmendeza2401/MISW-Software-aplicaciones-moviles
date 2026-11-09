package com.example.aplicacionvinilos.network

import android.util.Log
import com.example.aplicacionvinilos.BuildConfig
import com.example.aplicacionvinilos.data.model.Collector
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import java.io.IOException
import retrofit2.HttpException

// Service Adapter (HU05 y HU06): adapta la interfaz Retrofit (ApiService) al contrato que espera
// CollectorRepository. Es la única clase que sabe que por debajo hay Retrofit y HTTP:
// traduce excepciones de red, HTTP y parseo a Result con mensajes legibles para la UI.
// Mismo patrón que AlbumServiceAdapter.
class CollectorServiceAdapter(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    // HU05: GET /collectors
    suspend fun getCollectors(): Result<List<Collector>> =
        call(notFoundMessage = "No se encontró el listado de coleccionistas") {
            apiService.getCollectors()
        }

    // HU06: GET /collectors/{collectorId}
    suspend fun getCollector(collectorId: Int): Result<Collector> =
        call(notFoundMessage = "El coleccionista no existe") {
            apiService.getCollector(collectorId)
        }

    // Un solo lugar con el try/catch: cada endpoint nuevo es una línea más arriba.
    // notFoundMessage: el texto que verá el usuario si el backend responde 404 en ESTE endpoint.
    private suspend fun <T> call(
        notFoundMessage: String,
        request: suspend () -> T
    ): Result<T> =
        try {
            Result.success(request())
        } catch (e: HttpException) {
            traceError("HTTP ${e.code()}", e)
            Result.failure(Exception(extractMessage(e, notFoundMessage)))
        } catch (e: IOException) {
            traceError("Fallo de red: ${e.message}", e)
            Result.failure(Exception("Sin conexión con el servidor"))
        } catch (e: JsonParseException) {
            traceError("Respuesta inválida: ${e.message}", e)
            Result.failure(Exception("Respuesta inválida del servidor"))
        }

    // El backend responde 404 en inglés; lo traducimos según el endpoint que falló.
    private fun extractMessage(e: HttpException, notFoundMessage: String): String {
        if (e.code() == HTTP_NOT_FOUND) return notFoundMessage
        val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
        val message = runCatching {
            JsonParser.parseString(body).asJsonObject.get("message").asString
        }.getOrNull()
        return message ?: "Error ${e.code()}"
    }

    private fun traceError(message: String, e: Throwable) {
        if (BuildConfig.DEBUG) Log.e(TAG, message, e)
    }

    private companion object {
        const val TAG = "CollectorServiceAdapter"
        const val HTTP_NOT_FOUND = 404
    }
}
