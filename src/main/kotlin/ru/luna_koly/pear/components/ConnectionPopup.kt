package ru.luna_koly.pear.components

import ru.luna_koly.pear.components.more.TitledTextField
import ru.luna_koly.pear.components.more.hintbar
import ru.luna_koly.pear.components.more.titledtextfield
import ru.luna_koly.pear.events.ConnectionEstablishedEvent
import ru.luna_koly.pear.events.ConnectionRequest
import ru.luna_koly.pear.events.InvalidAddressEvent
import tornadofx.*

/**
 * Asks user to type the target ip address
 */
class ConnectionPopup : Fragment() {
    /**
     * Where the user types their target address
     */
    private var addressField: TitledTextField by singleAssign()

    override val root = anchorpane {
        prefHeight = 400.0
        prefWidth = 600.0

        // if error occurred, it'll be logged here
        val status = label("Hit ENTER!") {
            addClass(Decorations.status)

            anchorpaneConstraints {
                topAnchor = 200.0
                leftAnchor = 15.0
                rightAnchor = 15.0
            }
        }

        // type ip address here
        addressField = titledtextfield("IP address: ", "127.0.0.1") {
            anchorpaneConstraints {
                topAnchor = 15.0
                leftAnchor = 15.0
                rightAnchor = 15.0
            }

            inner.action {
                val target = if (status.text.isBlank()) "127.0.0.1" else inner.text
                inner.isDisable = true
                status.text = "Connecting..."
                fire(ConnectionRequest(target))
            }
        }

        hintbar {
            hint {
                label("ENTER") { addClass(Decorations.key) }
                label(" to select") { addClass(Decorations.hint) }
            }

            hint {
                label("ESC") { addClass(Decorations.key) }
                label(" to cancel") { addClass(Decorations.hint) }
            }
        }

        subscribe<InvalidAddressEvent> {
            status.text = "Could'n not connect to the specified address"
            addressField.inner.isDisable = false
        }

        subscribe<ConnectionEstablishedEvent> {
            // Hopefully, that'l do th trick with EventBus
            // I suppose that ConcurrentModificationException
            // is thrown if I call close() here.
            // TODO: Is it a bug in TornadoFX?
            status.text = "Done!"
        }
    }
}