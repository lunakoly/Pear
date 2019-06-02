package ru.luna_koly.pear.components

import javafx.scene.Scene
import ru.luna_koly.pear.logic.DataBase
import ru.luna_koly.pear.net.analyzer.RequestAnalyzer
import ru.luna_koly.pear.net.protocol.IdentityValidationProtocol
import ru.luna_koly.pear.util.Logger
import tornadofx.App
import tornadofx.FX
import tornadofx.UIComponent
import tornadofx.find

class TheApp : App(TerminalView::class) {
    override fun createPrimaryScene(view: UIComponent) = Scene(view.root, 800.0, 600.0)

    init {
        // initialize DataBase
        val dataBase = find(DataBase::class)

        // access main request parser
        val analyzer = find(
            RequestAnalyzer::class,
            FX.defaultScope,
            RequestAnalyzer::dataBase to dataBase
        )

        // access main protocol
        val protocol = IdentityValidationProtocol(dataBase, analyzer)

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