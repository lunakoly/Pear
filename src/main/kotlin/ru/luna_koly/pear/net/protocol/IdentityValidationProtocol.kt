package ru.luna_koly.pear.net.protocol

import ru.luna_koly.pear.logic.DataBase
import ru.luna_koly.pear.logic.ProfileConnector
import ru.luna_koly.pear.net.connection.Connection
import ru.luna_koly.pear.net.connection.SymmetricProtector
import ru.luna_koly.pear.net.analyzer.Analyser
import ru.luna_koly.pear.util.Logger
import java.security.PublicKey
import java.util.*

/**
 * Implements main functionality
 */
class IdentityValidationProtocol(
    private val dataBase: DataBase,
    private val analyser: Analyser
) : Protocol {
    private val cryptor = dataBase.cryptor

    private fun acceptPublicKey(connection: Connection): PublicKey {
        val othersKey = connection.readBytes()!!
        return cryptor.toPublicKey(othersKey)
    }

    private fun askIdentity(side: String, connection: Connection, othersKey: PublicKey): Boolean {
        val secret = cryptor.generateSecret()
        Logger.log("IdentityValidationProtocol", "$side's secret (${secret.size}) = `${Base64.getEncoder().encodeToString(secret)}`")

        connection.sendBytes(cryptor.encrypt(secret, othersKey))
        Logger.log("IdentityValidationProtocol", "$side sends secret")

        val echoEncryptedSecret = connection.readBytes()!!

        val echoSecret = cryptor.decrypt(echoEncryptedSecret)
        Logger.log("IdentityValidationProtocol", "$side got his secret back")

        Logger.log("IdentityValidationProtocol", "$side's REFLECTED secret (${echoSecret.size}) = `${Base64.getEncoder().encodeToString(echoSecret)}`")

        if (!echoSecret.contentEquals(secret)) {
            Logger.log("IdentityValidationProtocol", "$side's secret is NOT correct")
            return false
        }

        Logger.log("IdentityValidationProtocol", "$side's secret is correct")
        return true
    }

    private fun proveIdentity(side: String, connection: Connection, othersKey: PublicKey) {
        val othersEncryptedSecret = connection.readBytes()!!

        val othersSecret = cryptor.decrypt(othersEncryptedSecret)
        Logger.log("IdentityValidationProtocol", "$side's secret received")

        Logger.log("IdentityValidationProtocol", "$side's RECEIVED secret (${othersSecret.size}) = `${Base64.getEncoder().encodeToString(othersSecret)}`")

        connection.sendBytes(cryptor.encrypt(othersSecret, othersKey))
        Logger.log("IdentityValidationProtocol", "$side's secret is sent back")
    }

    override fun acceptClient(connection: Connection): ProfileConnector? {
        connection.sendBytes(cryptor.getIdentity().encoded)
        Logger.log("Net", "Acceptor sends their encryption key")

        val othersKey = acceptPublicKey(connection)
        Logger.log("Net", "Initiator sent their encryption key and we caught it")

        if (!askIdentity("Acceptor", connection, othersKey))
            return null

        proveIdentity("Initiator", connection, othersKey)

        // add protection layer to the connection
        val protector = SymmetricProtector.generate()
        connection.sendBytes(protector.key.encoded)
        Logger.log("IdentityValidationProtocol", "Sent symmetric key: ${Base64.getEncoder().encodeToString(protector.key.encoded)}")
        connection.protector = protector

        // bind connection with profile
        val profile = dataBase.getProfileFor(othersKey)
        val profileConnector = ProfileConnector(profile, analyser, dataBase)
        profileConnector.lastBoundConnection = connection
        dataBase.addProfileConnector(profileConnector)

        return profileConnector
    }

    override fun acceptServer(connection: Connection): ProfileConnector? {
        val othersKey = acceptPublicKey(connection)
        Logger.log("IdentityValidationProtocol", "Acceptor sent their encryption key and we caught it")

        connection.sendBytes(cryptor.getIdentity().encoded)
        Logger.log("IdentityValidationProtocol", "Initiator sends their encryption key")

        proveIdentity("Acceptor", connection, othersKey)

        if (!askIdentity("Initiator", connection, othersKey))
            return null

        // add protection layer to the connection
        val protectorKey = connection.readBytes()!!
        Logger.log("IdentityValidationProtocol", "Got symmetric key: ${Base64.getEncoder().encodeToString(protectorKey)}")
        val protector = SymmetricProtector.fromByteData(protectorKey)
        connection.protector = protector

        // bind connection with profile
        val profile = dataBase.getProfileFor(othersKey)
        val profileConnector = ProfileConnector(profile, analyser, dataBase)
        profileConnector.lastBoundConnection = connection
        dataBase.addProfileConnector(profileConnector)

        return profileConnector
    }
}