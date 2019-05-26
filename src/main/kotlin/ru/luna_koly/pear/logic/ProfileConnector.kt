package ru.luna_koly.pear.logic

import ru.luna_koly.json.Json
import ru.luna_koly.pear.net.connection.Connection

class ProfileConnector(val profile: Profile) {
    var lastBoundConnection: Connection? = null

    fun send(message: String) {
        lastBoundConnection?.sendString(Json.dictionary {
            item("command", "message")
            item("text", message)
        }.toString())
    }

    override fun toString(): String {
        return profile.toString()
    }
}