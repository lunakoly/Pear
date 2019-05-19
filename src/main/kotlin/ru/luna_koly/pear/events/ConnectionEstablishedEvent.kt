package ru.luna_koly.pear.events

import ru.luna_koly.pear.net.Connection
import tornadofx.FXEvent

data class ConnectionEstablishedEvent(var connection: Connection) : FXEvent()