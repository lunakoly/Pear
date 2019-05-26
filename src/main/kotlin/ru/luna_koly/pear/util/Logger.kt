package ru.luna_koly.pear.util

import ru.luna_koly.pear.events.LogEvent
import ru.luna_koly.pear.ui.TerminalView
import tornadofx.Controller

/**
 * Standard way to output a message.
 *
 * It's marked as Controller() merely
 * to allow interaction with EventBus
 */
object Logger : Controller() {
    private val terminalView: TerminalView by inject()

    init {
        subscribe<LogEvent> {
            terminalView.log(it.message)
        }
    }

    fun log(namespace: String, message: String) {
        fire(LogEvent("$namespace > ${Thread.currentThread().name} > $message"))
    }
}