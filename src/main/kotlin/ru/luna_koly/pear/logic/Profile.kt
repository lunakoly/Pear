package ru.luna_koly.pear.logic

import java.security.PublicKey

/**
 * Represents information about someone
 * on the other side
 */
class Profile(identity: PublicKey): Person(identity) {
    val aliases = HashMap<String, String>()

    var isFileTransferAllowed = true
    var isStreamingAllowed = true
    var isBanned = false
}