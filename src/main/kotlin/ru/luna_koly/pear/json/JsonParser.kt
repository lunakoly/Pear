package ru.luna_koly.pear.json

import ru.luna_koly.pear.json.JsonLexer.Fetcher
import ru.luna_koly.pear.json.JsonLexer.scan
import ru.luna_koly.pear.json.JsonLexer.scanFloat
import ru.luna_koly.pear.json.JsonLexer.scanKeyword
import ru.luna_koly.pear.json.JsonLexer.scanNonBlank
import ru.luna_koly.pear.json.JsonLexer.scanString

/**
 * 'C-Style' parser for Json that uses
 * JsonLexer for lexical analysis
 */
object JsonParser {
    private fun parseList(out: JsonLexer.Fetcher): Json.List {
        val list = Json.List()

        do {
            list.add(parseObject(out))
        } while (scan(",", out))

        return list
    }

    private fun parseDictionary(out: Fetcher): Json.Dictionary {
        val dictionary = Json.Dictionary()

        do {
            if (!scanString(out))
                continue

            val key = out.value.toString()

            if (!scan(":", out))
                continue

            val value = parseObject(out)
            dictionary[key] = value

        } while (scan(",", out))

        return dictionary
    }

    private fun parseObject(out: Fetcher): Json.Object {
        if (scan("{", out)) {
            val value = parseDictionary(out)

            if (!scan("}", out))
                return Json.Dictionary()

            return value
        }

        if (scan("[", out)) {
            val value = parseList(out)

            if (!scan("]", out))
                return Json.List()

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