package ru.luna_koly.pear

import ru.luna_koly.pear.json.Json
import ru.luna_koly.pear.net.Connection
import java.security.PublicKey

class Profile(val identity: PublicKey) {
    private var lastBoundConnection: Connection? = null

    fun setLastBoundConnection(connection: Connection) {
        lastBoundConnection = connection
    }

    fun send(message: String) {
        lastBoundConnection?.sendString(Json.Dictionary {
            item("command", "message")
            item("text", message)
        }.toString())
    }

    override fun toString(): String {
        return "Profile {${
            identity.encoded
                .toString(Charsets.UTF_8)
                .substring(0, 10)
                .replace("\n", "")
        }}"
    }
}