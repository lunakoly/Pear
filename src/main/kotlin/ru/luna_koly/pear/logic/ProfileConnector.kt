package ru.luna_koly.pear.logic

import ru.luna_koly.pear.net.analyzer.Analyser
import ru.luna_koly.pear.net.connection.Connection

class ProfileConnector(
    val profile: Profile,
    private val analyzer: Analyser,
    private val dataBase: DataBase
) {
    var lastBoundConnection: Connection? = null

    fun sendMessage(message: String) {
        lastBoundConnection?.let {
            analyzer.sendMessage(it, message)
            dataBase.addMessage(Message(profile, message, true))
        }
    }

    fun updateInfo() {
        lastBoundConnection?.let {
            analyzer.updateInfo(it)
        }
    }

    fun sendInfoUpdatedNotification() {
        lastBoundConnection?.let {
            analyzer.sendInfoUpdatedNotification(it)
        }
    }

    override fun toString(): String {
        return profile.toString()
    }
}