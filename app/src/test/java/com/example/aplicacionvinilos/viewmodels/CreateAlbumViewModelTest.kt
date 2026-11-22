package com.example.aplicacionvinilos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aplicacionvinilos.data.repository.AlbumRepository
import com.example.aplicacionvinilos.testutil.FakeAlbumService
import com.example.aplicacionvinilos.testutil.MainDispatcherRule
import com.example.aplicacionvinilos.testutil.album
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// HU07: crear álbum desde el formulario.
class CreateAlbumViewModelTest {

    @get:Rule val instantRule = InstantTaskExecutorRule()
    @get:Rule val mainRule = MainDispatcherRule()

    private fun viewModel(service: FakeAlbumService) =
        CreateAlbumViewModel(AlbumRepository(service, Dispatchers.Unconfined))

    private fun CreateAlbumViewModel.submitValid(name: String = "  TEST  ") = submit(
        name, "https://img.com/a.jpg", "1984-08-01", "Descripción", "Salsa", "Elektra"
    )

    @Test
    fun `envio valido arma el cuerpo esperado y expone Success`() {
        val service = FakeAlbumService { album(it) }
        val vm = viewModel(service)

        vm.submitValid()

        val posted = service.postedAlbum!!
        assertEquals("TEST", posted.name)
        assertEquals("1984-08-01T00:00:00-05:00", posted.releaseDate)
        assertEquals("Salsa", posted.genre)
        assertEquals("Elektra", posted.recordLabel)
        assertTrue(vm.state.value is UiState.Success)
        assertNull(vm.validationError.value)
    }

    @Test
    fun `formulario invalido expone el error y no llama al API`() {
        val service = FakeAlbumService { album(it) }
        val vm = viewModel(service)

        vm.submitValid(name = "ab")

        assertEquals(AlbumValidator.Error.INVALID_NAME, vm.validationError.value)
        assertNull(service.postedAlbum)
        assertEquals(UiState.Idle, vm.state.value)
    }

    @Test
    fun `fallo de red expone Error con el mensaje`() {
        val service = FakeAlbumService(onCreate = { throw IOException("sin red") }) { album(it) }
        val vm = viewModel(service)

        vm.submitValid()

        val state = vm.state.value
        assertTrue(state is UiState.Error)
        assertEquals("Sin conexión con el servidor", (state as UiState.Error).message)
    }

    @Test
    fun `consumeState vuelve a Idle y onFormChanged limpia el error`() {
        val vm = viewModel(FakeAlbumService { album(it) })

        vm.submitValid(name = "ab")
        vm.onFormChanged()
        assertNull(vm.validationError.value)

        vm.submitValid()
        vm.consumeState()
        assertEquals(UiState.Idle, vm.state.value)
    }
}
