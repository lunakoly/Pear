package ru.luna_koly.pear.util

import ru.luna_koly.pear.events.LogEvent
import ru.luna_koly.pear.components.debug.TerminalView
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

    /**
     * Writes message to stdout & Pear | Terminal
     */
    fun log(namespace: String, message: String) {
        synchronized(System.out) {
            println("$namespace > ${Thread.currentThread().name} > $message")
        }
        fire(LogEvent("$namespace > ${Thread.currentThread().name} > $message"))
    }
}