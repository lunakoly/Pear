package ru.luna_koly.pear.components

import ru.luna_koly.pear.util.Logger
import tornadofx.App
import tornadofx.find

class TheApp : App(TerminalView::class) {
    init {
        // initialize Net component and
        // start background request handler
        // to catch incoming events
        val netThread = Thread(find(Net::class))
        netThread.name = "Net Thread"
        netThread.isDaemon = true
        netThread.start()

        Logger.log("UI", "Starting Net...")
    }
}