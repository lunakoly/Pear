package ru.luna_koly.pear.ui

import ru.luna_koly.pear.logic.DataBase
import ru.luna_koly.pear.events.ConnectionRequest
import ru.luna_koly.pear.net.Net
import tornadofx.Controller
import tornadofx.isInt

class TerminalController : Controller() {
    private val view: TerminalView by inject()

    private fun tryRaiseConnection(args: List<String>) {
        when {
            args.size < 2 -> view.log("Usage > connect <address> [port]")
            args.size < 3 -> fire(ConnectionRequest(args[1]))
            else -> fire(ConnectionRequest(args[1], args[2].toIntOrNull() ?: Net.DEFAULT_PORT))
        }
    }

    private fun list(args: List<String>) {
        when {
            args.size < 2 -> view.log("Usage > list (profiles | connectors)")

            args[1] == "profiles" -> {
                synchronized(DataBase.profiles) {
                    view.log(
                        DataBase.profiles
                            .mapIndexed { index, it -> "$index - $it" }
                            .joinToString("\n")
                    )
                }
            }

            args[1] == "connectors" -> {
                synchronized(DataBase.profileConnectors) {
                    view.log(
                        DataBase.profileConnectors
                            .mapIndexed { index, it -> "$index - $it" }
                            .joinToString("\n")
                    )
                }
            }

            else -> {
                view.log("Error > ${args[1]} is not a proper target")
            }
        }
    }

    private fun trySend(args: List<String>) {
        when {
            args.size < 2 -> view.log("Usage > send <profile id> word1 [,word2...]")
            !args[1].isInt() -> view.log("Error > profile id must be an integer")
            args.size < 3 -> view.log("Error > message is required")

            else -> {
                val id = args[1].toInt()
                val profile = DataBase.profileConnectors.getOrNull(id)

                if (profile == null) {
                    view.log("Error > $id is not a valid profile id")
                    return
                }

                val message = args.subList(2, args.size).joinToString(" ")
                profile.send(message)
            }
        }
    }

    fun proceed(command: String) {
        val args = command
            .split(Regex("\\s+"))
            .filter { !it.isBlank() }

        if (args.isEmpty())
            return

        when (args[0]) {
            "connect" -> tryRaiseConnection(args)
            "list" -> list(args)
            "send" -> trySend(args)
            "exit" -> view.close()
            else -> view.log("Could not find command `${args[0]}`")
        }
    }
}