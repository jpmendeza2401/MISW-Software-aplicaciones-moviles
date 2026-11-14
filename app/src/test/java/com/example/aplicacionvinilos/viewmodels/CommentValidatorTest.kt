package com.example.aplicacionvinilos.viewmodels

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// HU09: reglas del formulario de comentario.
class CommentValidatorTest {

    @Test
    fun `comentario valido no tiene errores`() {
        assertNull(CommentValidator.validate("Gran álbum", 4))
    }

    @Test
    fun `descripcion vacia o en blanco es invalida`() {
        assertEquals(CommentValidator.Error.EMPTY_DESCRIPTION, CommentValidator.validate("", 4))
        assertEquals(CommentValidator.Error.EMPTY_DESCRIPTION, CommentValidator.validate("   ", 4))
    }

    @Test
    fun `descripcion de mas de 600 caracteres es invalida`() {
        assertEquals(
            CommentValidator.Error.DESCRIPTION_TOO_LONG,
            CommentValidator.validate("a".repeat(601), 4)
        )
        assertNull(CommentValidator.validate("a".repeat(600), 4))
    }

    @Test
    fun `calificacion fuera de 1 a 5 es invalida`() {
        assertEquals(CommentValidator.Error.INVALID_RATING, CommentValidator.validate("Ok", 0))
        assertEquals(CommentValidator.Error.INVALID_RATING, CommentValidator.validate("Ok", 6))
        assertNull(CommentValidator.validate("Ok", 1))
        assertNull(CommentValidator.validate("Ok", 5))
    }
}
