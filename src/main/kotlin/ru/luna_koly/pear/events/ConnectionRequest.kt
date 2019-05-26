package ru.luna_koly.pear.events

import ru.luna_koly.pear.components.Net
import tornadofx.EventBus
import tornadofx.FXEvent

data class ConnectionRequest(
    var address: String,
    var port: Int = Net.DEFAULT_PORT
) : FXEvent(EventBus.RunOn.BackgroundThread)