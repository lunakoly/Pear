package ru.luna_koly.pear.net.connection

/**
 * Protector that does nothing
 */
class TrivialProtector : Protector {
    override fun encrypt(data: ByteArray) = data
    override fun decrypt(data: ByteArray) = data
}