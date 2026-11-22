package com.example.aplicacionvinilos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aplicacionvinilos.data.model.Track
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

// HU08: asociar un track a un álbum desde el detalle.
class AlbumViewModelTrackTest {

    @get:Rule val instantRule = InstantTaskExecutorRule()
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `track valido envia el cuerpo esperado y expone Success`() {
        val service = FakeAlbumService { album(it) }
        val vm = AlbumViewModel(AlbumRepository(service))

        vm.addTrack(albumId = 100, name = "  Decisiones ", duration = "5:05")

        assertEquals(100, service.postedAlbumId)
        assertEquals("Decisiones", service.postedTrack?.name)
        assertEquals("5:05", service.postedTrack?.duration)
        assertTrue(vm.trackState.value is UiState.Success)
        assertNull(vm.trackError.value)
    }

    @Test
    fun `al agregar con exito se recarga el album con el nuevo track`() {
        val updated = album(100, tracks = listOf(Track(1, "Decisiones", "5:05")))
        val vm = AlbumViewModel(AlbumRepository(FakeAlbumService { updated }))

        vm.addTrack(100, "Decisiones", "5:05")

        assertEquals(updated, (vm.album.value as UiState.Success).data)
    }

    @Test
    fun `nombre corto expone error de validacion y no llama al API`() {
        val service = FakeAlbumService { album(it) }
        val vm = AlbumViewModel(AlbumRepository(service))

        vm.addTrack(100, "ab", "5:05")

        assertEquals(TrackValidator.Error.INVALID_NAME, vm.trackError.value)
        assertNull(service.postedTrack)
        assertEquals(UiState.Idle, vm.trackState.value)
    }

    @Test
    fun `duracion menor a un minuto no llama al API`() {
        val service = FakeAlbumService { album(it) }
        val vm = AlbumViewModel(AlbumRepository(service))

        vm.addTrack(100, "Corto", "0:30")

        assertEquals(TrackValidator.Error.DURATION_TOO_SHORT, vm.trackError.value)
        assertNull(service.postedTrack)
    }

    @Test
    fun `fallo de red expone Error`() {
        val service = FakeAlbumService(onTrack = { _, _ -> throw IOException("sin red") }) { album(it) }
        val vm = AlbumViewModel(AlbumRepository(service))

        vm.addTrack(100, "Decisiones", "5:05")

        assertEquals(
            UiState.Error("Sin conexión con el servidor"),
            vm.trackState.value
        )
    }

    @Test
    fun `clearTrackForm vuelve a Idle y limpia el error`() {
        val vm = AlbumViewModel(AlbumRepository(FakeAlbumService { album(it) }))

        vm.addTrack(100, "ab", "5:05")
        vm.clearTrackForm()

        assertNull(vm.trackError.value)
        assertEquals(UiState.Idle, vm.trackState.value)
    }
}
