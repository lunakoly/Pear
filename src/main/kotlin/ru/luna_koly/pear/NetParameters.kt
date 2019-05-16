package ru.luna_koly.pear

fun Int.provide(mask: Int) = this and mask != 0

object NetParameters {
    const val NOTHING = 0
    const val HAS_NEXT = 0b1
}