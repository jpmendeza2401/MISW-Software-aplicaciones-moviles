package com.example.aplicacionvinilos.data.repository

import com.example.aplicacionvinilos.testutil.FakeApiService
import com.example.aplicacionvinilos.testutil.album
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

// HU01 - CA01.1: el repositorio entrega el listado o un error legible.
class AlbumListRepositoryTest {

    private fun httpError(code: Int, body: String) = HttpException(
        Response.error<Any>(code, body.toResponseBody("application/json".toMediaType()))
    )

    @Test
    fun `getAlbums devuelve la lista del servicio`() = runTest {
        val albums = listOf(album(1), album(2, "Thriller"))
        val result = AlbumListRepository(FakeApiService { albums }).getAlbums()
        assertTrue(result.isSuccess)
        assertEquals(albums, result.getOrNull())
    }

    @Test
    fun `getAlbums devuelve lista vacia sin error`() = runTest {
        val result = AlbumListRepository(FakeApiService { emptyList() }).getAlbums()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `HttpException con message en el cuerpo usa ese mensaje`() = runTest {
        val repo = AlbumListRepository(
            FakeApiService { throw httpError(500, """{"message":"Falla interna"}""") }
        )
        assertEquals("Falla interna", repo.getAlbums().exceptionOrNull()?.message)
    }

    @Test
    fun `HttpException sin message usa el codigo HTTP`() = runTest {
        val repo = AlbumListRepository(FakeApiService { throw httpError(404, "no es json") })
        assertEquals("Error 404", repo.getAlbums().exceptionOrNull()?.message)
    }

    @Test
    fun `IOException se traduce a sin conexion`() = runTest {
        val repo = AlbumListRepository(FakeApiService { throw IOException("timeout") })
        assertEquals("Sin conexión con el servidor", repo.getAlbums().exceptionOrNull()?.message)
    }
}
