package com.example.aplicacionvinilos.data.repository

import android.util.Log
import com.example.aplicacionvinilos.BuildConfig
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.network.AlbumService
import com.example.aplicacionvinilos.network.RetrofitClient
import com.google.gson.JsonParseException
import java.io.IOException
import retrofit2.HttpException

// Misma estrucutra try/catch  que AlbumBandRepository, los errores solo se logean en consola en debug builds
class AlbumRepository(
    private val albumService: AlbumService = RetrofitClient.albumService
) {

    suspend fun getAlbum(albumId: Int): Result<Album> =
        try {
            Result.success(albumService.getAlbum(albumId))
        } catch (e: HttpException) {
            traceError("HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            traceError("Fallo de red: ${e.message}", e)
            Result.failure(e)
        } catch (e: JsonParseException) {
            traceError("Respuesta inválida: ${e.message}", e)
            Result.failure(e)
        }

    private fun traceError(message: String, e: Throwable) {
        if (BuildConfig.DEBUG) Log.e(TAG, message, e)
    }

    private companion object {
        const val TAG = "AlbumRepository"
    }
}
