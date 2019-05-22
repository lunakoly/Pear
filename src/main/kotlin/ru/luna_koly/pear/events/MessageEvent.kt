package ru.luna_koly.pear.events

import tornadofx.FXEvent

data class MessageEvent(val text: String = ""): FXEvent()