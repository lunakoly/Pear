package ru.luna_koly.pear.net.cryptor

import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * Implements main functionality
 */
class ClientCryptor : Cryptor {
    private val cipher: Cipher = Cipher.getInstance("RSA")

    override fun encrypt(data: ByteArray, publicKey: PublicKey): ByteArray {
        synchronized(cipher) {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            return cipher.doFinal(data)
        }
    }

    private fun decrypt(data: ByteArray, privateKey: PrivateKey): ByteArray {
        synchronized(cipher) {
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            return cipher.doFinal(data)
        }
    }

    override fun toPublicKey(data: ByteArray): PublicKey {
        return KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(data))
    }

    private fun toPrivateKey(data: ByteArray): PrivateKey {
        return KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(data))
    }

    private var publicKey: PublicKey
    private var privateKey: PrivateKey

    override fun getIdentity() = publicKey

    override fun decrypt(data: ByteArray) = decrypt(data, privateKey)

    init {
        val publicKeyPath = Paths.get(System.getProperty("user.home"), ".pear", "public_key")
        val privateKeyPath = Paths.get(System.getProperty("user.home"), ".pear", "private_key")

        if (
            Files.isRegularFile(publicKeyPath) &&
            Files.isRegularFile(privateKeyPath)
        ) {
            publicKey =
                toPublicKey(Files.readAllBytes(publicKeyPath))
            privateKey =
                toPrivateKey(Files.readAllBytes((privateKeyPath)))
        }

        else {
            val keyGenerator = KeyPairGenerator.getInstance("RSA")
            keyGenerator.initialize(2048)
            val keyPair = keyGenerator.generateKeyPair()

            publicKey = keyPair.public
            privateKey = keyPair.private
        }
    }
}