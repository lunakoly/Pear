package ru.luna_koly.pear.components

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import ru.luna_koly.pear.components.more.EvenMoreStyles
import ru.luna_koly.pear.components.more.OverlayedScrollPane
import ru.luna_koly.pear.components.more.ProfileConnectorPane
import ru.luna_koly.pear.logic.Person
import ru.luna_koly.pear.logic.ProfileConnector
import tornadofx.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Represents menu with profile connectors
 */
class SideBar : OverlayedScrollPane() {
    private var searchConnectorsField: TextField by singleAssign()
    private var connectButton: Button by singleAssign()
    private var connectorsList: VBox by singleAssign()

    private val allConnectors = CopyOnWriteArrayList<ProfileConnectorPane>()
    private var selectedConnector: ProfileConnectorPane? = null

    init {
        addClass(SideBarStyle.sideBar)

        content {
            isFitToWidth = true

            vbox {
                addClass(SideBarStyle.content)
                alignment = Pos.CENTER

                // press to initiate new connection
                connectButton = button("Connect") {
                    addClass(SideBarStyle.connectButton)
                    addClass(Decorations.brightButton)
                }

                // type here to sort profile connectors
                searchConnectorsField = textfield {
                    addClass(SideBarStyle.searchConnectorsField)
                    addClass(Decorations.accurateTextField)

                    promptText = "SEARCH"
                    isFillWidth = false
                }

                // profile connectors go here
                connectorsList = vbox {
                    addClass(SideBarStyle.connectors)
                }
            }
        }
    }

    fun action(op: () -> Unit) {
        connectButton.action(op)
    }

    private var onConnectorSelected: ((ProfileConnector) -> Unit)? = null

    fun selection(op: (ProfileConnector) -> Unit) {
        onConnectorSelected = op
    }

    fun addProfileConnector(profileConnector: ProfileConnector) {
        val pane = ProfileConnectorPane(profileConnector, this)
        allConnectors.add(pane)
        connectorsList += pane
    }

    fun update(person: Person) {
        synchronized(allConnectors) {
            allConnectors
                .filter {
                    it.profileConnector.profile.identity == person.identity
                }
                .forEach {
                    it.update(person)
                }
        }
    }

    fun selectConnector(newOne: ProfileConnectorPane) {
        selectedConnector?.removeClass(EvenMoreStyles.selectedProfileConnectorPane)
        selectedConnector?.addClass(EvenMoreStyles.profileConnectorPane)
        newOne.removeClass(EvenMoreStyles.profileConnectorPane)
        newOne.addClass(EvenMoreStyles.selectedProfileConnectorPane)
        selectedConnector = newOne

        onConnectorSelected?.invoke(newOne.profileConnector)
    }
}