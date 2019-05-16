package ru.luna_koly.pear.events

import tornadofx.FXEvent

data class LogEvent(val message: String) : FXEvent()