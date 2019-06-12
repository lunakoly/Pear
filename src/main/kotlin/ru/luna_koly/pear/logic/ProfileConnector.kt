package ru.luna_koly.pear.logic

import ru.luna_koly.pear.net.analyzer.Analyser
import ru.luna_koly.pear.net.connection.Connection

/**
 * Handler for interaction
 * with a certain Profile
 */
class ProfileConnector(
    /**
     * Target profile
     */
    val profile: Profile,
    /**
     * Used to delegate networking
     * functionality to
     */
    val connection: Connection,
    /**
     * Used to delegate networking
     * functionality to
     */
    private val analyzer: Analyser
) {
    /**
     * Sends message to the profile
     */
    fun sendMessage(message: String) {
        analyzer.sendMessage(profile, connection, message)
    }

    /**
     * Updates information about the profile
     */
    fun updateInfo() {
        analyzer.updateInfo(connection)
    }

    /**
     * Notifies profile about the necessity to
     * update their information about the user
     */
    fun sendInfoUpdatedNotification() {
        analyzer.sendInfoUpdatedNotification(connection)
    }

    override fun toString(): String {
        return profile.toString()
    }
}