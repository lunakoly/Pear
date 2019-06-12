package ru.luna_koly.pear.events

import ru.luna_koly.pear.components.Net
import tornadofx.EventBus
import tornadofx.FXEvent

/**
 * Requests establishing connection
 * with the given address
 */
data class ConnectionRequest(
    var address: String,
    var port: Int = Net.DEFAULT_PORT
) : FXEvent(EventBus.RunOn.BackgroundThread)