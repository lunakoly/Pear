package ru.luna_koly.pear.events

import ru.luna_koly.pear.logic.Person
import tornadofx.FXEvent

/**
 * Fired by DataBase to notify the GUI
 * about the necessity to refresh available messages
 */
class UpdateMessagesEvent(val person: Person) : FXEvent()