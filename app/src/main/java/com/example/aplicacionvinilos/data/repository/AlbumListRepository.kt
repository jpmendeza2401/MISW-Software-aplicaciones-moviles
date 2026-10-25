package com.example.aplicacionvinilos.data.repository

import android.util.Log
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.network.ApiService
import com.example.aplicacionvinilos.network.RetrofitClient
import com.google.gson.JsonParser
import java.io.IOException
import retrofit2.HttpException

// HU01. Mismo patrón que AlbumBandRepository: envuelve la llamada en Result
// y traduce los errores a mensajes legibles.
class AlbumListRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun getAlbums(): Result<List<Album>> =
        try {
            Result.success(apiService.getAlbums())
        } catch (e: HttpException) {
            Result.failure(Exception(extractMessage(e)))
        } catch (e: IOException) {
            Log.e("AlbumListRepository", "Fallo de red: ${e::class.java.name}: ${e.message}", e)
            Result.failure(Exception("Sin conexión con el servidor"))
        }

    private fun extractMessage(e: HttpException): String {
        val body = e.response()?.errorBody()?.string()
        val message = runCatching {
            JsonParser.parseString(body).asJsonObject.get("message").asString
        }.getOrNull()
        return message ?: "Error ${e.code()}"
    }
}
