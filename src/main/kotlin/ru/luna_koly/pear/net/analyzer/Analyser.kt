package ru.luna_koly.pear.net.analyzer

import ru.luna_koly.pear.logic.Person
import ru.luna_koly.pear.net.connection.Connection

/**
 * Performs request formatting and analysis
 */
interface Analyser {
    /**
     * Analyzes incoming requests and fires
     * corresponding events
     */
    fun analyze(connection: Connection, author: Person)

    /**
     * Sends formatted string data representing
     * a simple message via the given connection
     */
    fun sendMessage(receiver: Person, connection: Connection, message: String)

    /**
     * Sends a request for getting
     * up to date peer personal info
     */
    fun updateInfo(connection: Connection)

    /**
     * Notifies peer that our personal
     * info has been changed so that they
     * have a reason to send updateInfo() request
     */
    fun sendInfoUpdatedNotification(connection: Connection)
}