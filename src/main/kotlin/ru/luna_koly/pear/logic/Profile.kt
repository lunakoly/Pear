package ru.luna_koly.pear.logic

import java.security.PublicKey

/**
 * Represents information about someone
 * on the other side
 */
@Suppress("unused")
class Profile(identity: PublicKey): Person(identity) {
    /**
     * Handy aliases for commonly used
     * ip addresses
     * TODO: implement usage
     */
    val aliases = HashMap<String, String>()

    /**
     * This profile will not be able to
     * send files to the user
     * TODO: implement usage
     */
    var isFileTransferAllowed = true

    /**
     * This profile will not be able to
     * stream media to the user
     * TODO: implement usage
     */
    var isStreamingAllowed = true

    /**
     * This profile will not be able to
     * interact with the user in any way
     * TODO: implement usage
     */
    var isBanned = false
}