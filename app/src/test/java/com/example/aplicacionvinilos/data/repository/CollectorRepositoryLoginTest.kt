package com.example.aplicacionvinilos.data.repository

import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.model.Band
import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.data.session.CollectorSession
import com.example.aplicacionvinilos.network.ApiService
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CollectorRepositoryLoginTest {

    private fun collector(id: Int, email: String) = Collector(id, "n$id", "123", email)

    private fun repo(vararg items: Collector) = CollectorRepository(object : ApiService {
        override suspend fun getAlbumBand(albumId: Int, bandId: Int): Band = error("no")
        override suspend fun getAlbums(): List<Album> = error("no")
        override suspend fun getCollectors(): List<Collector> = items.toList()
        override suspend fun getCollector(collectorId: Int): Collector = error("no")
    })

    @After
    fun tearDown() = CollectorSession.clear()

    @Test
    fun `con correo repetido gana el id mas grande y queda en sesion`() = runTest {
        val result = repo(
            collector(1, "x@y.com"), collector(7, "X@Y.com"), collector(3, "x@y.com")
        ).login(" x@y.com ")
        assertEquals(7, result.getOrThrow().id)
        assertEquals(7, CollectorSession.current?.id_usuario)
    }

    @Test
    fun `correo inexistente falla y no abre sesion`() = runTest {
        val result = repo(collector(1, "a@b.com")).login("z@b.com")
        assertTrue(result.isFailure)
        assertEquals(CollectorRepository.MSG_EMAIL_NOT_FOUND, result.exceptionOrNull()?.message)
        assertNull(CollectorSession.current)
    }
}
