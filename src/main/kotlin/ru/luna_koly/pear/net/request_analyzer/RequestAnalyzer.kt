package ru.luna_koly.pear.net.request_analyzer

import ru.luna_koly.json.JsonParser
import ru.luna_koly.pear.events.MessageEvent
import ru.luna_koly.pear.net.connection.Connection
import ru.luna_koly.pear.util.Logger
import tornadofx.Controller
import java.security.GeneralSecurityException

/**
 * Implements main functionality
 */
class RequestAnalyzer : Controller(), Analyser {
    override fun analyze(connection: Connection) {
        // simple message analysis
        try {
            val content = JsonParser.parse(connection.readString())
            fire(MessageEvent(content["text"].value))
        }

        catch (e: JsonParser.SyntaxException) {
            Logger.log("RequestAnalyzer", "Error > Invalid message received")
        }

        catch (e: GeneralSecurityException) {
            Logger.log("RequestAnalyzer", "Error > Encryption error")
        }
    }
}