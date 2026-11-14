package com.example.aplicacionvinilos.data.repository

import com.example.aplicacionvinilos.testutil.FakeAlbumService
import com.example.aplicacionvinilos.testutil.album
import com.google.gson.JsonParseException
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

// HU02 - CA02.1 a CA02.3: el repositorio entrega el detalle o un fallo controlado.
class AlbumRepositoryTest {

    @Test
    fun `getAlbum devuelve el album solicitado`() = runTest {
        var requestedId = -1
        val expected = album(7, "Thriller")
        val repo = AlbumRepository(FakeAlbumService { requestedId = it; expected })

        val result = repo.getAlbum(7)

        assertEquals(7, requestedId)
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `HttpException se devuelve como failure con mensaje del backend`() = runTest {
        val error = HttpException(
            Response.error<Any>(
                404,
                """{"message":"Album no existe"}""".toResponseBody("application/json".toMediaType())
            )
        )
        val result = AlbumRepository(FakeAlbumService { throw error }).getAlbum(1)
        assertTrue(result.isFailure)
        assertEquals("Album no existe", result.exceptionOrNull()?.message)
    }

    @Test
    fun `IOException se devuelve como failure`() = runTest {
        val result = AlbumRepository(FakeAlbumService { throw IOException("sin red") }).getAlbum(1)
        assertEquals("Sin conexión con el servidor", result.exceptionOrNull()?.message)
    }

    @Test
    fun `JsonParseException se devuelve como failure`() = runTest {
        val result = AlbumRepository(FakeAlbumService { throw JsonParseException("malo") }).getAlbum(1)
        assertEquals("Respuesta inválida del servidor", result.exceptionOrNull()?.message)
    }

    // HU09: POST /albums/{albumId}/comments
    @Test
    fun `addComment envia descripcion, rating y collector id`() = runTest {
        val service = FakeAlbumService { album(it) }

        val result = AlbumRepository(service).addComment(5, "Muy bueno", 4, collectorId = 2)

        assertTrue(result.isSuccess)
        assertEquals(5, service.postedAlbumId)
        assertEquals("Muy bueno", service.postedComment?.description)
        assertEquals(4, service.postedComment?.rating)
        assertEquals(2, service.postedComment?.collector?.id)
    }

    @Test
    fun `addComment con error HTTP devuelve el mensaje del backend`() = runTest {
        val error = HttpException(
            Response.error<Any>(
                412,
                """{"message":"Rating must be between 1 and 5"}""".toResponseBody("application/json".toMediaType())
            )
        )
        val service = FakeAlbumService(onComment = { _, _ -> throw error }) { album(it) }

        val result = AlbumRepository(service).addComment(5, "x", 9, collectorId = 2)

        assertEquals("Rating must be between 1 and 5", result.exceptionOrNull()?.message)
    }
}
