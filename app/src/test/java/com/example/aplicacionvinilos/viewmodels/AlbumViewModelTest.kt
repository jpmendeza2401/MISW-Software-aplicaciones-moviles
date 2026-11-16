package com.example.aplicacionvinilos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aplicacionvinilos.data.repository.AlbumRepository
import com.example.aplicacionvinilos.testutil.FakeAlbumService
import com.example.aplicacionvinilos.testutil.MainDispatcherRule
import com.example.aplicacionvinilos.testutil.album
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// HU02: estados del detalle de álbum.
class AlbumViewModelTest {

    @get:Rule val instantRule = InstantTaskExecutorRule()
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `estado inicial es Idle`() {
        val vm = AlbumViewModel(AlbumRepository(FakeAlbumService { album(it) }))
        assertEquals(UiState.Idle, vm.album.value)
    }

    @Test
    fun `loadAlbum expone Success con tracks y comentarios`() {
        val expected = album(3)
        val vm = AlbumViewModel(AlbumRepository(FakeAlbumService { expected }))

        vm.loadAlbum(3)

        val state = vm.album.value as UiState.Success
        assertEquals(expected, state.data)
        assertTrue(state.data.tracks.isNotEmpty())
        assertTrue(state.data.comments.isNotEmpty())
    }

    @Test
    fun `loadAlbum con fallo expone Error`() {
        val vm = AlbumViewModel(AlbumRepository(FakeAlbumService { throw IOException("sin red") }))

        vm.loadAlbum(1)

        assertTrue(vm.album.value is UiState.Error)
    }

    @Test
    fun `album sin tracks ni comentarios sigue siendo Success`() {
        val vm = AlbumViewModel(
            AlbumRepository(FakeAlbumService { album(tracks = emptyList(), comments = emptyList()) })
        )

        vm.loadAlbum(1)

        val state = vm.album.value as UiState.Success
        assertTrue(state.data.tracks.isEmpty() && state.data.comments.isEmpty())
    }
}
