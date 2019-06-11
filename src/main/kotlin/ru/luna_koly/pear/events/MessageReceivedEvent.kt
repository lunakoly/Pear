package ru.luna_koly.pear.events

import ru.luna_koly.pear.logic.Message
import tornadofx.FXEvent

data class MessageReceivedEvent(val message: Message): FXEvent()