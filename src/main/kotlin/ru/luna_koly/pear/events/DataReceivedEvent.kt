package ru.luna_koly.pear.events

import ru.luna_koly.pear.net.connection.Connection
import tornadofx.FXEvent

data class DataReceivedEvent(val connection: Connection) : FXEvent()