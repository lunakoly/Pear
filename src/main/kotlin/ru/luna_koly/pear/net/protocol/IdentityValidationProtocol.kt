package ru.luna_koly.pear.net.protocol

import ru.luna_koly.pear.logic.DataBase
import ru.luna_koly.pear.logic.ProfileConnector
import ru.luna_koly.pear.net.connection.Connection
import ru.luna_koly.pear.net.connection.SymmetricProtector
import ru.luna_koly.pear.util.Logger
import java.security.PublicKey

/**
 * Implements main functionality
 */
class IdentityValidationProtocol(private val dataBase: DataBase) : Protocol {
    private val cryptor = dataBase.cryptor

    private fun acceptPublicKey(connection: Connection): PublicKey {
        val othersKey = connection.readBytes()
        return cryptor.toPublicKey(othersKey)
    }

    private fun askIdentity(side: String, connection: Connection, othersKey: PublicKey): Boolean {
        val secret = cryptor.generateSecret()
        Logger.log("IdentityValidationProtocol", "$side's secret (${secret.size}) = `${secret.toString(Charsets.UTF_8)}`")

        connection.sendBytes(cryptor.encrypt(secret, othersKey))
        Logger.log("IdentityValidationProtocol", "$side sends secret")

        val echoEncryptedSecret = connection.readBytes()

        val echoSecret = cryptor.decrypt(echoEncryptedSecret)
        Logger.log("IdentityValidationProtocol", "$side got his secret back")

        Logger.log("IdentityValidationProtocol", "$side's REFLECTED secret (${echoSecret.size}) = `${echoSecret.toString(Charsets.UTF_8)}`")

        if (!echoSecret.contentEquals(secret)) {
            Logger.log("IdentityValidationProtocol", "$side's secret is NOT correct")
            return false
        }

        Logger.log("IdentityValidationProtocol", "$side's secret is correct")
        return true
    }

    private fun proveIdentity(side: String, connection: Connection, othersKey: PublicKey) {
        val othersEncryptedSecret = connection.readBytes()

        val othersSecret = cryptor.decrypt(othersEncryptedSecret)
        Logger.log("IdentityValidationProtocol", "$side's secret received")

        Logger.log("IdentityValidationProtocol", "$side's RECEIVED secret (${othersSecret.size}) = `${othersSecret.toString(Charsets.UTF_8)}`")

        connection.sendBytes(cryptor.encrypt(othersSecret, othersKey))
        Logger.log("IdentityValidationProtocol", "$side's secret is sent back")
    }

    override fun acceptClient(connection: Connection): ProfileConnector? {
        connection.sendBytes(cryptor.getIdentity().encoded)
//        Logger.log("Net", "Acceptor sends their encryption key")

        val othersKey = acceptPublicKey(connection)
//        Logger.log("Net", "Initiator sent their encryption key and we caught it")

        if (!askIdentity("Acceptor", connection, othersKey))
            return null

        proveIdentity("Initiator", connection, othersKey)

        // add protection layer to the connection
        val protector = SymmetricProtector.generate()
        connection.sendBytes(protector.key.encoded)
        connection.sendBytes(protector.ivSpec.iv)
        connection.protector = protector

        // bind connection with profile
        val profile = dataBase.getProfileFor(othersKey)
        val profileConnector = ProfileConnector(profile)
        profileConnector.lastBoundConnection = connection
        dataBase.addProfileConnector(profileConnector)

        Logger.log("IdentityValidationProtocol", "ProfileConnector " + (dataBase.profileConnectors.size - 1) + " is registered as Acceptor")
        return profileConnector
    }

    override fun acceptServer(connection: Connection): ProfileConnector? {
        val othersKey = acceptPublicKey(connection)
//        Logger.log("IdentityValidationProtocol", "Acceptor sent their encryption key and we caught it")

        connection.sendBytes(cryptor.getIdentity().encoded)
//        Logger.log("IdentityValidationProtocol", "Initiator sends their encryption key")

        proveIdentity("Acceptor", connection, othersKey)

        if (!askIdentity("Initiator", connection, othersKey))
            return null

        // add protection layer to the connection
        val protectorKey = connection.readBytes()
        val protectorIv = connection.readBytes()
        val protector = SymmetricProtector.fromByteData(protectorKey, protectorIv)
        connection.protector = protector

        // bind connection with profile
        val profile = dataBase.getProfileFor(othersKey)
        val profileConnector = ProfileConnector(profile)
        profileConnector.lastBoundConnection = connection
        dataBase.addProfileConnector(profileConnector)

        Logger.log("IdentityValidationProtocol", "ProfileConnector " + (dataBase.profileConnectors.size - 1) + " is registered as Initiator")
        return profileConnector
    }
}