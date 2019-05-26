package ru.luna_koly.pear.logic

import java.security.PublicKey

/**
 * Represents information about someone
 * on the other side
 */
data class Profile(val identity: PublicKey) {
    var name = ""
    var info = ""
    var avatarLocation = ""

    val aliases = HashMap<String, String>()

    var isFileTransferAllowed = true
    var isStreamingAllowed = true
    var isBanned = false


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Profile

        if (identity != other.identity)
            return false

        return true
    }

    override fun hashCode(): Int {
        return identity.hashCode()
    }
}