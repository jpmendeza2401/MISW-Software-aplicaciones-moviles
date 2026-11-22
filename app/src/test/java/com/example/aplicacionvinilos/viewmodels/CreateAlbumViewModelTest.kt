package com.example.aplicacionvinilos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aplicacionvinilos.data.repository.AlbumRepository
import com.example.aplicacionvinilos.data.repository.CollectorRepository
import com.example.aplicacionvinilos.testutil.FakeAlbumService
import com.example.aplicacionvinilos.testutil.FakeCollectorApiService
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

    private fun viewModel(
        service: FakeAlbumService,
        api: FakeCollectorApiService = FakeCollectorApiService()
    ) = CreateAlbumViewModel(
        AlbumRepository(service, Dispatchers.Unconfined),
        CollectorRepository(api)
    )

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

    private fun CreateAlbumViewModel.submitWith(association: CreateAlbumViewModel.Association) = submit(
        "TEST", "https://img.com/a.jpg", "1984-08-01", "Descripción", "Salsa", "Elektra", association
    )

    @Test
    fun `con asociacion crea el album y lo asocia al coleccionista`() {
        val api = FakeCollectorApiService()
        val vm = viewModel(FakeAlbumService(onCreate = { album(42, it.name) }) { album(it) }, api)

        vm.submitWith(CreateAlbumViewModel.Association(7, " 25000 ", "Active"))

        assertEquals(7, api.postedCollectorId)
        assertEquals(42, api.postedAlbumId)
        assertEquals(25000, api.postedBody?.price)
        assertEquals("Active", api.postedBody?.status)
        assertTrue(vm.state.value is UiState.Success)
    }

    @Test
    fun `precio o estado invalidos no llaman a ningun endpoint`() {
        val service = FakeAlbumService { album(it) }
        val api = FakeCollectorApiService()
        val vm = viewModel(service, api)

        vm.submitWith(CreateAlbumViewModel.Association(7, "0", "Active"))
        assertEquals(AlbumValidator.Error.INVALID_PRICE, vm.validationError.value)

        vm.submitWith(CreateAlbumViewModel.Association(7, "100", "Otro"))
        assertEquals(AlbumValidator.Error.INVALID_STATUS, vm.validationError.value)

        assertNull(service.postedAlbum)
        assertEquals(0, api.associateCalls)
    }

    @Test
    fun `si falla la asociacion el reintento no vuelve a crear el album`() {
        var creates = 0
        var fail = true
        val service = FakeAlbumService(onCreate = { creates++; album(42, it.name) }) { album(it) }
        val api = FakeCollectorApiService { _, _, b ->
            if (fail) throw IOException("sin red")
            com.example.aplicacionvinilos.data.model.CollectorAlbum(1, b.price, b.status)
        }
        val vm = viewModel(service, api)
        val association = CreateAlbumViewModel.Association(7, "25000", "Active")

        vm.submitWith(association)
        assertTrue(vm.state.value is UiState.Error)

        fail = false
        vm.submitWith(association)

        assertEquals(1, creates)
        assertEquals(2, api.associateCalls)
        assertTrue(vm.state.value is UiState.Success)
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
