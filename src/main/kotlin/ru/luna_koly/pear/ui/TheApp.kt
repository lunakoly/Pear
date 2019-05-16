package ru.luna_koly.pear.ui

import ru.luna_koly.pear.Logger
import ru.luna_koly.pear.Net
import ru.luna_koly.pear.events.ServerStartRequest
import tornadofx.App
import tornadofx.find

class TheApp : App(TerminalView::class) {
    init {
        // initialize Net component and
        // start background request handler
        // to catch incoming events
        find(Net::class)
        fire(ServerStartRequest)
        Logger.log("UI", "Starting Net...")
    }
}