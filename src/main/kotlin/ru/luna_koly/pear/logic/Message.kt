package ru.luna_koly.pear.logic

/**
 * A simple message.
 *
 * TODO: isOurs is needed due to code issues with GUI (SideBar & ChatPane are not Views)
 */
class Message(val author: Person, val text: String = "", val isOurs: Boolean = false)