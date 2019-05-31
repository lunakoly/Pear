package ru.luna_koly.pear.net.connection

/**
 * Provides additional encryption level
 * for Connection
 */
interface Protector {
    /**
     * Protects data
     */
    fun encrypt(data: ByteArray): ByteArray

    /**
     * Reads protected data
     */
    fun decrypt(data: ByteArray): ByteArray
}