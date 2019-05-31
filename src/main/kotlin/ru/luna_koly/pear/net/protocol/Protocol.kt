package ru.luna_koly.pear.net.protocol

import ru.luna_koly.pear.logic.ProfileConnector
import ru.luna_koly.pear.net.connection.Connection

/**
 * Does everything to guarantee the connection
 * is ready for sending "user-level" requests
 */
interface Protocol {
    /**
     * Called on server side to accept a new
     * incoming connection
     */
    fun acceptClient(connection: Connection): ProfileConnector?

    /**
     * Called on client side to configure
     * outgoing connection
     */
    fun acceptServer(connection: Connection): ProfileConnector?
}