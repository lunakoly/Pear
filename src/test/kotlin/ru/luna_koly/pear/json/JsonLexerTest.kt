package ru.luna_koly.pear.json

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class JsonLexerTest {
    @Test
    fun scan() {
        val out = JsonLexer.Fetcher("Hello test!", 0)
        assertEquals(true, JsonLexer.scan("Hello", out))
        assertEquals("Hello", out.value.toString())
        assertEquals(5, out.index)

        assertEquals(false, JsonLexer.scan("teff", out))
        assertEquals("", out.value.toString())
        assertEquals(6, out.index)

        assertEquals(true, JsonLexer.scan("test!", out))
        assertEquals("test!", out.value.toString())
        assertEquals(11, out.index)
    }

    @Test
    fun scanNonBlank() {
        val out = JsonLexer.Fetcher("Hello test!", 0)
        assertEquals(true, JsonLexer.scanNonBlank(out))
        assertEquals("Hello", out.value.toString())
        assertEquals(5, out.index)

        assertEquals(true, JsonLexer.scanNonBlank(out))
        assertEquals("test!", out.value.toString())
        assertEquals(11, out.index)
    }

    @Test
    fun scanFloat() {
        val out = JsonLexer.Fetcher("1 1.0 .5 1e-2 1.5E2 foo", 0)
        assertEquals(true, JsonLexer.scanFloat(out))
        assertEquals("1", out.value.toString())
        assertEquals(1, out.index)

        assertEquals(true, JsonLexer.scanFloat(out))
        assertEquals("1.0", out.value.toString())
        assertEquals(5, out.index)

        assertEquals(true, JsonLexer.scanFloat(out))
        assertEquals("0.5", out.value.toString())
        assertEquals(8, out.index)

        assertEquals(true, JsonLexer.scanFloat(out))
        assertEquals("1e-2", out.value.toString())
        assertEquals(13, out.index)

        assertEquals(true, JsonLexer.scanFloat(out))
        assertEquals("1.5E2", out.value.toString())
        assertEquals(19, out.index)

        assertEquals(false, JsonLexer.scanFloat(out))
        assertEquals("", out.value.toString())
        assertEquals(20, out.index)
    }

    @Test
    fun scanString() {
        val out = JsonLexer.Fetcher("   \"test lol kek\" shrek", 0)
        assertEquals(true, JsonLexer.scanString(out))
        assertEquals("test lol kek", out.value.toString())
        assertEquals(17, out.index)

        assertEquals(false, JsonLexer.scanString(out))
        assertEquals("", out.value.toString())
        assertEquals(18, out.index)
    }

    @Test
    fun scanKeyword() {
        val out = JsonLexer.Fetcher("do some++ shit", 0)
        assertEquals(true, JsonLexer.scanKeyword("do", out))
        assertEquals("do", out.value.toString())
        assertEquals(2, out.index)

        assertEquals(true, JsonLexer.scanKeyword("some", out))
        assertEquals("some", out.value.toString())
        assertEquals(7, out.index)

        assertEquals(false, JsonLexer.scanKeyword("shif", out))
        assertEquals("", out.value.toString())
        assertEquals(7, out.index)

        assertEquals(true, JsonLexer.scanKeyword("++", out))
        assertEquals("++", out.value.toString())
        assertEquals(9, out.index)

        assertEquals(true, JsonLexer.scanKeyword("shit", out))
        assertEquals("shit", out.value.toString())
        assertEquals(14, out.index)
    }
}