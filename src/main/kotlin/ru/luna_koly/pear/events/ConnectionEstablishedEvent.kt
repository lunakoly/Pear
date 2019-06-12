package ru.luna_koly.pear.events

import ru.luna_koly.pear.logic.ProfileConnector
import tornadofx.FXEvent

/**
 * Fired when a valid connection is established
 * and peers have already updated info about each other
 */
data class ConnectionEstablishedEvent(var profileConnector: ProfileConnector) : FXEvent()