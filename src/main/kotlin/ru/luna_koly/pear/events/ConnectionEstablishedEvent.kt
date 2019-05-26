package ru.luna_koly.pear.events

import ru.luna_koly.pear.logic.ProfileConnector
import tornadofx.FXEvent

data class ConnectionEstablishedEvent(var profileConnector: ProfileConnector) : FXEvent()