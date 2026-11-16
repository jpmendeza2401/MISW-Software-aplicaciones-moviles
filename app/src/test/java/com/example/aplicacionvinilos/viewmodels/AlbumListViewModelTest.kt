package com.example.aplicacionvinilos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.repository.AlbumListRepository
import com.example.aplicacionvinilos.testutil.FakeApiService
import com.example.aplicacionvinilos.testutil.MainDispatcherRule
import com.example.aplicacionvinilos.testutil.album
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// HU01: estados del listado (Loading / Success / vacío / Error / reintento).
class AlbumListViewModelTest {

    @get:Rule val instantRule = InstantTaskExecutorRule()
    @get:Rule val mainRule = MainDispatcherRule()

    private fun viewModel(block: suspend () -> List<Album>) =
        AlbumListViewModel(AlbumListRepository(FakeApiService(block)))

    @Test
    fun `al crearse carga y expone Loading hasta recibir la respuesta`() {
        val gate = CompletableDeferred<List<Album>>()
        val vm = viewModel { gate.await() }
        assertTrue(vm.albums.value is UiState.Loading)

        val albums = listOf(album(1), album(2))
        gate.complete(albums)

        assertEquals(UiState.Success(albums), vm.albums.value)
    }

    @Test
    fun `lista vacia produce Success vacio`() {
        val vm = viewModel { emptyList() }
        val state = vm.albums.value as UiState.Success
        assertTrue(state.data.isEmpty())
    }

    @Test
    fun `fallo de red produce Error con mensaje`() {
        val vm = viewModel { throw IOException("x") }
        val state = vm.albums.value as UiState.Error
        assertEquals("Sin conexión con el servidor", state.message)
    }

    @Test
    fun `loadAlbums reintenta despues de un error`() {
        var calls = 0
        val vm = viewModel {
            calls++
            if (calls == 1) throw IOException("x") else listOf(album(1))
        }
        assertTrue(vm.albums.value is UiState.Error)

        vm.loadAlbums()

        assertTrue(vm.albums.value is UiState.Success)
        assertEquals(2, calls)
    }
}
