package ru.luna_koly.pear.components.debug

import ru.luna_koly.pear.components.Net
import ru.luna_koly.pear.logic.DataBase
import ru.luna_koly.pear.events.ConnectionRequest
import tornadofx.Controller
import tornadofx.isInt

/**
 * Defines commands for Pear | Terminal
 */
class TerminalController : Controller() {
    private val view: TerminalView by inject()
    private val dataBase: DataBase by inject()

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
                synchronized(dataBase.getProfiles()) {
                    view.log(
                        dataBase.getProfiles()
                            .mapIndexed { index, it -> "[$index]\t$it" }
                            .joinToString("\n")
                    )
                }
            }

            args[1] == "connectors" -> {
                synchronized(dataBase.getProfileConnectors()) {
                    view.log(
                        dataBase.getProfileConnectors()
                            .mapIndexed { index, it -> "[$index]\t$it" }
                            .joinToString("\n")
                    )
                }
            }

            else -> {
                view.log("Error > ${args[1]} is not a proper target")
            }
        }
    }

    private fun send(args: List<String>) {
        when {
            args.size < 2 -> view.log("Usage > send <profile id> word1 [,word2...]")
            !args[1].isInt() -> view.log("Error > profile id must be an integer")
            args.size < 3 -> view.log("Error > message is required")

            else -> {
                val id = args[1].toInt()
                val profile = dataBase.getProfileConnectors().getOrNull(id)

                if (profile == null) {
                    view.log("Error > $id is not a valid profile id")
                    return
                }

                val message = args.subList(2, args.size).joinToString(" ")
                profile.sendMessage(message)
            }
        }
    }

    private fun set(args: List<String>) {
        when {
            args.size < 3 -> view.log("Usage > set <field> <value>")

            args[1] == "topBarButton" -> {
                dataBase.user.name = args[2]
                dataBase.getProfileConnectors().forEach {
                    it.sendInfoUpdatedNotification()
                }
            }

            args[1] == "info" -> {
                dataBase.user.info = args[2]
                dataBase.getProfileConnectors().forEach {
                    it.sendInfoUpdatedNotification()
                }
            }

            else -> view.log("Error > field ${args[1]} does not exist")
        }
    }

    private fun update(args: List<String>) {
        when {
            args.size < 2 -> view.log("Usage > update info <profile id>")

            args[1] == "info" -> {
                if (args.size < 3)
                    view.log("Usage > update info <profile id>")
                else {
                    dataBase.getProfileConnectors().getOrNull(args[2].toInt())?.updateInfo()
                }
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
            "send" -> send(args)
            "set" -> set(args)
            "update" -> update(args)
            "exit" -> view.close()
            else -> view.log("Could not find command `${args[0]}`")
        }
    }
}