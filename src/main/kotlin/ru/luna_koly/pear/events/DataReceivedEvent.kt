package ru.luna_koly.pear.events

import tornadofx.FXEvent

data class DataReceivedEvent(val data: ByteArray) : FXEvent() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataReceivedEvent

        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }
}