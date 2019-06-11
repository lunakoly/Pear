package ru.luna_koly.pear.events

import ru.luna_koly.pear.logic.Person
import tornadofx.FXEvent

class UpdateMessagesEvent(val person: Person) : FXEvent()