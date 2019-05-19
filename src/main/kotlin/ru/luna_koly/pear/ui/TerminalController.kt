package ru.luna_koly.pear.ui

import ru.luna_koly.pear.net.Net
import ru.luna_koly.pear.events.ConnectionRequest
import tornadofx.Controller

class TerminalController : Controller() {
    private val view: TerminalView by inject()

    private fun tryRaiseConnection(args: List<String>) {
        when {
            args.size < 2 -> view.log("Usage > connect <address> [port]")
            args.size < 3 -> fire(ConnectionRequest(args[1]))
            else -> fire(ConnectionRequest(args[1], args[2].toIntOrNull() ?: Net.DEFAULT_PORT))
        }
    }

    private fun trySend(args: List<String>) {

    }

    fun proceed(command: String) {
        val args = command
            .split(Regex("\\s+"))
            .filter { !it.isBlank() }

        if (args.isEmpty())
            return

        when (args[0]) {
            "connect" -> tryRaiseConnection(args)
            "send" -> trySend(args)
            "exit" -> view.close()
            else -> view.log("Could not find command `${args[0]}`")
        }
    }
}