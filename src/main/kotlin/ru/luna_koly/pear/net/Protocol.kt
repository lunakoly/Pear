package ru.luna_koly.pear.net

import ru.luna_koly.pear.DataBase
import ru.luna_koly.pear.Logger
import ru.luna_koly.pear.Profile
import java.security.PublicKey

object Protocol {
    private fun acceptPublicKey(connection: Connection): PublicKey {
        val othersKey = connection.readBytes()
        return Cryptor.toPublicKey(othersKey)
    }

    private fun askIdentity(side: String, connection: Connection, othersKey: PublicKey): Boolean {
        val secret = Cryptor.generateSecret()
        Logger.log("Protocol", "$side's secret = `${secret.toString(Charsets.UTF_8)}`")

        connection.sendBytes(Cryptor.encrypt(secret, othersKey))
        Logger.log("Protocol", "$side sends secret")

        val echoEncryptedSecret = connection.readBytes()

        val echoSecret = Cryptor.decrypt(echoEncryptedSecret)
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

        val othersSecret = Cryptor.decrypt(othersEncryptedSecret)
        Logger.log("Protocol", "$side's secret received")

        Logger.log("Protocol", "$side's RECEIVED secret = `${othersSecret.toString(Charsets.UTF_8)}`")

        connection.sendBytes(Cryptor.encrypt(othersSecret, othersKey))
        Logger.log("Protocol", "$side's secret is sent back")
    }

    fun onClientAccepted(connection: Connection): Boolean {
        connection.sendBytes(Cryptor.publicKey.encoded)
        Logger.log("Net", "Acceptor sends their encryption key")

        val othersKey = acceptPublicKey(connection)
        Logger.log("Net", "Initiator sent their encryption key and we caught it")

        if (!askIdentity("Acceptor", connection, othersKey))
            return false

        proveIdentity("Initiator", connection, othersKey)

        val profile = Profile(othersKey)
        profile.setLastBoundConnection(connection)
        DataBase.profiles.add(profile)

        Logger.log("Protocol", "Profile " + (DataBase.profiles.size - 1) + " is registered as Acceptor")
        return true
    }

    fun onServerAccepted(connection: Connection): Boolean {
        val othersKey = acceptPublicKey(connection)
        Logger.log("Protocol", "Acceptor sent their encryption key and we caught it")

        connection.sendBytes(Cryptor.publicKey.encoded)
        Logger.log("Protocol", "Initiator sends their encryption key")

        proveIdentity("Acceptor", connection, othersKey)

        if (!askIdentity("Initiator", connection, othersKey))
            return false

        println("!! 1")

        val profile = Profile(othersKey)
        profile.setLastBoundConnection(connection)
        DataBase.profiles.add(profile)

        println("!! 2")

        Logger.log("Protocol", "Profile " + (DataBase.profiles.size - 1) + " is registered as Initiator")
        return true
    }
}