package ru.luna_koly.pear.components

import ru.luna_koly.pear.logic.DataBase
import ru.luna_koly.pear.net.protocol.IdentityValidationProtocol
import ru.luna_koly.pear.net.request_analyzer.RequestAnalyzer
import ru.luna_koly.pear.util.Logger
import tornadofx.App
import tornadofx.FX
import tornadofx.find

class TheApp : App(TerminalView::class) {
    init {
        // initialize DataBase
        val dataBase = find(DataBase::class)

        // access main protocol
        val protocol = IdentityValidationProtocol(dataBase)

        // access main request parser
        val analyzer = find(RequestAnalyzer::class)

        // initialize Net component
        val net = find(
            Net::class,
            FX.defaultScope,
            Net::analyzer to analyzer,
            Net::protocol to protocol
        )

        // start background request handler
        // to catch incoming events
        val netThread = Thread(net)
        netThread.name = "Net Thread"
        netThread.isDaemon = true
        netThread.start()

        Logger.log("UI", "Starting Net...")
    }
}