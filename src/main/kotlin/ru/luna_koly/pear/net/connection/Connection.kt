package ru.luna_koly.pear.net.connection

/**
 * Something that delivers data
 */
interface Connection {
    /**
     * Provides additional encryption level
     */
    var protector: Protector

    /**
     * Protects data and sends it
     */
    fun sendBytes(data: ByteArray)

    /**
     * Reads protected data
     */
    fun readBytes(): ByteArray

    /**
     * Alias for string data
     */
    fun sendString(string: String) = sendBytes(string.toByteArray(Charsets.UTF_8))

    /**
     * Alias for string data
     */
    fun readString() = String(readBytes(), Charsets.UTF_8)
}