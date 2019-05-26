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
class SymmetricProtector(val key: SecretKey, val ivSpec: IvParameterSpec) : Protector {
    companion object {
        private val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        /**
         * Returns new instance with new key and ivSpec
         */
        fun generate(): SymmetricProtector {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(128)
            val key = keyGenerator.generateKey()

            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)

            return SymmetricProtector(key, IvParameterSpec(iv))
        }

        /**
         * Constructs SymmetricProtector from key and iv
         * accepted via network
         */
        fun fromByteData(rawKey: ByteArray, rawIv: ByteArray): SymmetricProtector {
            val key = SecretKeySpec(rawKey, "AES")
            val iv = IvParameterSpec(rawIv)
            return SymmetricProtector(key, iv)
        }
    }

    override fun encrypt(data: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
        return cipher.doFinal(data)
    }

    override fun decrypt(data: ByteArray): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        return cipher.doFinal(data)
    }
}