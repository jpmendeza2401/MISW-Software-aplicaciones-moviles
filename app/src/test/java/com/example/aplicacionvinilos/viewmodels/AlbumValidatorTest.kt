package com.example.aplicacionvinilos.viewmodels

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// HU07 y HU11: reglas del formulario de creación de álbum.
class AlbumValidatorTest {

    private fun validate(
        name: String = "Buscando América",
        cover: String = "https://i.pinimg.com/564x/aa/5f/ed/cover.jpg",
        date: String = "1984-08-01",
        description: String = "Primer álbum de Rubén Blades",
        genre: String = "Salsa",
        label: String = "Elektra"
    ) = AlbumValidator.validate(name, cover, date, description, genre, label)

    @Test
    fun `formulario completo es valido`() = assertNull(validate())

    @Test
    fun `nombre con menos de 3 caracteres es invalido`() {
        assertEquals(AlbumValidator.Error.INVALID_NAME, validate(name = "ab"))
        assertEquals(AlbumValidator.Error.INVALID_NAME, validate(name = "  a "))
        assertNull(validate(name = "abc"))
    }

    @Test
    fun `portada debe ser una URL http o https`() {
        assertEquals(AlbumValidator.Error.INVALID_COVER, validate(cover = ""))
        assertEquals(AlbumValidator.Error.INVALID_COVER, validate(cover = "portada.jpg"))
        assertEquals(AlbumValidator.Error.INVALID_COVER, validate(cover = "ftp://x.com/a.jpg"))
        assertNull(validate(cover = "http://10.0.2.2/a.jpg"))
    }

    @Test
    fun `fecha vacia o inexistente es invalida`() {
        assertEquals(AlbumValidator.Error.INVALID_DATE, validate(date = ""))
        assertEquals(AlbumValidator.Error.INVALID_DATE, validate(date = "1984-13-01"))
        assertEquals(AlbumValidator.Error.INVALID_DATE, validate(date = "1984-02-30"))
        assertEquals(AlbumValidator.Error.INVALID_DATE, validate(date = "01/08/1984"))
    }

    @Test
    fun `febrero bisiesto acepta el dia 29`() {
        assertTrue(AlbumValidator.isValidDate("2024-02-29"))
        assertFalse(AlbumValidator.isValidDate("2023-02-29"))
        assertFalse(AlbumValidator.isValidDate("1900-02-29"))
        assertTrue(AlbumValidator.isValidDate("2000-02-29"))
    }

    @Test
    fun `descripcion en blanco es invalida`() =
        assertEquals(AlbumValidator.Error.EMPTY_DESCRIPTION, validate(description = "   "))

    @Test
    fun `solo se aceptan los generos permitidos`() {
        listOf("Classical", "Salsa", "Rock", "Folk").forEach { assertNull(validate(genre = it)) }
        assertEquals(AlbumValidator.Error.INVALID_GENRE, validate(genre = "Jazz"))
        assertEquals(AlbumValidator.Error.INVALID_GENRE, validate(genre = ""))
    }

    @Test
    fun `asociacion exige precio entero mayor que 0 y estado permitido`() {
        assertNull(AlbumValidator.validateAssociation("25000", "Active"))
        assertNull(AlbumValidator.validateAssociation(" 1 ", "Inactive"))
        listOf("", "0", "-5", "abc", "10.5").forEach {
            assertEquals(AlbumValidator.Error.INVALID_PRICE, AlbumValidator.validateAssociation(it, "Active"))
        }
        assertEquals(AlbumValidator.Error.INVALID_STATUS, AlbumValidator.validateAssociation("100", ""))
        assertEquals(AlbumValidator.Error.INVALID_STATUS, AlbumValidator.validateAssociation("100", "Sold"))
    }

    @Test
    fun `solo se aceptan los sellos permitidos`() {
        listOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records")
            .forEach { assertNull(validate(label = it)) }
        assertEquals(AlbumValidator.Error.INVALID_LABEL, validate(label = "Universal"))
        assertEquals(AlbumValidator.Error.INVALID_LABEL, validate(label = ""))
    }
}
