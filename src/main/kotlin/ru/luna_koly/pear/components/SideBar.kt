package ru.luna_koly.pear.components

import javafx.geometry.Pos
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
 *
 * TODO: make it a View
 */
class SideBar : OverlayedScrollPane() {
    private var searchConnectorsField: TextField by singleAssign()
    private var connectButton: Button by singleAssign()
    private var connectorsList: VBox by singleAssign()

    /**
     * List of all available ProfileConnectors
     * Contents of connectorsList may very because
     * of sorting
     */
    private val allConnectors = CopyOnWriteArrayList<ProfileConnectorPane>()

    /**
     * The one that we are having an opened ChatPane for
     */
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

                    setOnKeyTyped {
                        synchronized(allConnectors) {
                            connectorsList.clear()

                            allConnectors
                                .filter {
                                    it.profileConnector.profile.name.startsWith(searchConnectorsField.text)
                                }
                                .forEach { connectorsList.add(it) }
                        }
                    }
                }

                // profile connectors go here
                connectorsList = vbox {
                    addClass(SideBarStyle.connectors)
                }
            }
        }
    }

    /**
     * Performed whenever the connection button
     * is pressed.
     *
     * Could be removed if SideBar was a View
     */
    fun action(op: () -> Unit) {
        connectButton.action(op)
    }

    /**
     * Called whenever the profile connector
     * is pressed.
     *
     * Could be removed if SideBar was a View
     */
    private var onConnectorSelected: ((ProfileConnector) -> Unit)? = null

    /**
     * Performed whenever the profile connector
     * is pressed.
     *
     * Could be removed if SideBar was a View
     */
    fun selection(op: (ProfileConnector) -> Unit) {
        onConnectorSelected = op
    }

    /**
     * Adds a new ProfileConnector entry
     * to the list
     */
    fun addProfileConnector(profileConnector: ProfileConnector) {
        val pane = ProfileConnectorPane(profileConnector, this)
        connectorsList += pane

        println("____LOCKING CONNECTORS")
        synchronized(allConnectors) {
            allConnectors.add(pane)
        }
    }

    /**
     * Updates profile connectors to reflect
     * profile info changes
     */
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

    /**
     * Deselects the old connector and
     * selects the new one
     */
    fun selectConnector(newOne: ProfileConnectorPane) {
        selectedConnector?.removeClass(EvenMoreStyles.selectedProfileConnectorPane)
        selectedConnector?.addClass(EvenMoreStyles.profileConnectorPane)
        newOne.removeClass(EvenMoreStyles.profileConnectorPane)
        newOne.addClass(EvenMoreStyles.selectedProfileConnectorPane)
        selectedConnector = newOne

        onConnectorSelected?.invoke(newOne.profileConnector)
    }
}