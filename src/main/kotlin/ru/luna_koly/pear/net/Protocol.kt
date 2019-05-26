package ru.luna_koly.pear.net

import ru.luna_koly.pear.logic.DataBase
import ru.luna_koly.pear.util.Logger
import ru.luna_koly.pear.logic.ProfileConnector
import ru.luna_koly.pear.net.connection.Connection
import ru.luna_koly.pear.net.connection.SymmetricProtector
import java.security.PublicKey

object Protocol {
    private fun acceptPublicKey(connection: Connection): PublicKey {
        val othersKey = connection.readBytes()
        return Cryptor.toPublicKey(othersKey)
    }

    private fun askIdentity(side: String, connection: Connection, othersKey: PublicKey): Boolean {
        val secret = Cryptor.generateSecret()
        Logger.log("Protocol", "$side's secret (${secret.size}) = `${secret.toString(Charsets.UTF_8)}`")

        connection.sendBytes(Cryptor.encrypt(secret, othersKey))
        Logger.log("Protocol", "$side sends secret")

        val echoEncryptedSecret = connection.readBytes()

        val echoSecret = Cryptor.decrypt(echoEncryptedSecret)
        Logger.log("Protocol", "$side got his secret back")

        Logger.log("Protocol", "$side's REFLECTED secret (${echoSecret.size}) = `${echoSecret.toString(Charsets.UTF_8)}`")

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

        Logger.log("Protocol", "$side's RECEIVED secret (${othersSecret.size}) = `${othersSecret.toString(Charsets.UTF_8)}`")

        connection.sendBytes(Cryptor.encrypt(othersSecret, othersKey))
        Logger.log("Protocol", "$side's secret is sent back")
    }

    fun acceptClient(connection: Connection): Boolean {
        connection.sendBytes(Cryptor.publicKey.encoded)
//        Logger.log("Net", "Acceptor sends their encryption key")

        val othersKey = acceptPublicKey(connection)
//        Logger.log("Net", "Initiator sent their encryption key and we caught it")

        if (!askIdentity("Acceptor", connection, othersKey))
            return false

        proveIdentity("Initiator", connection, othersKey)

        // add protection layer to the connection
        val protector = SymmetricProtector.generate()
        connection.sendBytes(protector.key.encoded)
        connection.sendBytes(protector.ivSpec.iv)
        connection.protector = protector

        // bind connection with profile
        val profile = DataBase.getProfileFor(othersKey)
        val profileConnector = ProfileConnector(profile)
        profileConnector.setLastBoundConnection(connection)
        DataBase.addProfileConnector(profileConnector)

        Logger.log("Protocol", "ProfileConnector " + (DataBase.profileConnectors.size - 1) + " is registered as Acceptor")
        return true
    }

    fun acceptServer(connection: Connection): Boolean {
        val othersKey = acceptPublicKey(connection)
//        Logger.log("Protocol", "Acceptor sent their encryption key and we caught it")

        connection.sendBytes(Cryptor.publicKey.encoded)
//        Logger.log("Protocol", "Initiator sends their encryption key")

        proveIdentity("Acceptor", connection, othersKey)

        if (!askIdentity("Initiator", connection, othersKey))
            return false

        // add protection layer to the connection
        val protectorKey = connection.readBytes()
        val protectorIv = connection.readBytes()
        val protector = SymmetricProtector.fromByteData(protectorKey, protectorIv)
        connection.protector = protector

        // bind connection with profile
        val profile = DataBase.getProfileFor(othersKey)
        val profileConnector = ProfileConnector(profile)
        profileConnector.setLastBoundConnection(connection)
        DataBase.addProfileConnector(profileConnector)

        Logger.log("Protocol", "ProfileConnector " + (DataBase.profileConnectors.size - 1) + " is registered as Initiator")
        return true
    }
}