package ru.luna_koly.pear.net.request_analyzer

import ru.luna_koly.pear.net.connection.Connection

/**
 * Performs request formatting and analysis
 */
interface Analyser {
    /**
     * Analyzes incoming requests and fires
     * corresponding events
     */
    fun analyze(connection: Connection)
}