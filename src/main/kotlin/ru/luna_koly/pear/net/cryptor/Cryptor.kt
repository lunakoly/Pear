package ru.luna_koly.pear.net.cryptor

import java.security.PublicKey
import kotlin.random.Random

/**
 * Performs client-dependent encryption
 */
interface Cryptor {
    /**
     * Encrypts data for sending to another identity
     */
    fun encrypt(data: ByteArray, publicKey: PublicKey): ByteArray

    /**
     * Decrypts data destined for the client
     */
    fun decrypt(data: ByteArray): ByteArray

    /**
     * Returns some random byte array value
     * used to check whether opponent is able
     * to decrypt the message destined for them
     * and sendMessage it back to us
     */
    fun generateSecret() = Random.nextBytes(20)

    /**
     * Turns byte array into a usable encryption key
     */
    fun toPublicKey(data: ByteArray): PublicKey

    /**
     * Returns clients identity
     */
    fun getIdentity(): PublicKey
}