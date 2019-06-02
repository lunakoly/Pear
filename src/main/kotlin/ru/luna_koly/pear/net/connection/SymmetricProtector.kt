package ru.luna_koly.pear.net.connection

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Manages AES/CBC/PKCS5Padding encryption
 */
class SymmetricProtector(val key: SecretKey) : Protector {
    companion object {
        private const val IV_SIZE = 16

        private val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        /**
         * Returns new instance with new key and ivSpec
         */
        fun generate(): SymmetricProtector {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(128)
            val key = keyGenerator.generateKey()
            return SymmetricProtector(key)
        }

        /**
         * Constructs SymmetricProtector from key and iv
         * accepted via network
         */
        fun fromByteData(rawKey: ByteArray): SymmetricProtector {
            val key = SecretKeySpec(rawKey, "AES")
            return SymmetricProtector(key)
        }
    }

    override fun encrypt(data: ByteArray): ByteArray {
        synchronized(cipher) {
            val iv = ByteArray(IV_SIZE)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
            val encrypted = cipher.doFinal(data)

            val result = ByteArray(iv.size + encrypted.size)
            iv.copyInto(result, 0, 0, iv.size)
            encrypted.copyInto(result, iv.size, 0, encrypted.size)
            return result
        }
    }

    override fun decrypt(data: ByteArray): ByteArray {
        synchronized(cipher) {
            val iv = data.copyOfRange(0, IV_SIZE)
            val ivSpec = IvParameterSpec(iv)

            val encrypted = data.copyOfRange(IV_SIZE, data.size)

            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
            return cipher.doFinal(encrypted)
        }
    }
}