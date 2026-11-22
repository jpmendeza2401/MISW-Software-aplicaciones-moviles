package com.example.aplicacionvinilos.viewmodels

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// HU08: nombre con mínimo 3 caracteres y duración de mínimo 1 minuto.
class TrackValidatorTest {

    @Test
    fun `track valido`() {
        assertNull(TrackValidator.validate("Decisiones", "5:05"))
        assertNull(TrackValidator.validate("abc", "1:00"))
        assertNull(TrackValidator.validate("Largo", "12:30"))
    }

    @Test
    fun `nombre de menos de 3 caracteres es invalido`() {
        assertEquals(TrackValidator.Error.INVALID_NAME, TrackValidator.validate("ab", "5:05"))
        assertEquals(TrackValidator.Error.INVALID_NAME, TrackValidator.validate("  a  ", "5:05"))
        assertEquals(TrackValidator.Error.INVALID_NAME, TrackValidator.validate("", "5:05"))
    }

    @Test
    fun `duracion menor a un minuto es invalida`() {
        assertEquals(TrackValidator.Error.DURATION_TOO_SHORT, TrackValidator.validate("Corto", "0:59"))
        assertEquals(TrackValidator.Error.DURATION_TOO_SHORT, TrackValidator.validate("Corto", "0:00"))
    }

    @Test
    fun `duracion con formato incorrecto es invalida`() {
        listOf("", "5", "5:5", "5:60", "abc", "1:2:3", "5:05:00", "-1:00").forEach {
            assertEquals(it, TrackValidator.Error.INVALID_DURATION_FORMAT, TrackValidator.validate("Nombre", it))
        }
    }
}
