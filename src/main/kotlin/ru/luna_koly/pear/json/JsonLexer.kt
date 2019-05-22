package ru.luna_koly.pear.json

/**
 * 'C-Style' lexical analyzer that
 * implements common functionality
 * required to parse any syntax
 */
@Suppress("MemberVisibilityCanBePrivate")
object JsonLexer {
    /**
     * Represents parsing context and
     * is used for recursive caret movement.
     * It allows to separate inner functions
     * workflow from their callers
     */
    data class Fetcher(val text: String, var index: Int) {
        var value = StringBuilder()

        constructor(other: Fetcher) : this(other.text, other.index)

        /**
         * Used at the end of inner function
         * to submit it's result
         */
        fun fetch(other: Fetcher) {
            value.append(other.value)
            index = other.index
        }
    }

    /**
     * If the next few chars represent
     * the token, returns true, moves caret forward
     * and appends token value to output.value
     */
    private fun read(token: String, out: Fetcher): Boolean {
        for (it in 0 until token.length)
            if (
                out.index + it >= out.text.length ||
                token[it] != out.text[out.index + it]
            ) return false

        out.value.append(token)
        out.index += token.length
        return true
    }

    /**
     * Returns true if it manages to read
     * something before reaching whitespaces.
     * Moves index forward and appends content
     * to value
     */
    private fun readNonBlank(out: Fetcher): Boolean {
        if (
            out.text[out.index] == '\n' ||
            out.text[out.index] == '\t' ||
            out.text[out.index] == ' '
        ) return false

        out.value.append(out.text[out.index])
        out.index++

        while (
            out.index < out.text.length &&
            out.text[out.index] != '\n' &&
            out.text[out.index] != '\t' &&
            out.text[out.index] != ' '
        ) {
            out.value.append(out.text[out.index])
            out.index++
        }

        return true
    }

    /**
     * Reads decimal numbers sequence
     */
    private fun readDecimal(out: Fetcher): Boolean {
        if (out.text[out.index] !in '0'..'9')
            return false

        out.value.append(out.text[out.index])
        out.index++

        while (out.text[out.index] in '0'..'9') {
            out.value.append(out.text[out.index])
            out.index++
        }

        return true
    }

    /**
     * Reads optional minus and decimal
     * numbers sequence
     */
    private fun readSignedDecimal(out: Fetcher): Boolean {
        val local = Fetcher(out)
        read("-", local)

        if (!readDecimal(local))
            return false

        out.fetch(local)
        return true
    }

    /**
     * Reads dot symbol if there is no
     * one more dot after it
     */
    private fun readSingleDot(out: Fetcher): Boolean {
        val local = Fetcher(out)

        if (!read(".", local))
            return false

        if (read(".", local))
            return false

        out.fetch(local)
        return true
    }

    /**
     * Reads float number representation
     * "1", "1.0", ".5", "1e-2", "1.5E2"
     */
    private fun readFloat(out: Fetcher): Boolean {
        var wereNumbersBeforeDot = false
        val local = Fetcher(out)
        read("-", local)

        if (!readDecimal(local))
            local.value.append(0)
        else
            wereNumbersBeforeDot = true

        if (readSingleDot(local)) {
            if (!readDecimal(local))
                return false
        } else if (!wereNumbersBeforeDot)
            return false

        if (read("e", local) || read("E", local))
            if (!readSignedDecimal(local))
                return false

        out.fetch(local)
        return true
    }

    /**
     * Reads double-quoted string and
     * removes quotes
     */
    private fun readString(out: Fetcher): Boolean {
        if (out.text[out.index] != '"')
            return false

        out.index++
        val local = Fetcher(out)

        while (
            local.index < local.text.length &&
            local.text[local.index] != '\"'
        ) {
            if (local.text[local.index] == '\\')
                local.index++

            local.value.append(local.text[local.index])
            local.index++
        }

        local.index++
        out.fetch(local)
        return true
    }

    /**
     * Reads character that is not a part
     * of any common operator
     * Actually this thing is language-specific.
     * Maybe I should put it somewhere else
     */
    private fun readOperator(out: Fetcher): Boolean {
        if (
            out.index >= out.text.length ||
            out.text[out.index] in 'a'..'z' ||
            out.text[out.index] in 'A'..'Z'
        ) return false

        out.value.append(out.text[out.index])
        out.index++
        return true
    }

    /**
     * Reads a token and returns true
     * if there is an operator after it
     * (whitespaces are operators)
     */
    private fun readKeyword(token: String, out: Fetcher): Boolean {
        val local = Fetcher(out)

        if (!read(token, local))
            return false

        if (readOperator(local))
            return false

        out.fetch(local)
        return true
    }

    /**
     * Moves index forward until non-whitespace
     * characters are met
     */
    private fun skipBlank(out: Fetcher) {
        while (
            out.text[out.index] == '\n' ||
            out.text[out.index] == '\t' ||
            out.text[out.index] == ' '
        ) out.index++
    }

    /**
     * If the next few non-whitespace chars represent
     * the token, returns true, moves caret forward
     * and sets token value to output.value
     */
    fun scan(token: String, out: Fetcher): Boolean {
        skipBlank(out)
        out.value.clear()
        return read(token, out)
    }

    /**
     * Scans at least some non-whitespace
     * symbol sequence
     */
    fun scanNonBlank(out: Fetcher): Boolean {
        skipBlank(out)
        out.value.clear()
        return readNonBlank(out)
    }

    /**
     * If the next few non-whitespace chars represent
     * a float, returns true, moves caret forward
     * and sets token value to output.value
     */
    fun scanFloat(out: Fetcher): Boolean {
        skipBlank(out)
        out.value.clear()
        return readFloat(out)
    }

    /**
     * If the next few non-whitespace chars represent
     * a string, returns true, moves caret forward
     * and sets token value to output.value
     */
    fun scanString(out: Fetcher): Boolean {
        skipBlank(out)
        out.value.clear()
        return readString(out)
    }

    /**
     * If the next few non-whitespace chars represent
     * the token with an operator after it, returns true,
     * moves caret forward and sets token value
     * to output.value
     */
    fun scanKeyword(token: String, out: Fetcher): Boolean {
        skipBlank(out)
        out.value.clear()
        return readKeyword(token, out)
    }
}