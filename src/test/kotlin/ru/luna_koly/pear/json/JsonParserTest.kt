package ru.luna_koly.pear.json

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class JsonParserTest {
    @Test
    fun parse() {
        val result = JsonParser.parse("""
            {
                "a": [
                    true,"kek", {
                        "b": 120
                    }, -1.3e-16
                ]
            }
        """.trimIndent())

        assertEquals("120", result["a"][2]["b"].toString())
        assertEquals("true", result["a"][0].toString())
        assertEquals("\"kek\"", result["a"][1].toString())
        assertEquals("-1.3e-16", result["a"][3].toString())

        assertThrows(JsonParser.SyntaxException::class.java) {
            JsonParser.parse("""
                {
                    a = 1
                }
            """.trimIndent())
        }

        assertThrows(JsonParser.SyntaxException::class.java) {
            JsonParser.parse("""
                {
                    "b" = 1
                }
            """.trimIndent())
        }

        assertThrows(JsonParser.SyntaxException::class.java) {
            JsonParser.parse("""
                {
                    "a"  : 1
                    "c" : 2.1
                }
            """.trimIndent())
        }
    }
}