package ru.luna_koly.pear.net.analyzer

import ru.luna_koly.json.Json
import ru.luna_koly.json.JsonParser
import ru.luna_koly.pear.events.MessageEvent
import ru.luna_koly.pear.logic.DataBase
import ru.luna_koly.pear.logic.Person
import ru.luna_koly.pear.net.connection.Connection
import ru.luna_koly.pear.util.Logger
import tornadofx.Controller
import java.security.GeneralSecurityException

/**
 * Implements main functionality
 */
class RequestAnalyzer : Controller(), Analyser {
    companion object {
        private const val TASK = "task"
        private const val MESSAGE = "message"
        private const val INFO_UPDATE = "info_update"
        private const val DESCRIPTION = "description"
        private const val INFO_UPDATED_NOTIFICATION = "info_updated_notification"
    }

    val dataBase: DataBase by params

    override fun analyze(connection: Connection, author: Person) {
        try {
            val data = connection.readString()
            val content = JsonParser.parse(data)

            when(content[TASK].value) {
                // simple message analysis
                MESSAGE -> onMessage(content, author)

                // info update
                INFO_UPDATE -> onInfoUpdate(connection)

                // info update response
                DESCRIPTION -> onDescription(content, author)

                // when they have changed their name
                // or something, we can request changes
                INFO_UPDATED_NOTIFICATION -> onInfoUpdatedNotification(connection)
            }
        }

        catch (e: JsonParser.SyntaxException) {
            Logger.log("RequestAnalyzer", "Error > Invalid message received")
            println("Caused at: $connection")
            e.printStackTrace()
        }

        catch (e: ClassCastException) {
            Logger.log("RequestAnalyzer", "Error > Invalid message received")
            println("Caused at: $connection")
            e.printStackTrace()
        }

        catch (e: GeneralSecurityException) {
            Logger.log("RequestAnalyzer", "Error > Encryption error")
            println("Caused at: $connection")
            e.printStackTrace()
        }
    }

    private fun onMessage(content: Json.Object, author: Person) {
        fire(MessageEvent(author, content["text"].value))
    }

    private fun onInfoUpdate(connection: Connection) {
        Logger.log("RequestAnalyzer", "Got INFO_UPDATE, sending data")
        connection.sendString(Json.dictionary {
            item(TASK, DESCRIPTION)
            item("name", dataBase.user.name)
            item("info", dataBase.user.info)
        }.toString())
    }

    private fun onDescription(content: Json.Object, author: Person) {
        Logger.log("RequestAnalyzer", "Got DESCRIPTION, applying data")
        val name = content["name"].value
        val info = content["info"].value
        author.name = name
        author.info = info
    }

    private fun onInfoUpdatedNotification(connection: Connection) {
        Logger.log("RequestAnalyzer", "Got notified about changed info")
        updateInfo(connection)
    }

    override fun sendMessage(connection: Connection, message: String) {
        connection.sendString(Json.dictionary {
            item(TASK, MESSAGE)
            item("text", message)
        }.toString())
    }

    override fun updateInfo(connection: Connection) {
        Logger.log("RequestAnalyzer", "Asking for up-to-date info")
        connection.sendString(Json.dictionary {
            item(TASK, INFO_UPDATE)
        }.toString())
    }

    override fun sendInfoUpdatedNotification(connection: Connection) {
        Logger.log("RequestAnalyzer", "Sending info updated notification")
        connection.sendString(Json.dictionary {
            item(TASK, INFO_UPDATED_NOTIFICATION)
        }.toString())
    }
}