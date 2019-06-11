package ru.luna_koly.pear.events

import ru.luna_koly.pear.logic.Person
import tornadofx.FXEvent

class InfoUpdatedEvent(val person: Person) : FXEvent()