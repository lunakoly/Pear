package ru.luna_koly.json

import ru.luna_koly.json.JsonLexer.Fetcher
import ru.luna_koly.json.JsonLexer.scan
import ru.luna_koly.json.JsonLexer.scanFloat
import ru.luna_koly.json.JsonLexer.scanKeyword
import ru.luna_koly.json.JsonLexer.scanNonBlank
import ru.luna_koly.json.JsonLexer.scanString
import kotlin.math.max
import kotlin.math.min

/**
 * 'C-Style' parser for Json that uses
 * JsonLexer for lexical analysis
 */
object JsonParser {
    /**
     * Shows part of text near to the point
     * that info fetcher is pointing to
     */
    private fun printErrorRange(info: Fetcher): String {
        val preText = max(info.index - 5, 0)
        val postText = min(info.index + 6, info.text.length)
        return info.text.substring(preText, postText) + "\n     ^     "
    }

    /**
     * Exceptions that shows where the error occurred
     */
    class SyntaxException(message: String, info: Fetcher):
        Exception(message + " at ${info.index}\":\n" + printErrorRange(info))

    /**
     * Parses list contents without matching
     * square brackets
     */
    private fun parseList(out: JsonLexer.Fetcher): Json.List {
        val list = Json.List()

        do {
            list.add(parseObject(out))
        } while (scan(",", out))

        return list
    }

    /**
     * Parses dictionary content without
     * matching curly braces
     */
    private fun parseDictionary(out: Fetcher): Json.Dictionary {
        val dictionary = Json.Dictionary()

        do {
            if (!scanString(out))
                throw SyntaxException("Error > String expected", out)

            val key = out.value.toString()

            if (!scan(":", out))
                throw SyntaxException("Error > `:` expected", out)

            val value = parseObject(out)
            dictionary[key] = value

        } while (scan(",", out))

        return dictionary
    }

    /**
     * Parses either an item, a list or a dictionary
     */
    private fun parseObject(out: Fetcher): Json.Object {
        if (scan("{", out)) {
            val value = parseDictionary(out)

            if (!scan("}", out))
                throw SyntaxException("Error > `}` expected", out)

            return value
        }

        if (scan("[", out)) {
            val value = parseList(out)

            if (!scan("]", out))
                throw SyntaxException("Error > `]` expected", out)

            return value
        }

        if (scanString(out))
            return Json.Item(out.value.toString(), true)

        if (
            scanKeyword("false", out) ||
            scanKeyword("true", out) ||
            scanFloat(out)
        ) return Json.Item(out.value.toString(), false)

        scanNonBlank(out)
        return Json.Item(out.value.toString(), true)
    }

    /**
     * Parses Json string representation
     * to Json.Object tree
     */
    fun parse(text: String): Json.Object {
        val out = Fetcher(text, 0)
        return parseObject(out)
    }
}