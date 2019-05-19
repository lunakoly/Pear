package ru.luna_koly.pear.net

import ru.luna_koly.pear.Logger
import ru.luna_koly.pear.Profiler
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import kotlin.random.Random

object Protocol {
    private val cipher: Cipher = Cipher.getInstance("RSA")

    private fun generateSecret() = Random.nextBytes(20)

    private fun acceptPublicKey(connection: Connection): PublicKey {
        val othersKey = connection.readBytes()
        return KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(othersKey))
    }

    private fun encrypt(data: ByteArray, publicKey: PublicKey): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    private fun decrypt(data: ByteArray, privateKey: PrivateKey): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(data)
    }

    private fun askIdentity(side: String, connection: Connection, othersKey: PublicKey): Boolean {
        val secret = generateSecret()
        Logger.log("Protocol", "$side's secret = `${secret.toString(Charsets.UTF_8)}`")

        connection.sendBytes(encrypt(secret, othersKey))
        Logger.log("Protocol", "$side sends secret")

        val echoEncryptedSecret = connection.readBytes()

        val echoSecret = decrypt(echoEncryptedSecret, Profiler.keyPair.private)
        Logger.log("Protocol", "$side got his secret back")

        Logger.log("Protocol", "$side's REFLECTED secret = `${echoSecret.toString(Charsets.UTF_8)}`")

        if (!echoSecret.contentEquals(secret)) {
            Logger.log("Protocol", "$side's secret is NOT correct")
            return false
        }

        Logger.log("Protocol", "$side's secret is correct")
        return true
    }

    private fun proveIdentity(side: String, connection: Connection, othersKey: PublicKey) {
        val othersEncryptedSecret = connection.readBytes()

        val othersSecret = decrypt(othersEncryptedSecret, Profiler.keyPair.private)
        Logger.log("Protocol", "$side's secret received")

        Logger.log("Protocol", "$side's RECEIVED secret = `${othersSecret.toString(Charsets.UTF_8)}`")

        connection.sendBytes(encrypt(othersSecret, othersKey))
        Logger.log("Protocol", "$side's secret is sent back")
    }

    fun onClientAccepted(connection: Connection) {
        connection.sendBytes(Profiler.keyPair.public.encoded)
        Logger.log("Net", "Acceptor sends their encryption key")

        val othersKey = acceptPublicKey(connection)
        Logger.log("Net", "Initiator sent their encryption key and we caught it")

        if (!askIdentity("Acceptor", connection, othersKey))
            return

        proveIdentity("Initiator", connection, othersKey)
    }

    fun onServerAccepted(connection: Connection) {
        val othersKey = acceptPublicKey(connection)
        Logger.log("Protocol", "Acceptor sent their encryption key and we caught it")

        connection.sendBytes(Profiler.keyPair.public.encoded)
        Logger.log("Protocol", "Initiator sends their encryption key")

        proveIdentity("Acceptor", connection, othersKey)

        if (!askIdentity("Initiator", connection, othersKey))
            return
    }
}