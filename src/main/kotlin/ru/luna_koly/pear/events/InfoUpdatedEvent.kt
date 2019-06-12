package ru.luna_koly.pear.events

import ru.luna_koly.pear.logic.Person
import tornadofx.FXEvent

/**
 * Called when a new info about someone
 * is available
 */
class InfoUpdatedEvent(val person: Person) : FXEvent()