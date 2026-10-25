package com.example.aplicacionvinilos.data.repository

import android.util.Log
import com.example.aplicacionvinilos.data.model.Band
import com.example.aplicacionvinilos.network.ApiService
import com.example.aplicacionvinilos.network.RetrofitClient
import com.google.gson.JsonParser
import java.io.IOException
import retrofit2.HttpException

class AlbumBandRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun getAlbumBand(albumId: Int, bandId: Int): Result<Band> =
        try {
            Result.success(apiService.getAlbumBand(albumId, bandId))
        } catch (e: HttpException) {
            Result.failure(Exception(extractMessage(e)))
        } catch (e: IOException) {
            Log.e("AlbumBandRepository", "Fallo de red: ${e::class.java.name}: ${e.message}", e)
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
