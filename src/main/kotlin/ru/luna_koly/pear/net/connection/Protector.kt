package ru.luna_koly.pear.net.connection

interface Protector {
    fun encrypt(data: ByteArray): ByteArray
    fun decrypt(data: ByteArray): ByteArray
}