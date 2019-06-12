package ru.luna_koly.pear.events

import tornadofx.FXEvent

/**
 * Used by Logger to deliver messages
 * from background thread (net) to the GUI (Pear | Terminal)
 */
data class LogEvent(val message: String) : FXEvent()