package ru.luna_koly.pear.events

import tornadofx.FXEvent
import java.nio.channels.SelectionKey

data class ConnectionEstablishedEvent(var selectionKey: SelectionKey) : FXEvent()