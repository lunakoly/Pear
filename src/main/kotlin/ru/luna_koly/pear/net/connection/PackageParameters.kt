package ru.luna_koly.pear.net.connection

/**
 * Returns true if the given flag is
 * presented inside the number
 */
fun Int.provide(mask: Int) = this and mask != 0

/**
 * Defines flags required for
 * network communication
 */
object PackageParameters {
    /**
     * All flags are zero
     */
    const val NOTHING = 0
    /**
     * Next chunk is also a part of the
     * current message
     */
    const val HAS_NEXT = 0b1
}