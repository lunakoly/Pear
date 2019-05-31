package ru.luna_koly.pear.net.connection

fun Int.provide(mask: Int) = this and mask != 0

object PackageParameters {
    const val NOTHING = 0
    const val HAS_NEXT = 0b1
}