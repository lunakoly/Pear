package ru.luna_koly.pear.events

import ru.luna_koly.pear.logic.Person
import tornadofx.FXEvent

data class MessageEvent(val author: Person, val text: String = ""): FXEvent()