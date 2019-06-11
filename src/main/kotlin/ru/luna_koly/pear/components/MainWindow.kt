package ru.luna_koly.pear.components

import javafx.scene.layout.VBox
import javafx.scene.text.Font
import ru.luna_koly.pear.components.more.chatpane
import ru.luna_koly.pear.components.more.sidebar
import ru.luna_koly.pear.events.ConnectionEstablishedEvent
import ru.luna_koly.pear.events.InfoUpdatedEvent
import ru.luna_koly.pear.events.MessageReceivedEvent
import ru.luna_koly.pear.events.UpdateMessagesEvent
import ru.luna_koly.pear.logic.DataBase
import tornadofx.*

/**
 * The main and the only window user
 * will interact with
 */
class MainWindow : View("Pear") {
    private val dataBase: DataBase by inject()

    private var sideBar: SideBar by singleAssign()
    private var chatPane: ChatPane by singleAssign()

    override val root = borderpane {
        left {
            sideBar = sidebar {
                action {
                    openInternalWindow<ConnectionPopup>(movable = false)
                }

                subscribe<ConnectionEstablishedEvent> {
                    addProfileConnector(it.profileConnector)
                }
            }
        }

        center {
            chatPane = chatpane {
                sideBar.selection {
                    chatPane.selectConnector(it)
                    chatPane.refresh(dataBase.getMessagesFor(it.profile))
                }

                settings {
                    openInternalWindow<SettingsPopup>(movable = false)
                }

                profileInfo {
                    openInternalWindow<ProfileInfoPopup>(
                        params = mapOf(
                            ProfileInfoPopup::profile to it
                        ),
                        movable = false
                    )
                }
            }
        }

        subscribe<InfoUpdatedEvent> {
            sideBar.update(it.person)
            chatPane.update(it.person)
            chatPane.refresh(dataBase.getMessagesFor(it.person))
        }

        subscribe<MessageReceivedEvent> {
            dataBase.addMessage(it.message)
        }

        subscribe<UpdateMessagesEvent> {
            chatPane.refresh(dataBase.getMessagesFor(it.person))
        }
    }

    /**
     * This can be used to see and compare
     * different fonts presented via Font.getFamilies()
     */
    @Suppress("unused")
    private fun printFontDemos(vBox: VBox) {
        val fonts = Font.getFamilies()

        for (it in fonts) {
            println(it)

            vBox += button(it) {
                style {
                    fontFamily = it
                }
            }
        }
    }
}