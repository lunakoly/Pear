package ru.luna_koly.pear.net.connection

class TrivialProtector : Protector {
    override fun encrypt(data: ByteArray) = data
    override fun decrypt(data: ByteArray) = data
}