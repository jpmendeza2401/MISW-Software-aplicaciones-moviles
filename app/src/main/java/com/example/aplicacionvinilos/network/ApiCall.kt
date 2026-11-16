package com.example.aplicacionvinilos.network

import android.util.Log
import com.example.aplicacionvinilos.BuildConfig
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import java.io.IOException
import retrofit2.HttpException

// Único lugar con el try/catch de red: todos los Service Adapters delegan aquí.
// Traduce excepciones HTTP, de red y de parseo a Result con mensajes legibles para la UI.
// tag: nombre del adapter para el log (solo en debug).
// notFoundMessage: texto propio si el backend responde 404 en ESE endpoint (null = usa el
// "message" del cuerpo de error, o "Error <código>").
internal object ApiCall {

    private const val HTTP_NOT_FOUND = 404

    suspend fun <T> execute(
        tag: String,
        notFoundMessage: String? = null,
        request: suspend () -> T
    ): Result<T> =
        try {
            Result.success(request())
        } catch (e: HttpException) {
            trace(tag, "HTTP ${e.code()}", e)
            Result.failure(Exception(extractMessage(e, notFoundMessage)))
        } catch (e: IOException) {
            trace(tag, "Fallo de red: ${e.message}", e)
            Result.failure(Exception("Sin conexión con el servidor"))
        } catch (e: JsonParseException) {
            trace(tag, "Respuesta inválida: ${e.message}", e)
            Result.failure(Exception("Respuesta inválida del servidor"))
        }

    private fun extractMessage(e: HttpException, notFoundMessage: String?): String {
        if (e.code() == HTTP_NOT_FOUND && notFoundMessage != null) return notFoundMessage
        val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
        val message = runCatching {
            JsonParser.parseString(body).asJsonObject.get("message").asString
        }.getOrNull()
        return message ?: "Error ${e.code()}"
    }

    private fun trace(tag: String, message: String, e: Throwable) {
        if (BuildConfig.DEBUG) Log.e(tag, message, e)
    }
}
