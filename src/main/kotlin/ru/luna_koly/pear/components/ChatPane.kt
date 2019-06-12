package ru.luna_koly.pear.components

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import ru.luna_koly.pear.components.more.MessagePane
import ru.luna_koly.pear.components.more.OverlayedScrollPane
import ru.luna_koly.pear.components.more.overlayedscrollpane
import ru.luna_koly.pear.logic.Message
import ru.luna_koly.pear.logic.Person
import ru.luna_koly.pear.logic.Profile
import ru.luna_koly.pear.logic.ProfileConnector
import tornadofx.*

/**
 * A view for sending messages to
 * a profile
 *
 * TODO: make it a View
 */
class ChatPane : BorderPane() {
    private var topBar: HBox by singleAssign()
    private var name: Label by singleAssign()
    private var settings: Label by singleAssign()

    private var content: OverlayedScrollPane by singleAssign()
    private var currentMessages: VBox by singleAssign()

    private var userInput: TextField by singleAssign()

    private var protector: Label by singleAssign()

    /**
     * If true means the protector has not
     * been hidden yet and that needs to be done
     */
    private var isProtectorVisible = true

    /**
     * The ProfileConnector the user
     * is interacting with
     */
    var selectedConnector: ProfileConnector? = null

    init {
        addClass(ChatPaneStyle.chatPane)

        topBar = hbox {
            addClass(ChatPaneStyle.topBar)

            name = label("Nobody") {
                addClass(ChatPaneStyle.topBarButton)
            }

            spacer()

            settings = label("  #  ") {
                addClass(ChatPaneStyle.topBarButton)
            }
        }

        content = overlayedscrollpane {
            content {
                isFitToWidth = true

                currentMessages = vbox {
                    alignment = Pos.CENTER
                    spacing = 5.0
                }
            }
        }

        userInput = textfield {
            addClass(ChatPaneStyle.messageField)

            promptText = "Type..."

            action {
                if (text.isNotBlank()) {
                    selectedConnector?.sendMessage(text)
                    text = ""
                }
            }
        }

        protector = label("Stay focused.") {
            addClass(ChatPaneStyle.protector)

            anchorpaneConstraints {
                topAnchor = 0.0
                leftAnchor = 0.0
                rightAnchor = 0.0
                bottomAnchor = 0.0
            }
        }

        center = protector
    }

    /**
     * Updates UI according to the
     * profile info changes
     */
    fun update(person: Person) {
        name.text = person.name
    }

    /**
     * Updates titles for the new profile.
     * Does not refresh messages
     *
     * If ChatPane was a View it could also
     * refresh messages automatically
     */
    fun selectConnector(profileConnector: ProfileConnector) {
        name.text = profileConnector.profile.name
        selectedConnector = profileConnector

        if (isProtectorVisible) {
            top = topBar
            center = content
            bottom = userInput
            isProtectorVisible = false
        }
    }

    /**
     * Updates visible messages list
     */
    fun refresh(messages: List<Message>) {
        currentMessages.children.clear()

        for (it in messages)
            currentMessages += MessagePane(it)
    }

    /**
     * Performed whenever the settings button
     * is pressed.
     *
     * Could be removed if ChatPane was a View
     */
    fun settings(op: () -> Unit) {
        settings.setOnMouseClicked { op() }
    }

    /**
     * Performed whenever the profile name
     * is pressed and the current profile is
     * presented
     *
     * Could be removed if ChatPane was a View
     */
    fun profileInfo(op: (Profile) -> Unit) {
        name.setOnMouseClicked {
            selectedConnector?.profile?.let {
                op(it)
            }
        }
    }
}