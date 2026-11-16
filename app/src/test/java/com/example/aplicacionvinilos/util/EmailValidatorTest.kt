package com.example.aplicacionvinilos.util

import com.example.aplicacionvinilos.util.EmailValidator.Result
import org.junit.Assert.assertEquals
import org.junit.Test

class EmailValidatorTest {

    @Test
    fun `correos validos`() {
        listOf("a@b.co", "john.doe+tag@mail.example.com", " user_1@dominio.org ").forEach {
            assertEquals(it, Result.VALID, EmailValidator.validate(it))
        }
    }

    @Test
    fun `vacio o nulo`() {
        assertEquals(Result.EMPTY, EmailValidator.validate(null))
        assertEquals(Result.EMPTY, EmailValidator.validate("   "))
    }

    @Test
    fun `formato invalido`() {
        listOf("abc", "a@b", "@b.com", "a@.com", "a b@c.com", "a@@b.com", "a..b@c.com", "a@b.c")
            .forEach { assertEquals(it, Result.INVALID_FORMAT, EmailValidator.validate(it)) }
    }
}
