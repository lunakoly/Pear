package ru.luna_koly.pear.ui

import javafx.beans.property.SimpleStringProperty
import ru.luna_koly.pear.Logger
import ru.luna_koly.pear.events.ConnectionEstablishedEvent
import ru.luna_koly.pear.events.DataReceivedEvent
import tornadofx.*

class TerminalView : View() {
    private val controller: TerminalController by inject()
    private val inputText = SimpleStringProperty()

    private val userOutput = textarea {
        isEditable = false
    }

    private val userInput = textfield(inputText) {
        action {
            log("$ " + (inputText.value ?: ""))
            controller.proceed(inputText.value ?: "")
            inputText.value = ""
        }

        style {
            promptText = "$ connect <ip>"
        }
    }

    override val root = borderpane {
        center = userOutput
        bottom = userInput
    }

    init {
        subscribe<ConnectionEstablishedEvent> {
            Logger.log("UI", "Connection Established with ${it.connection}")
        }

        subscribe<DataReceivedEvent> {
            Logger.log("UI", "Data received: `${it.connection.readString()}`")
        }
    }

    fun log(message: String) {
        userOutput.text += message + '\n'
    }
}