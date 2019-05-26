package ru.luna_koly.pear.util

import java.util.*

class ByteCache {
    /**
     * Caches incoming ByteArray data and helps to
     * construct it back into one big ByteArray
     */
    private val cache = LinkedList<ByteArray>()

    /**
     * The sum of all chunk sizes
     */
    private var totalSize = 0

    /**
     * Resets to default cleared state
     */
    fun clear() {
        cache.clear()
        totalSize = 0
    }

    /**
     * Appends byte chunk to the end
     */
    fun add(data: ByteArray) {
        cache.add(data)
        totalSize += data.size
    }

    /**
     * Builds one big ByteArray of
     * all of the separate chunks
     */
    fun concat(): ByteArray {
        val result = ByteArray(totalSize)
        var index = 0

        for (it in cache) {
            System.arraycopy(it, 0, result, index, it.size)
            index += it.size
        }

        return result
    }
}