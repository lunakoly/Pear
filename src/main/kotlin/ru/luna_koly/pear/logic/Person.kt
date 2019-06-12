package ru.luna_koly.pear.logic

import java.security.PublicKey

/**
 * Describes `atomic` user data.
 * This class can be applied to both
 * dedicated people described via profiles
 * and the pear user
 */
@Suppress("unused")
open class Person(
    /**
     * Public key used to identify anyone
     */
    val identity: PublicKey,
    /**
     * Nik
     */
    var name: String = "Someone",
    /**
     * Description
     */
    var info: String = "",
    /**
     * Path to the location of avatar image
     * on local drive
     * TODO: implement usage
     */
    var avatarLocation: String = ""
) {
    /**
     * Persons are equal when and only when
     * their public keys are equal
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Person

        if (identity != other.identity)
            return false

        return true
    }

    override fun hashCode(): Int {
        return identity.hashCode()
    }

    override fun toString(): String {
        return "$name (${identity.encoded.toString(Charsets.UTF_8).replace("\n", "").take(10)})"
    }
}