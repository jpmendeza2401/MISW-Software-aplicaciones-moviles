package com.example.aplicacionvinilos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aplicacionvinilos.data.model.Comment
import com.example.aplicacionvinilos.data.repository.AlbumRepository
import com.example.aplicacionvinilos.testutil.FakeAlbumService
import com.example.aplicacionvinilos.testutil.MainDispatcherRule
import com.example.aplicacionvinilos.testutil.album
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// HU09: publicar comentario desde el detalle del álbum.
class AlbumViewModelCommentTest {

    @get:Rule val instantRule = InstantTaskExecutorRule()
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `publicar comentario valido envia el cuerpo esperado y expone Success`() {
        val service = FakeAlbumService { album(it) }
        val vm = AlbumViewModel(AlbumRepository(service))

        vm.publishComment(albumId = 100, description = "  It is an amazing album  ", rating = 4, collectorId = 7)

        assertEquals(100, service.postedAlbumId)
        assertEquals("It is an amazing album", service.postedComment?.description)
        assertEquals(4, service.postedComment?.rating)
        assertEquals(7, service.postedComment?.collector?.id)
        assertTrue(vm.commentState.value is UiState.Success)
        assertNull(vm.validationError.value)
    }

    @Test
    fun `al publicar con exito se recarga el album con el nuevo comentario`() {
        val updated = album(100, comments = listOf(Comment(1, "Nuevo", 5)))
        val vm = AlbumViewModel(AlbumRepository(FakeAlbumService { updated }))

        vm.publishComment(100, "Nuevo", 5, collectorId = 1)

        assertEquals(updated, (vm.album.value as UiState.Success).data)
    }

    @Test
    fun `sin coleccionista en sesion no se hace la peticion`() {
        val service = FakeAlbumService { album(it) }
        val vm = AlbumViewModel(AlbumRepository(service))

        vm.publishComment(100, "Hola", 5, collectorId = null)

        assertNull(service.postedComment)
        assertEquals(UiState.Idle, vm.commentState.value)
    }

    @Test
    fun `descripcion vacia expone error de validacion y no llama al API`() {
        val service = FakeAlbumService { album(it) }
        val vm = AlbumViewModel(AlbumRepository(service))

        vm.publishComment(100, "   ", 5, collectorId = 1)

        assertEquals(CommentValidator.Error.EMPTY_DESCRIPTION, vm.validationError.value)
        assertNull(service.postedComment)
    }

    @Test
    fun `fallo del API expone Error`() {
        val service = FakeAlbumService(onComment = { _, _ -> throw IOException("sin red") }) { album(it) }
        val vm = AlbumViewModel(AlbumRepository(service))

        vm.publishComment(100, "Hola", 5, collectorId = 1)

        val state = vm.commentState.value as UiState.Error
        assertEquals("Sin conexión con el servidor", state.message)
    }

    @Test
    fun `clearCommentForm vuelve a Idle`() {
        val vm = AlbumViewModel(AlbumRepository(FakeAlbumService { album(it) }))
        vm.publishComment(100, "", 5, collectorId = 1)

        vm.clearCommentForm()

        assertEquals(UiState.Idle, vm.commentState.value)
        assertNull(vm.validationError.value)
    }
}
