package ru.luna_koly.pear.net.protocol

import ru.luna_koly.pear.logic.DataBase
import ru.luna_koly.pear.logic.ProfileConnector
import ru.luna_koly.pear.net.connection.Connection
import ru.luna_koly.pear.net.connection.SymmetricProtector
import ru.luna_koly.pear.net.analyzer.Analyser
import ru.luna_koly.pear.util.Logger
import java.security.PublicKey

/**
 * Implements main functionality
 */
class IdentityValidationProtocol(
    private val dataBase: DataBase,
    private val analyser: Analyser
) : Protocol {
    private val cryptor = dataBase.cryptor

    private fun acceptPublicKey(connection: Connection): PublicKey {
        val othersKey = connection.readBytes()
        return cryptor.toPublicKey(othersKey)
    }

    private fun askIdentity(connection: Connection, othersKey: PublicKey): Boolean {
        val secret = cryptor.generateSecret()
        connection.sendBytes(cryptor.encrypt(secret, othersKey))
        val echoEncryptedSecret = connection.readBytes()
        val echoSecret = cryptor.decrypt(echoEncryptedSecret)

        if (!echoSecret.contentEquals(secret))
            return false

        return true
    }

    private fun proveIdentity(connection: Connection, othersKey: PublicKey) {
        val othersEncryptedSecret = connection.readBytes()
        val othersSecret = cryptor.decrypt(othersEncryptedSecret)
        connection.sendBytes(cryptor.encrypt(othersSecret, othersKey))
    }

    override fun acceptClient(connection: Connection): ProfileConnector? {
        connection.sendBytes(cryptor.getIdentity().encoded)
        val othersKey = acceptPublicKey(connection)

        if (!askIdentity(connection, othersKey))
            return null

        proveIdentity(connection, othersKey)

        // add protection layer to the connection
        val protector = SymmetricProtector.generate()
        connection.sendBytes(protector.key.encoded)
        connection.protector = protector

        // bind connection with profile
        val profile = dataBase.getProfileFor(othersKey)
        val profileConnector = ProfileConnector(profile, analyser)
        profileConnector.lastBoundConnection = connection
        dataBase.addProfileConnector(profileConnector)

        Logger.log("IdentityValidationProtocol", "ProfileConnector " + (dataBase.profileConnectors.size - 1) + " is registered as Acceptor")
        return profileConnector
    }

    override fun acceptServer(connection: Connection): ProfileConnector? {
        val othersKey = acceptPublicKey(connection)
        connection.sendBytes(cryptor.getIdentity().encoded)

        proveIdentity(connection, othersKey)

        if (!askIdentity(connection, othersKey))
            return null

        // add protection layer to the connection
        val protectorKey = connection.readBytes()
        val protector = SymmetricProtector.fromByteData(protectorKey)
        connection.protector = protector

        // bind connection with profile
        val profile = dataBase.getProfileFor(othersKey)
        val profileConnector = ProfileConnector(profile, analyser)
        profileConnector.lastBoundConnection = connection
        dataBase.addProfileConnector(profileConnector)

        Logger.log("IdentityValidationProtocol", "ProfileConnector " + (dataBase.profileConnectors.size - 1) + " is registered as Initiator")
        return profileConnector
    }
}