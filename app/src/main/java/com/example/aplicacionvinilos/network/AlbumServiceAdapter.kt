package com.example.aplicacionvinilos.network

import android.util.Log
import com.example.aplicacionvinilos.BuildConfig
import com.example.aplicacionvinilos.data.model.Album
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import java.io.IOException
import retrofit2.HttpException

// Service Adapter: adapta la interfaz Retrofit (AlbumService) al contrato que espera el repositorio.
// Traduce excepciones de red/HTTP/parseo a Result con mensajes de dominio; los detalles tecnicos
// solo se registran en log en debug builds.
class AlbumServiceAdapter(
    private val albumService: AlbumService = RetrofitClient.albumService
) {

    suspend fun getAlbum(albumId: Int): Result<Album> =
        try {
            Result.success(albumService.getAlbum(albumId))
        } catch (e: HttpException) {
            traceError("HTTP ${e.code()}", e)
            Result.failure(Exception(extractMessage(e)))
        } catch (e: IOException) {
            traceError("Fallo de red: ${e.message}", e)
            Result.failure(Exception("Sin conexión con el servidor"))
        } catch (e: JsonParseException) {
            traceError("Respuesta inválida: ${e.message}", e)
            Result.failure(Exception("Respuesta inválida del servidor"))
        }

    private fun extractMessage(e: HttpException): String {
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
        const val TAG = "AlbumServiceAdapter"
    }
}
